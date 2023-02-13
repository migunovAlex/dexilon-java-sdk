package com.migal.trading.dexilon.client.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderBookInfo {
    private List<OrderBook> ask;
    private List<OrderBook> bid;
}
