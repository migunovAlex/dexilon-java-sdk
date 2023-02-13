package com.migal.trading.dexilon.client;

import com.migal.trading.dexilon.client.models.AvailableSymbol;
import com.migal.trading.dexilon.client.models.OrderBookInfo;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TradingITTest {

    private final DexilonClientImpl testInstance = new DexilonClientImpl();

    @Test
    public void shouldReceiveAllSymbols() {
        Set<AvailableSymbol> symbolsResponse = testInstance.getAllSymbols();
        assertNotNull(symbolsResponse);
        assertTrue(symbolsResponse.size() > 0);
    }

    @Test
    public void shouldReceiveOrderBook(){
        Optional<OrderBookInfo> orderBookInfo = testInstance.getOrderBook("sol_usdt");
        assertTrue(orderBookInfo.isPresent());
    }

}