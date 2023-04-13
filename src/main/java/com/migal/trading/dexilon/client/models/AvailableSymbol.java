package com.migal.trading.dexilon.client.models;

import java.math.BigDecimal;

public record AvailableSymbol(String symbol, BigDecimal lastPrice, BigDecimal volume, BigDecimal price24Percentage) { }
