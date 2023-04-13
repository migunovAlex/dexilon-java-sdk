package com.migal.trading.dexilon.client.models;

import java.util.List;
public record OrderBookInfo(List<OrderBook> ask, List<OrderBook> bid) {}
