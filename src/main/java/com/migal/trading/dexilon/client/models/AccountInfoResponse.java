package com.migal.trading.dexilon.client.models;

import java.math.BigDecimal;
import java.util.List;

public record AccountInfoResponse (BigDecimal upl, BigDecimal equity, Integer feeTierStructure, Integer feeTierDiscount, String tradeFeeAsset, List<AssetInfo> assets, List<PositionInfo> positions, List<OrderBalanceInfo> orders) {}