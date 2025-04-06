package com.example.musinsa.singleton;

import lombok.Getter;

@Getter
public class CustomOrder implements Comparable<CustomOrder> {
    int price;
    String brand;

    public CustomOrder(String raw) {
        String[] parts = raw.split("/");
        this.price = Integer.parseInt(parts[0]);
        this.brand = parts[1];
    }

    @Override
    public int compareTo(CustomOrder c) {
        int priceCompare = Integer.compare(this.price, c.price);
        if (priceCompare != 0)
            return priceCompare;
        return c.brand.compareTo(this.brand);
    }

    @Override
    public String toString() {
        return price + "/" + brand;
    }
}