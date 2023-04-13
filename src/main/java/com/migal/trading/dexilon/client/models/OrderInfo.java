package com.migal.trading.dexilon.client.models;

import java.math.BigDecimal;

public record OrderInfo(Long orderId, String clientOrderId, String symbol, BigDecimal amount, BigDecimal price, BigDecimal filled, BigDecimal averageExecutionPrice, String type, String side, String status, Long updatedAt) {}