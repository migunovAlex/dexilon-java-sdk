package com.migal.trading.dexilon.client;

import com.migal.trading.dexilon.client.models.AvailableSymbol;
import com.migal.trading.dexilon.client.models.OrderBookInfo;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DexilonHttpClient {

    @GetExchange("/symbols")
    Flux<AvailableSymbol> getAllSymbols();

    @GetExchange("/orders/book")
    Mono<OrderBookInfo> getOrderBook(@RequestParam String symbol);

}
