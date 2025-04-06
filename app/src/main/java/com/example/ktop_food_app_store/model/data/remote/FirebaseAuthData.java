package com.example.ktop_food_app_store.model.data.remote;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseAuthData {
    private static final String DATABASE_URL = "https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app";
    private final FirebaseAuth mAuth;

    public FirebaseAuthData() {
        mAuth = FirebaseAuth.getInstance();
    }

    // Kiểm tra user hiện tại
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    // Đăng nhập
    public Task signInWithEmailAndPassword(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    // Gửi email đặt lại mật khẩu
    public Task sendPasswordResetEmail(String email) {
        return mAuth.sendPasswordResetEmail(email);
    }


    // Thêm phương thức để lấy DatabaseReference
    public DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance(DATABASE_URL).getReference();
    }
}