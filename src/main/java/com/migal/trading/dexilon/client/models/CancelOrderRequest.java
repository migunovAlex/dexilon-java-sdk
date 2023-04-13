package com.migal.trading.dexilon.client.models;

public record CancelOrderRequest(String symbol, Integer orderId, String clientOrderId) { }
