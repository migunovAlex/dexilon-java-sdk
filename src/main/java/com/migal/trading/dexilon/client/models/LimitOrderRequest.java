package com.migal.trading.dexilon.client.models;

import java.math.BigDecimal;

public record LimitOrderRequest (String clientOrderId, String symbol, String side, BigDecimal size, BigDecimal price) {}
