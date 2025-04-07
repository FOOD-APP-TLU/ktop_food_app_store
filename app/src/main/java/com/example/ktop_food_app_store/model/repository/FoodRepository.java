package com.example.ktop_food_app_store.model.repository;

import com.example.ktop_food_app_store.model.data.entity.Food;
import com.example.ktop_food_app_store.model.data.remote.FirebaseFoodData;

import java.util.List;

public class FoodRepository {

    private FirebaseFoodData firebaseFoodData;

    public FoodRepository() {
        firebaseFoodData = new FirebaseFoodData();
    }

    // Lấy danh sách món ăn
    public void fetchFoodItems(FirebaseFoodData.FoodDataCallback callback) {
        firebaseFoodData.fetchFoodItems(callback);
    }

    // Xóa món ăn
    public void deleteFood(String foodId, FirebaseFoodData.DeleteFoodCallback callback) {
        firebaseFoodData.deleteFood(foodId, callback);
    }
}