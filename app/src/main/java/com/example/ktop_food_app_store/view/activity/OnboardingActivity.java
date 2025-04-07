package com.example.ktop_food_app_store.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ktop_food_app_store.databinding.ActivityOnboardingBinding;
import com.example.ktop_food_app_store.view.activity.Auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class OnboardingActivity extends AppCompatActivity {
    private ActivityOnboardingBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // Kiểm tra nếu người dùng đã đăng nhập
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(OnboardingActivity.this, FoodListActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        handleLogin();
    }


    private void handleLogin() {
        binding.btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
