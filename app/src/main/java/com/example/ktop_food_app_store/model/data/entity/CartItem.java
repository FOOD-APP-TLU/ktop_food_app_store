package com.example.ktop_food_app_store.model.data.entity;


import java.io.Serializable;

public class CartItem implements Serializable {
    private String foodId;
    private String imagePath;
    private String name;
    private double price;
    private int quantity;
    private double totalPrice;

    // Constructor mặc định (yêu cầu bởi Firebase)
    public CartItem() {}

    public CartItem(String foodId, String imagePath, String name, double price, int quantity, double totalPrice) {
        this.foodId = foodId;
        this.imagePath = imagePath;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Getters và Setters
    public String getFoodId() { return foodId; }
    public void setFoodId(String foodId) { this.foodId = foodId; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}
