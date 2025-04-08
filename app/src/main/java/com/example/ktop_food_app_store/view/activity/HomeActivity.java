package com.example.ktop_food_app_store.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ktop_food_app_store.model.data.remote.FirebaseAuthData;
import com.example.ktop_food_app_store.model.repository.AuthRepository;
import com.example.ktop_food_app_store.model.repository.CategoryRepository;
import com.example.ktop_food_app_store.view.activity.Auth.LoginActivity;
import com.example.ktop_food_app_store.view.adapter.CategoryAdapter;
import com.example.ktop_food_app_store.viewmodel.CategoryViewModel;
import com.example.ktop_food_app_store.R;
import com.example.ktop_food_app_store.databinding.ActivityNavHomeBinding;
import com.example.ktop_food_app_store.databinding.NavHeaderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ActivityNavHomeBinding binding;
    private NavHeaderBinding headerBinding;
    private CategoryRepository categoryRepository;
    private CategoryAdapter categoryAdapter;
    private AuthRepository authRepository;
    private DatabaseReference mDatabase;
    private String currentUserId;
    private CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Giữ nguyên binding.getRoot() như code gốc

        headerBinding = NavHeaderBinding.bind(binding.navView.getHeaderView(0));

        // Khởi tạo AuthRepository
        authRepository = new AuthRepository(new FirebaseAuthData());
        mDatabase = authRepository.getDatabaseReference();

        // Kiểm tra người dùng hiện tại
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        categoryAdapter = new CategoryAdapter(this, new ArrayList<>());
        binding.home.gridViewCategories.setAdapter(categoryAdapter);

        currentUserId = currentUser.getUid();
        categoryRepository = new CategoryRepository();

        setupViewModels();
        handleMenuButton();
        handleNavigationMenu();
        handleBackUpButton();
        handleLogoutButton();
    }

    private void handleBackUpButton() {
        headerBinding.imgBackArrow.setOnClickListener(v ->
                binding.drawerLayout.closeDrawer(GravityCompat.START));
    }

    private void handleLogoutButton() {
        binding.logoutButton.setOnClickListener(v -> {
            authRepository.getCurrentUser();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void handleMenuButton() {
        binding.home.menuButtonIcon.setOnClickListener(v ->
                binding.drawerLayout.openDrawer(GravityCompat.START));
    }

    private void handleNavigationMenu() {
        binding.navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_add_item) {
                Intent intent = new Intent(HomeActivity.this, AddItemActivity.class );
                startActivity(intent);
            } else if (itemId == R.id.nav_track_order) {
                Intent intent = new Intent(HomeActivity.this, OrderProcessActivity.class );
                startActivity(intent);
            } else if (itemId == R.id.nav_order_history) {
                Toast.makeText(HomeActivity.this, "Chuyen den trang Order History", Toast.LENGTH_SHORT).show();
            }
            else if (itemId == R.id.nav_revenue_statistics) {
                Intent intent = new Intent(HomeActivity.this, StatisticActivity.class );
                startActivity(intent);
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupViewModels() {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        categoryViewModel.getCategoriesLiveData().observe(this, categoryList -> {
            categoryAdapter.setCategoryList(categoryList); // categoryAdapter giờ sẽ không null
        });
        categoryViewModel.getErrorMessage().observe(this, error -> {
            Toast.makeText(this, "Lỗi tải danh mục: " + error, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}