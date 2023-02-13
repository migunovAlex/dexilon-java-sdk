package com.migal.trading.dexilon.client.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DexilonBlockchainAddressMapping {

    private int chainId;
    private String address;
    private String cosmosAddress;

}
