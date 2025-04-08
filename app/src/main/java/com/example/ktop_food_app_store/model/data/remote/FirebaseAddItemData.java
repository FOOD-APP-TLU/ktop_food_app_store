package com.example.ktop_food_app_store.model.data.remote;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.Map;

public class FirebaseAddItemData {

    private final DatabaseReference foodsRef;
    private final DatabaseReference counterRef;

    public FirebaseAddItemData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        foodsRef = database.getReference("foods");
        counterRef = database.getReference("foodCounter");
    }

    public void addItem(Map<String, Object> newFood, MutableLiveData<String> result) {
        counterRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long counter = mutableData.getValue(Long.class);
                if (counter == null) {
                    counter = 0L; // Khởi tạo nếu chưa có
                }
                counter++; // Tăng bộ đếm
                mutableData.setValue(counter); // Chỉ cập nhật counter
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    result.setValue("Thêm món ăn thất bại: " + databaseError.getMessage());
                    return;
                }
                if (committed && dataSnapshot != null) {
                    Long counter = dataSnapshot.getValue(Long.class);
                    if (counter != null) {
                        String newFoodId = "F" + String.format("%03d", counter);
                        newFood.put("foodId", newFoodId);
                        System.out.println("Counter: " + counter + ", New Food ID: " + newFoodId); // Thêm log
                        foodsRef.child(newFoodId).setValue(newFood)
                                .addOnSuccessListener(aVoid -> result.setValue("Thêm món ăn thành công"))
                                .addOnFailureListener(e -> result.setValue("Thêm món ăn thất bại: " + e.getMessage()));
                    } else {
                        result.setValue("Lỗi: Không lấy được giá trị bộ đếm.");
                    }
                } else {
                    result.setValue("Giao dịch thất bại. Vui lòng thử lại.");
                }
            }
        });
    }
}