package com.example.ktop_food_app_store.model.data.remote;


import com.example.ktop_food_app_store.model.data.entity.Food;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseEditItemData {
    private final DatabaseReference databaseReference;

    // Khởi tạo tham chiếu đến Firebase Realtime Database
    public FirebaseEditItemData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("foods");
    }

    // Cập nhật món ăn lên Firebase
    public void updateFoodItem(Food food, Runnable onSuccess, Runnable onFailure) {
        databaseReference.child(food.getFoodId()).setValue(food)
                .addOnSuccessListener(aVoid -> onSuccess.run())
                .addOnFailureListener(e -> onFailure.run());
    }
}