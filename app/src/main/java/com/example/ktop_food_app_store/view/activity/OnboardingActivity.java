package com.example.ktop_food_app_store.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ktop_food_app_store.databinding.ActivityOnboardingBinding;
import com.example.ktop_food_app_store.view.activity.Auth.LoginActivity;

public class OnboardingActivity extends AppCompatActivity {
    private ActivityOnboardingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        handleLogin();
    }

    private void handleLogin() {
        binding.btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
