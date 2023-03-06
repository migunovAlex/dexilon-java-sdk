package com.migal.trading.dexilon.client.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
public class MarketOrderRequest {

    private final String clientOrderId;
    private final String symbol;
    private final String side;
    private final BigDecimal size;

}
