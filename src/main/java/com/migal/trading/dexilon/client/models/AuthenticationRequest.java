package com.migal.trading.dexilon.client.models;

public record AuthenticationRequest(String ethAddress, String nonce, String signedNonce) {}
