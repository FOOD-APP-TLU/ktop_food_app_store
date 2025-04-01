package com.example.ktop_food_app_store.view.activity.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ktop_food_app_store.utils.AuthValidator;
import com.example.ktop_food_app_store.R;
import com.example.ktop_food_app_store.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkActive();
        handleLogin();
        handleVisibilityToggle();
        handleTextWatchers();
    }

    private void checkActive() {
        if (validateLogin()) {
            binding.btnLogin.setBackgroundResource(R.drawable.custom_bg_success);
            binding.btnLogin.setEnabled(true);
        } else {
            binding.btnLogin.setBackgroundResource(R.drawable.custom_bg_default);
            binding.btnLogin.setEnabled(false);
        }
    }

    private void handleLogin() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.edtUsername.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();

            if (validateLogin()) {
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Đăng nhập thất bại: Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleVisibilityToggle() {
        binding.visibilityOffIcon.setOnClickListener(v -> {
            if (isPasswordVisible) {
                binding.edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                binding.visibilityOffIcon.setImageResource(R.drawable.visibility_off);
            } else {
                binding.edtPassword.setTransformationMethod(null);
                binding.visibilityOffIcon.setImageResource(R.drawable.visibility_on);
            }

            binding.edtPassword.setSelection(binding.edtPassword.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });
    }

    private boolean validateLogin() {
        String email = binding.edtUsername.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();

        return AuthValidator.validateEmail(email, message -> binding.edtUsername.setError(message)) &&
                AuthValidator.validatePassword(password, message -> binding.edtPassword.setError(message));
    }

    private void handleTextWatchers() {
        binding.edtUsername.addTextChangedListener(new TextWatcher() {
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

        binding.edtPassword.addTextChangedListener(new TextWatcher() {
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
