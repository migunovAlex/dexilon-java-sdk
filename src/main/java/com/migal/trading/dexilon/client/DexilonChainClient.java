package com.migal.trading.dexilon.client;

import com.migal.trading.dexilon.client.models.DexilonBlockchainAddressMappingInfo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Mono;

public interface DexilonChainClient {

    @GetExchange("/registration/address_mapping/mirror/{ethAddress}")
    Mono<DexilonBlockchainAddressMappingInfo> getCosmosAddressMapping(@PathVariable String ethAddress);

}
