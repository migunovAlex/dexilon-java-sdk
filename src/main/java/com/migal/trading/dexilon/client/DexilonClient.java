package com.migal.trading.dexilon.client;

public class DexilonClient {

    private final DexilonHttpClient dexilonHttpClient;
    private final DexilonChainClient dexilonChainClient;

    public DexilonClient(DexilonHttpClient dexilonHttpClient, DexilonChainClient dexilonChainClient) {
        this.dexilonHttpClient = dexilonHttpClient;
        this.dexilonChainClient = dexilonChainClient;
    }
}
