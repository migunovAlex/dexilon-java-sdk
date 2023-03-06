package com.migal.trading.dexilon.client.models;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class LimitOrderRequest extends MarketOrderRequest {

    private final BigDecimal price;

    public LimitOrderRequest(String clientOrderId, String symbol, String side, BigDecimal size, BigDecimal price) {
        super(clientOrderId, symbol, side, size);
        this.price = price;
    }
}
