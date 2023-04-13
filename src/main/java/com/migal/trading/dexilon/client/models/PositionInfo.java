package com.migal.trading.dexilon.client.models;

import java.math.BigDecimal;

public record PositionInfo(String symbol, String marginMode, BigDecimal amount, BigDecimal basePrice, BigDecimal liquidationPrice, BigDecimal markPrice, BigDecimal upl, Integer uplPercentage, BigDecimal lockedBalance, Integer leverage) {}
