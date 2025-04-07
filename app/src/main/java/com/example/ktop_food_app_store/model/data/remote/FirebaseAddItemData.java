package com.example.ktop_food_app_store.model.data.remote;

import androidx.lifecycle.MutableLiveData;

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
                    counter = 0L;
                }
                counter++;
                mutableData.setValue(counter);
                String newFoodId = "F" + String.format("%03d", counter);
                newFood.put("foodId", newFoodId);

                foodsRef.push().setValue(newFood);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(com.google.firebase.database.DatabaseError databaseError, boolean committed, com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    result.setValue("Failed to add item: " + databaseError.getMessage());
                } else if (committed) {
                    result.setValue("Item added successfully");
                } else {
                    result.setValue("Transaction failed. Please try again.");
                }
            }
        });
    }
}