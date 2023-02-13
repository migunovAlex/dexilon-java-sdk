package com.migal.trading.dexilon.client.models;

import lombok.Getter;

@Getter
public class AuthenticationResponse {

    private String accessToken;
    private String refreshToken;

}
