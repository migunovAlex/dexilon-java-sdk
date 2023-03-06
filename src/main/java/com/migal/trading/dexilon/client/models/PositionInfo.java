package com.migal.trading.dexilon.client.models;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PositionInfo {

    private String symbol;
    private String marginMode;
    private BigDecimal amount;
    private BigDecimal basePrice;
    private BigDecimal liquidationPrice;
    private BigDecimal markPrice;
    private BigDecimal upl;
    private Integer uplPercentage;
    private BigDecimal lockedBalance;
    private Integer leverage;

}
