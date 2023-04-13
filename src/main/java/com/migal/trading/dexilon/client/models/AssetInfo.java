package com.migal.trading.dexilon.client.models;

import java.math.BigDecimal;

public record AssetInfo (String name, BigDecimal deposited, BigDecimal margin, BigDecimal locked, Boolean isMargin) { }
