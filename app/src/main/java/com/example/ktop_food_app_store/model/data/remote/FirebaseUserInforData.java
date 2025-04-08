package com.example.ktop_food_app_store.model.data.remote;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseUserInforData {

    private final DatabaseReference usersRef;

    public FirebaseUserInforData() {
        usersRef = FirebaseDatabase
                .getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users");
    }

    public void getUserInfo(String uid, UserInfoCallback callback) {
        usersRef.child(uid).child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("displayName").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                callback.onSuccess(name, phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public interface UserInfoCallback {
        void onSuccess(String name, String phone);
        void onFailure(String errorMessage);
    }
}
