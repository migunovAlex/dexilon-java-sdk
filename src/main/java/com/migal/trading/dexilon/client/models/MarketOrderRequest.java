package com.migal.trading.dexilon.client.models;

import java.math.BigDecimal;

public record MarketOrderRequest(String clientOrderId, String symbol, String side, BigDecimal size) {}
