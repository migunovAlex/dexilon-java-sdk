package com.migal.trading.dexilon.client.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AvailableSymbol {

    private String symbol;
    private BigDecimal lastPrice;
    private BigDecimal volume;
    private BigDecimal price24Percentage;

}
