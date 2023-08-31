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
        if (size == 0) {
            mapToUpdate.remove(price);
            return;
        }

        mapToUpdate.put(price, size);
    }

    public String getBest(String type) {
        TreeMap<Long, Long> map = type.equals("best_bid") ? bids : asks;
        long price = map.firstKey();
        long size = map.get(price);

        return price + "," + size;
    }

    public String getSizeAtPrice(long price) {
        long bidSize = bids.getOrDefault(price, 0L);
        long askSize = asks.getOrDefault(price, 0L);

        if (bidSize == 0) {
            return String.valueOf(askSize);
        } else {
            return String.valueOf(bidSize);
        }
    }

    public void processMarketOrder(String[] order) {
        long orderSize = Long.parseLong(order[2]);
        String type = order[1];

        TreeMap<Long, Long> map = type.equals("buy") ? asks : bids;

        while (orderSize > 0) {
            long best = getBestPrice(type);
            long availableSize = map.get(best);

            if (availableSize <= orderSize) {
                map.remove(best);
                orderSize -= availableSize;
            } else {
                map.put(best, availableSize - orderSize);
                orderSize = 0;
            }
        }
    }

    public long getBestPrice(String type) {
        TreeMap<Long, Long> map = type.equals("sell") ? bids : asks;
        return map.firstKey();
    }
}

class MarketSimulator {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("input.txt"));
        BufferedWriter wr = new BufferedWriter(new FileWriter("output.txt"));
        OrderBook orderBook = new OrderBook();
        String line;

        while ((line = br.readLine()) != null) {
            String[] order = splitLine(line);
            String type = order[0];

            if (type.equals("u")) {
                orderBook.updateOrder(order);
            } else if (type.equals("q")) {
                String queryType = order[1];
                String result = "";

                if (queryType.equals("best_bid") || queryType.equals("best_ask")) {
                    result = orderBook.getBest(queryType);
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
        br.close();
    }

    private static String[] splitLine(String line) {
        int start = 0;
        int end;
        int index = 0;
        String[] result = new String[4];
        while ((end = line.indexOf(',', start)) != -1) {
            result[index++] = line.substring(start, end);
            start = end + 1;
        }
        result[index] = line.substring(start);
        return result;
    }
}