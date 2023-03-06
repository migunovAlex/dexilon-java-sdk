package com.migal.trading.dexilon.client.models;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class AccountInfoResponse {

    private BigDecimal upl;
    private BigDecimal equity;
    private Integer feeTierStructure;
    private Integer feeTierDiscount;
    private String tradeFeeAsset;

    private List<AssetInfo> assets;
    private List<PositionInfo> positions;
    private List<OrderBalanceInfo> orders;

}
