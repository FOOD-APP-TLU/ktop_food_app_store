package com.example.ktop_food_app_store.view.activity.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ktop_food_app_store.utils.AuthValidator;
import com.example.ktop_food_app_store.R;
import com.example.ktop_food_app_store.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;
    private AuthValidator.OnValidationError validationErrorCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        validationErrorCallback = message -> binding.txtEmail.setError(message);

        // Lấy email từ Intent (nếu có)
        String email = getIntent().getStringExtra("email");
        if (email != null && !email.isEmpty()) {
            binding.txtEmail.setText(email);
        }

        handleBackIcon();
        checkActive();
        handleSendCode();
        handleTextWatchers();
    }

    private void handleBackIcon() {
        binding.btnBackIcon.setOnClickListener(v -> finish());
    }

    private void checkActive() {
        if (validateSendCode()) {
            binding.btnSendCode.setBackgroundResource(R.drawable.custom_bg_success);
            binding.btnSendCode.setEnabled(true);
        } else {
            binding.btnSendCode.setBackgroundResource(R.drawable.custom_bg_default);
            binding.btnSendCode.setEnabled(false);
        }
    }

    private void handleSendCode() {
        binding.btnSendCode.setOnClickListener(v -> {
            if (validateSendCode()) {
                String email = binding.txtEmail.getText().toString().trim();
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateSendCode() {
        String email = binding.txtEmail.getText().toString().trim();
        return AuthValidator.validateEmail(email, validationErrorCallback);
    }

    private void handleTextWatchers() {
        binding.txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                checkActive();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}