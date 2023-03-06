package com.migal.trading.dexilon.client.models;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderInfo {

    private Long orderId;
    private String clientOrderId;
    private String symbol;
    private BigDecimal amount;
    private BigDecimal price;
    private BigDecimal filled;
    private BigDecimal averageExecutionPrice;
    private String type;
    private String side;
    private String status;
    private Long updatedAt;

}