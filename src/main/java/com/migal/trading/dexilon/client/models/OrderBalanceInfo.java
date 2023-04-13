package com.migal.trading.dexilon.client.models;

import java.math.BigDecimal;

public record OrderBalanceInfo(String symbol, BigDecimal lockedAsk, BigDecimal lockedBid, BigDecimal sumSizeAsk, BigDecimal sumSizeBid) {}
