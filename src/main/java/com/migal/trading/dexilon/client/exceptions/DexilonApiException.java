package com.migal.trading.dexilon.client.exceptions;

public class DexilonApiException extends RuntimeException{
    public DexilonApiException() {
    }

    public DexilonApiException(String message) {
        super(message);
    }
}
