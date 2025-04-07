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
import com.example.ktop_food_app_store.model.data.remote.FirebaseAuthData;
import com.example.ktop_food_app_store.model.repository.AuthRepository;

import com.example.ktop_food_app_store.view.activity.HomeActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private boolean isPasswordVisible = false;
    private AuthRepository authRepository; // Thay FirebaseAuth bằng AuthRepository

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Khởi tạo AuthRepository
        authRepository = new AuthRepository(new FirebaseAuthData());

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkActive();
        handleLogin();
        handleVisibilityToggle();
        handleTextWatchers();
        handleForgotPassword();
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
            if (validateLogin()) {
                String email = binding.edtUsername.getText().toString().trim();
                String password = binding.edtPassword.getText().toString().trim();
                loginUser(email, password); // Gọi hàm đăng nhập thông qua repository
            }
        });
    }

    private void loginUser(String email, String password) {
        authRepository.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công, lấy thông tin người dùng
                        FirebaseUser user = authRepository.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user.getUid()); // Kiểm tra vai trò
                        }
                    } else {
                        // Đăng nhập thất bại
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRole(String uid) {
        authRepository.getDatabaseReference().child("users").child(uid).child("role")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String role = snapshot.getValue(String.class);
                        if (role != null && "admin".equals(role)) {
                            // Vai trò là admin, cho phép đăng nhập
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công với vai trò Admin", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish(); // Đóng LoginActivity
                        } else {
                            // Không phải admin, đăng xuất và thông báo
                            authRepository.getCurrentUser().delete(); // Note: This might not be the best way to sign out
                            Toast.makeText(LoginActivity.this, "Bạn không có quyền truy cập. Chỉ admin mới được phép đăng nhập.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(LoginActivity.this, "Lỗi khi kiểm tra vai trò: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
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

    private void handleForgotPassword() {
        binding.txtForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
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