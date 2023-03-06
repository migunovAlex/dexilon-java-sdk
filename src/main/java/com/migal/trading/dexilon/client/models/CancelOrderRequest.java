package com.migal.trading.dexilon.client.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CancelOrderRequest {

    private final String symbol;
    private final Integer orderId;
    private final String clientOrderId;



}
