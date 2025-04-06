package com.example.ktop_food_app_store.model.data.entity;

public class TopSellingItem {
    private String name;
    private int quantity;
    private String imageUrl;

    public TopSellingItem(String name, int quantity, String imageUrl) {
        this.name = name;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
