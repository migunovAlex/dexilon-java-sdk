package com.migal.trading.dexilon.client;

import org.junit.jupiter.api.BeforeAll;

public class ITTestBase {

    static String testMetamaskAddress;
    static String testPrivateKey;

    DexilonClientImpl testInstance = new DexilonClientImpl(testMetamaskAddress, testPrivateKey);

    @BeforeAll
    public static void setUp() {
        testMetamaskAddress = System.getenv("TEST_METAMASK_ADDRESS");
        testPrivateKey = System.getenv("TEST_PRIVATE_KEY");
    }

}
