package com.example.ktop_food_app_store.model.data.remote;

import androidx.annotation.NonNull;

import com.example.ktop_food_app_store.model.data.entity.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseFoodData {

    private DatabaseReference databaseReference;

    public FirebaseFoodData() {
        databaseReference = FirebaseDatabase
                .getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("foods");
    }

    // Callback interface để trả về kết quả
    public interface FoodDataCallback {
        void onSuccess(List<Food> foodList);
        void onError(String errorMessage);
    }

    // Callback interface cho việc xóa món ăn
    public interface DeleteFoodCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    // Lấy danh sách món ăn từ Firebase
    public void fetchFoodItems(FoodDataCallback callback) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Food> foodList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    if (food != null ) {
                        foodList.add(food);
                    }
                }
                callback.onSuccess(foodList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    // Xóa món ăn từ Firebase
    public void deleteFood(String foodId, DeleteFoodCallback callback) {
        databaseReference.orderByChild("foodId").equalTo(foodId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callback.onError("Không tìm thấy món ăn với ID: " + foodId);
                    return;
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                    databaseReference.child(key).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onError(task.getException().getMessage());
                        }
                    });
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }
}