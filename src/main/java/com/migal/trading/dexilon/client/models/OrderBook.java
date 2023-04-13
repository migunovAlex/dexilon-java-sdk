package com.migal.trading.dexilon.client.models;

import java.math.BigDecimal;

public record OrderBook(BigDecimal price, BigDecimal size, BigDecimal sum) {}
