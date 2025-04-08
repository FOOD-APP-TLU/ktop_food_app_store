package com.example.ktop_food_app_store.model.data.entity;

import java.io.Serializable;

public class Food implements Serializable {
    private String foodId;
    private String title;
    private String description;
    private int price;
    private String imagePath;
    private String timeValue;
    private float star;
    private boolean bestFood;
    private int categoryId;

    public Food() {}

    public Food(String foodId, String title, String description, int price, String imagePath, String timeValue, float star, boolean bestFood, int categoryId) {
        this.foodId = foodId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imagePath = imagePath;
        this.timeValue = timeValue;
        this.star = star;
        this.bestFood = bestFood;
        this.categoryId = categoryId;
    }

    // Getters and setters
    public String getFoodId() { return foodId; }
    public void setFoodId(String foodId) { this.foodId = foodId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getTimeValue() { return timeValue; }
    public void setTimeValue(String timeValue) { this.timeValue = timeValue; }

    public float getStar() { return star; }
    public void setStar(float star) { this.star = star; }

    public boolean isBestFood() { return bestFood; }
    public void setBestFood(boolean bestFood) { this.bestFood = bestFood; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
}