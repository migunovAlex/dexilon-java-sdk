package com.migal.trading.dexilon.client;

import com.migal.trading.dexilon.client.exceptions.DexilonApiException;
import com.migal.trading.dexilon.client.models.*;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DexilonClientImpl {

    private static final String DEXILON_BLOCKCHAIN_CONTEXT = "/dexilon-exchange/dexilonl2/";
    private static final int AUTH_TOKEN_EXPIRATION_TIMEOUT_IN_MINUTES = 10;

    private final RestTemplate restTemplate = new RestTemplate();

    private String apiUrl = "https://api.staging.dexilon.io/api/v1/";
    private String dexilonChainUrl = "https://proxy.staging.dexilon.io/";

    private final Optional<String> ethAddress;
    private final Optional<String> privateKey;

    private HttpHeaders headers = new HttpHeaders();

    private boolean isAuthenticated;
    private String refreshToken;
    private LocalDateTime lastAuthenticationAttemptTime;

    public DexilonClientImpl() {
        this.ethAddress = Optional.empty();
        this.privateKey = Optional.empty();
        configureRestTemplate();
    }

    public DexilonClientImpl(String ethAddress, String privateKey) {
        this.ethAddress = Optional.ofNullable(ethAddress);
        this.privateKey = Optional.ofNullable(privateKey);
        configureRestTemplate();
    }

    public DexilonClientImpl(String apiUrl, String dexilonChainUrl, String ethAddress, String privateKey) {
        this.ethAddress = Optional.ofNullable(ethAddress);
        this.privateKey = Optional.ofNullable(privateKey);
        this.apiUrl = apiUrl;
        this.dexilonChainUrl = dexilonChainUrl;
        configureRestTemplate();
    }

    private void configureRestTemplate() {
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        this.restTemplate.setMessageConverters(messageConverters);
    }

    public Set<AvailableSymbol> getAllSymbols() {
        HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
        ResponseEntity<AvailableSymbol[]> response = restTemplate.exchange(apiUrl + "/symbols", HttpMethod.GET, httpEntity, AvailableSymbol[].class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new DexilonApiException("Dexilon API response code: " + response.getStatusCode());
        }
        return Set.of(Objects.requireNonNull(response.getBody()));
    }


    public Optional<OrderBookInfo> getOrderBook(String symbol) {
        OrderBookInfo response = restTemplate.getForObject(apiUrl + "/orders/book?symbol=" + symbol, OrderBookInfo.class);
        return Optional.ofNullable(response);
    }

    Optional<DexilonBlockchainAddressMappingInfo> getCosmosAddressMapping() {
        checkCredsWereProvided();
        try {
            DexilonBlockchainAddressMappingInfo response = restTemplate.getForObject(dexilonChainUrl + DEXILON_BLOCKCHAIN_CONTEXT + "/registration/address_mapping/mirror/" + this.ethAddress.get(), DexilonBlockchainAddressMappingInfo.class);
            return Optional.ofNullable(response);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                 return Optional.empty();
            }
        } catch (Exception e) {
            throw new DexilonApiException("Received error from service. Message: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<AuthenticationResponse> authenticate() {
        checkCredsWereProvided();
        Optional<DexilonBlockchainAddressMappingInfo> cosmosAddressMapping = getCosmosAddressMapping();
        if (cosmosAddressMapping.isEmpty()) {
            throw new DexilonApiException("Was not able to receive Dexilon address mapping for provided eth address");
        }

        String nonce = generateNonce(cosmosAddressMapping.get().getAddressMapping().getCosmosAddress());
        byte[] keccakNonce = hashWithKeccak(nonce);
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey.get()));
        Sign.SignatureData signature = Sign.signPrefixedMessage(keccakNonce, ecKeyPair);
        byte[] signedBytes = bytesFromSignature(signature);
        String signedMessage = Numeric.toHexString(signedBytes);

        try {
            HttpEntity<AuthenticationRequest> httpEntity = new HttpEntity<>(new AuthenticationRequest(ethAddress.get(), nonce, signedMessage), headers);
            ResponseEntity<AuthenticationResponse> result = restTemplate.postForEntity(apiUrl + "/auth/accessToken", httpEntity, AuthenticationResponse.class);
            if (result.getStatusCode().equals(HttpStatus.OK)) {
                isAuthenticated = true;
                AuthenticationResponse authenticationResponse = result.getBody();
                if (authenticationResponse != null) {
                    this.refreshToken = authenticationResponse.getRefreshToken();
                    this.lastAuthenticationAttemptTime = LocalDateTime.now();
                    headers.add("Authorization", "Bearer " + authenticationResponse.getAccessToken());
                    headers.add("CosmosAddress", cosmosAddressMapping.get().getAddressMapping().getCosmosAddress());
                    return Optional.ofNullable(result.getBody());
                }
                return Optional.empty();
            }
            throw new DexilonApiException("Error code returned on authentication request: " + result.getStatusCode());
        } catch (Exception e) {
            throw new DexilonApiException("Error while trying to authenticate: " + e.getMessage());
        }
    }

    private byte[] hashWithKeccak(String nonce) {
        return Hash.sha3(Numeric.hexStringToByteArray(Numeric.toHexString(nonce.getBytes())));
    }

    private String generateNonce(String dexilonAddress) {
        long currentMilliseconds = ZonedDateTime.now().toInstant().toEpochMilli();
        return currentMilliseconds + "#" + dexilonAddress;
    }

    byte[] bytesFromSignature(Sign.SignatureData signature)
    {
        byte[] sigBytes = new byte[65];
        Arrays.fill(sigBytes, (byte) 0);
        try
        {
            System.arraycopy(signature.getR(), 0, sigBytes, 0, 32);
            System.arraycopy(signature.getS(), 0, sigBytes, 32, 32);
            System.arraycopy(signature.getV(), 0, sigBytes, 64, 1);
        }
        catch (IndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }

        return sigBytes;
    }

    private void checkCredsWereProvided() {
        if (ethAddress.isEmpty() || privateKey.isEmpty()) {
            throw new DexilonApiException("You are trying to call method which suppose to have ethAddress and private key initialized");
        }
    }

    public Optional<OrderInfo> submitMarketOrder(MarketOrderRequest marketOrderRequest) {
        return requestWithAuth(marketOrderRequest, HttpMethod.POST, "orders/market", OrderInfo.class);
    }

    public Optional<OrderInfo> submitLimitOrder(LimitOrderRequest limitOrderRequest) {
        return requestWithAuth(limitOrderRequest, HttpMethod.POST, "/orders/limit", OrderInfo.class);
    }

    private void checkAuthIsOk() {
        if(!isAuthenticated) {
            Optional<AuthenticationResponse> authenticate = authenticate();
            if (authenticate.isEmpty()) {
                throw new DexilonApiException("You are not authorized to use this endpoint");
            }
        } else {
            if (!checkTokenIsNotExpiredOrRefresh()) {
                throw new DexilonApiException("Authorization token has been expired and was not able to successfully refresh it");
            }
        }
    }

    private boolean checkTokenIsNotExpiredOrRefresh() {
        long minutes = ChronoUnit.MINUTES.between(lastAuthenticationAttemptTime, LocalDateTime.now());
        if (minutes < AUTH_TOKEN_EXPIRATION_TIMEOUT_IN_MINUTES) return true;
        return refreshToken();
    }

    private boolean refreshToken() {

        //TODO refresh token and update header
        return true;
    }

    public Optional<OrderInfo> cancelOrder(String symbol, @Nullable  Long orderId, @Nullable String clientOrderId) {
        checkAuthIsOk();

        try {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(apiUrl + "/orders");
            Map<String, Object> params = new HashMap<>();

            uriComponentsBuilder.queryParam("symbol", "{symbol}");
            params.put("symbol", symbol);

            if (orderId != null) {
                uriComponentsBuilder.queryParam("orderId", "{orderId}");
                params.put("orderId", orderId);
            }
            if (clientOrderId != null) {
                uriComponentsBuilder.queryParam("clientOrderId", "{clientOrderId}");
                params.put("clientOrderId", clientOrderId);
            }

            String requestUrl = uriComponentsBuilder.encode().toUriString();
            HttpEntity<MarketOrderRequest> httpEntity = new HttpEntity<>(null, headers);
            ResponseEntity<OrderInfo> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, httpEntity, OrderInfo.class, params);
            if (response.getStatusCode() == HttpStatus.OK) {
                return Optional.ofNullable(response.getBody());
            }
        } catch (Exception e) {
            throw new DexilonApiException(e.getMessage());
        }

        return Optional.empty();
    }

    public List<OrderInfo> cancelAllOpenOrders() {
        Optional<OrderInfo[]> canceledOrders = requestWithAuth(null, HttpMethod.DELETE, "/orders/all", OrderInfo[].class);
        return canceledOrders.map(Arrays::asList).orElseGet(List::of);
    }

    public Optional<LeverageUpdateResponse> setLeverage(String symbol, Integer leverage) {
        LeverageUpdateRequest leverageUpdateRequest = LeverageUpdateRequest.builder().symbol(symbol).leverage(leverage).build();
        return requestWithAuth(leverageUpdateRequest, HttpMethod.PUT, "/accounts/leverage", LeverageUpdateResponse.class);
    }

    public Optional<AccountInfoResponse> getAccountInfo() {
        return requestWithAuth(null, HttpMethod.GET, "/accounts", AccountInfoResponse.class);
    }

    private<T, I> Optional<T> requestWithAuth(I requestObject, HttpMethod method, String path, Class<T> responseClass) {
        checkAuthIsOk();

        try{
            HttpEntity<I> httpEntity = new HttpEntity<>(requestObject, headers);
            ResponseEntity<T> response = restTemplate.exchange(apiUrl + path, method, httpEntity, responseClass);
            if (response.getStatusCode() == HttpStatus.OK) {
                return Optional.ofNullable(response.getBody());
            }
        } catch(Exception e) {
            throw new DexilonApiException(e.getMessage());
        }
        return Optional.empty();
    }
}
