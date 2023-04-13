package com.migal.trading.dexilon.client;

import com.migal.trading.dexilon.client.models.AvailableSymbol;
import com.migal.trading.dexilon.client.models.OrderBookInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ITTestsConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class PublicEndpointsITTest {

    @Autowired
    private DexilonHttpClient testInstance;


    @Test
    public void shouldReceiveAllSymbols() {
        Flux<AvailableSymbol> symbolsResponse = testInstance.getAllSymbols();
        assertNotNull(symbolsResponse);
        List<AvailableSymbol> result = symbolsResponse.toStream().toList();
        assertTrue(result.size() > 0);
    }

    @Test
    public void shouldReceiveOrderBook(){
        Mono<OrderBookInfo> orderBookInfo = testInstance.getOrderBook("sol_usdt");
        OrderBookInfo result = orderBookInfo.block();
        assertNotNull(result);
    }

}