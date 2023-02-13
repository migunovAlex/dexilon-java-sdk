package com.migal.trading.dexilon.client;

import com.migal.trading.dexilon.client.exceptions.DexilonApiException;
import com.migal.trading.dexilon.client.models.*;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.time.ZonedDateTime;
import java.util.*;

public class DexilonClientImpl {

    private static final String DEXILON_BLOCKCHAIN_CONTEXT = "/dexilon-exchange/dexilonl2/";

    private final RestTemplate restTemplate = new RestTemplate();

    private String apiUrl = "https://api.staging.dexilon.io/api/v1/";
    private String dexilonChainUrl = "https://proxy.staging.dexilon.io/";

    private final Optional<String> ethAddress;
    private final Optional<String> privateKey;

    private HttpHeaders headers = new HttpHeaders();

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
                return Optional.ofNullable(result.getBody());
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
//        return currentMilliseconds + "#" + dexilonAddress;
        return 1000 + "#" + dexilonAddress;
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
}
