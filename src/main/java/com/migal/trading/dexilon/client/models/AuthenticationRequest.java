package com.migal.trading.dexilon.client.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AuthenticationRequest {

    private final String ethAddress;
    private final String nonce;
    private final String signedNonce;

}
