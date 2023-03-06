package com.migal.trading.dexilon.client.models;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AssetInfo {

    private String name;
    private BigDecimal deposited;
    private BigDecimal margin;
    private BigDecimal locked;
    private Boolean isMargin;

}
