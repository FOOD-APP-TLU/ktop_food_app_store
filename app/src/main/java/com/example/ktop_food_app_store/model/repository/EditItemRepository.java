package com.example.ktop_food_app_store.model.repository;


import com.example.ktop_food_app_store.model.data.entity.Food;
import com.example.ktop_food_app_store.model.data.remote.FirebaseEditItemData;



public class EditItemRepository {
    private final FirebaseEditItemData firebaseData;

    // Khởi tạo repository với FirebaseEditItemData
    public EditItemRepository(FirebaseEditItemData firebaseData) {
        this.firebaseData = firebaseData;
    }

    // Cập nhật món ăn và xử lý kết quả qua callback
    public void updateFoodItem(Food food, Runnable onSuccess, Runnable onFailure) {
        firebaseData.updateFoodItem(food,
                () -> onSuccess.run(),
                () -> onFailure.run());
    }
}