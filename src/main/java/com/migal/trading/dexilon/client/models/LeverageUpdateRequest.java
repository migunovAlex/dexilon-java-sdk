package com.migal.trading.dexilon.client.models;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class LeverageUpdateRequest {

    private final String symbol;
    private final Integer leverage;

}
