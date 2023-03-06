package com.migal.trading.dexilon.client.models;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderBalanceInfo {

    private String symbol;
    private BigDecimal lockedAsk;
    private BigDecimal lockedBid;
    private BigDecimal sumSizeAsk;
    private BigDecimal sumSizeBid;

}
