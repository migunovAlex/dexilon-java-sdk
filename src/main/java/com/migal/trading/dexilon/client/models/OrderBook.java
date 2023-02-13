package com.migal.trading.dexilon.client.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderBook {

    private BigDecimal price;
    private BigDecimal size;
    private BigDecimal sum;

}
