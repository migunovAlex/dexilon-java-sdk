package com.migal.trading.dexilon.client;

import com.migal.trading.dexilon.client.exceptions.DexilonApiException;
import com.migal.trading.dexilon.client.models.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TradingFunctionalityITTest extends ITTestBase {


    @Test
    public void shouldCreateMarketOrder() {
        MarketOrderRequest marketOrderRequest = new MarketOrderRequest(UUID.randomUUID().toString(), "eth_usdt", "BUY", BigDecimal.valueOf(0.10));
        Optional<OrderInfo> response = testInstance.submitMarketOrder(marketOrderRequest);
        assertTrue(response.isPresent());
    }

    @Test
    public void shouldCreateLimitOrder() {
        LimitOrderRequest limitOrderRequest = new LimitOrderRequest(UUID.randomUUID().toString(), "eth_usdt", "SELL", BigDecimal.valueOf(0.1), BigDecimal.valueOf(1750.00));
        Optional<OrderInfo> response = testInstance.submitLimitOrder(limitOrderRequest);
        assertTrue(response.isPresent());
        assertNotNull(response.get().getPrice());
    }

    @Test
    public void shouldCreateMarketOrderWithRejectedState() {
        MarketOrderRequest marketOrderRequest = new MarketOrderRequest(UUID.randomUUID().toString(), "eth_usdt", "BUY", BigDecimal.valueOf(1000000000.0));

        DexilonApiException apiException = assertThrows(DexilonApiException.class, () -> {
            testInstance.submitMarketOrder(marketOrderRequest);
        });
        String actualMessage = apiException.getMessage();
        assertTrue(actualMessage.contains("NEW_ORDER_REJECTED"));
        assertTrue(actualMessage.contains("The maximum market order's notional is"));
    }

    @Test
    public void shouldCreateLimitOrderWithRejectedState() {
        LimitOrderRequest limitOrderRequest = new LimitOrderRequest(UUID.randomUUID().toString(), "eth_usdt", "BUY", BigDecimal.valueOf(1000000000.0), BigDecimal.valueOf(1250.00));
        DexilonApiException apiException = assertThrows(DexilonApiException.class, () -> testInstance.submitLimitOrder(limitOrderRequest));

        String actualMessage = apiException.getMessage();
        assertTrue(actualMessage.contains("NEW_ORDER_REJECTED"));
        assertTrue(actualMessage.contains("The maximum available to buy is"));
    }

    @Test
    public void shouldThrowErrorOnAlreadyExecutedOrderToCancel() {
        DexilonApiException apiException = assertThrows(DexilonApiException.class, () -> testInstance.cancelOrder("eth_usdt", 1000L, UUID.randomUUID().toString()));

        String actualMessage = apiException.getMessage();
        assertEquals("400 : \"{\"code\":2002,\"name\":\"CANCEL_REJECTED\",\"details\":[\"Order has been executed\"]}\"", actualMessage);
    }

    @Test
    public void shouldThrowErrorOnWrongOrderToCancel() {
        DexilonApiException apiException = assertThrows(DexilonApiException.class, () -> testInstance.cancelOrder("eth_usdt", Long.MAX_VALUE, UUID.randomUUID().toString()));

        String actualMessage = apiException.getMessage();
        assertEquals("400 : \"{\"code\":2002,\"name\":\"CANCEL_REJECTED\",\"details\":[\"Order has been executed\"]}\"", actualMessage);
    }

    @Test
    public void shouldCreateOrderAndSuccessfullyCancelIt() {
        LimitOrderRequest limitOrderRequest = new LimitOrderRequest(UUID.randomUUID().toString(), "eth_usdt", "SELL", BigDecimal.valueOf(0.1), BigDecimal.valueOf(1750.00));
        Optional<OrderInfo> response = testInstance.submitLimitOrder(limitOrderRequest);

        assertTrue(response.isPresent());

        Long orderId = response.get().getOrderId();

        Optional<OrderInfo> cancelOrderResult = testInstance.cancelOrder("eth_usdt", orderId, null);

        assertTrue(cancelOrderResult.isPresent());

        assertEquals("CANCELED", cancelOrderResult.get().getStatus());
    }

    @Test
    public void shouldGetAccountInfo() {
        Optional<AccountInfoResponse> accountInfo = testInstance.getAccountInfo();
        assertTrue(accountInfo.isPresent());
    }

    @Test
    public void shouldSetLeverageSuccessfully() {
        Optional<LeverageUpdateResponse> leverageResponse = testInstance.setLeverage("eth_usdt", 5);
        assertTrue(leverageResponse.isPresent());
        Integer leverage = leverageResponse.get().getLeverage();
        assertEquals(Integer.valueOf(5), leverage);
    }

}
