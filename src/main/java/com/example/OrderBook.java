package com.example;

import java.io.*;
import java.util.*;

public class OrderBook {
    TreeMap<Long, Long> bids;
    TreeMap<Long, Long> asks;

    public OrderBook() {
        bids = new TreeMap<>(Collections.reverseOrder());
        asks = new TreeMap<>();
    }

    public void updateOrder(String[] order) {
        long price = Long.parseLong(order[1]);
        long size = Long.parseLong(order[2]);
        String type = order[3];

        TreeMap<Long, Long> mapToUpdate = type.equals("bid") ? bids : asks;
        mapToUpdate.put(price, size);
    }

    public String getBestBid() {
        long price = bids.firstKey();
        long size = bids.get(price);

        return price + "," + size;
    }

    public String getBestAsk() {
        long price = asks.firstKey();
        long size = asks.get(price);

        return price + "," + size;
    }

    public String getSizeAtPrice(long price) {
        long bidSize = bids.getOrDefault(price, 0L);
        long askSize = asks.getOrDefault(price, 0L);

        if (bidSize == 0 && askSize == 0) {
            return "0";
        }

        return (bidSize + askSize) + "";
    }

    public void processMarketOrder(String[] order) {
        long size = Long.parseLong(order[2]);
        String type = order[1];

        if (type.equals("buy")) {
            while (!asks.isEmpty() && size > 0) {
                long lowestAsk = asks.firstKey();
                long availableSize = asks.get(lowestAsk);

                if (availableSize <= size) {
                    asks.remove(lowestAsk);
                    size -= availableSize;
                } else {
                    asks.put(lowestAsk, availableSize - size);
                    size = 0;
                }
            }
        } else if (type.equals("sell")) {
            while (!bids.isEmpty() && size > 0) {
                long highestBid = bids.firstKey();
                long availableSize = bids.get(highestBid);

                if (availableSize <= size) {
                    bids.remove(highestBid);
                    size -= availableSize;
                } else {
                    bids.put(highestBid, availableSize - size);
                    size = 0;
                }
            }
        }
    }
}

class MarketSimulator {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("input.txt"));
        BufferedWriter wr = new BufferedWriter(new FileWriter("output.txt"));
        OrderBook orderBook = new OrderBook();
        String line;

        while ((line = br.readLine()) != null) {
            String[] order = line.split(",");
            String type = order[0];

            if (type.equals("u")) {
                orderBook.updateOrder(order);
            } else if (type.equals("q")) {
                String queryType = order[1];
                String result = "";

                if (queryType.equals("best_bid")) {
                    result = orderBook.getBestBid();
                } else if (queryType.equals("best_ask")) {
                    result = orderBook.getBestAsk();
                } else if (queryType.equals("size")) {
                    long price = Long.parseLong(order[2]);
                    result = orderBook.getSizeAtPrice(price);
                }

                wr.write(result);
                wr.newLine();
            } else if (type.equals("o")) {
                orderBook.processMarketOrder(order);
            }
        }

        wr.close();
    }
}
