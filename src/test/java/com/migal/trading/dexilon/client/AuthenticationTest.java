package com.migal.trading.dexilon.client;

import com.migal.trading.dexilon.client.exceptions.DexilonApiException;
import com.migal.trading.dexilon.client.models.AuthenticationResponse;
import com.migal.trading.dexilon.client.models.DexilonBlockchainAddressMappingInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest {

    private static String testMetamaskAddress;
    private static String testPrivateKey;

    private DexilonClientImpl testInstance = new DexilonClientImpl(testMetamaskAddress, testPrivateKey);

    @BeforeAll
    public static void setUp() {
        testMetamaskAddress = System.getenv("TEST_METAMASK_ADDRESS");
        testPrivateKey = System.getenv("TEST_PRIVATE_KEY");
    }

    @Test
    public void shouldThrowExceptionTryingToCallCosmosMappingMethodWithoutCreds() {
        testInstance = new DexilonClientImpl();
        DexilonApiException apiException = assertThrows(DexilonApiException.class, () -> {
            testInstance.getCosmosAddressMapping();
        });
        String expectedMessage = "You are trying to call method which suppose to have ethAddress and private key initialized";
        String actualMessage = apiException.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldGetCosmosMappingSuccessfully() {
        Optional<DexilonBlockchainAddressMappingInfo> cosmosAddress = testInstance.getCosmosAddressMapping();
        assertTrue(cosmosAddress.isPresent());
    }

    @Test
    public void shouldGetEmptyIfThereIsNoMapping() {
        testInstance = new DexilonClientImpl(testMetamaskAddress + "_WRONG", testPrivateKey);
        Optional<DexilonBlockchainAddressMappingInfo> cosmosAddress = testInstance.getCosmosAddressMapping();
        assertTrue(cosmosAddress.isEmpty());
    }

    @Test
    public void shouldAuthenticate() {
        Optional<AuthenticationResponse> result = testInstance.authenticate();
        assertTrue(result.isPresent());
    }
}