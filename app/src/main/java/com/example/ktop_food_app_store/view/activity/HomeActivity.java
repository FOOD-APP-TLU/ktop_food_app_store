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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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
        setContentView(binding.getRoot());

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

        // Cập nhật ngày hiện tại và nhãn ngày trong biểu đồ
        updateCurrentDate();

        setupViewModels();
        handleMenuButton();
        handleNavigationMenu();
        handleBackUpButton();
        handleLogoutButton();
        fetchRevenueData(); // Lấy dữ liệu doanh thu
    }

    private void updateCurrentDate() {
        // Lấy ngày hiện tại từ hệ thống
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        binding.home.txtDate.setText(currentDate); // Cập nhật ngày lên giao diện

    }

    private void fetchRevenueData() {
        // Lấy ngày hiện tại từ hệ thống
        Calendar calendar = Calendar.getInstance();

        // Đặt thời gian cho đầu tháng và cuối tháng
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfMonth = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long endOfMonth = calendar.getTimeInMillis();

        // Đặt thời gian cho ngày hiện tại
        calendar = Calendar.getInstance(); // Reset calendar về ngày hiện tại
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfDay = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long endOfDay = calendar.getTimeInMillis();

        // Đặt thời gian cho ngày hôm trước
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfPreviousDay = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long endOfPreviousDay = calendar.getTimeInMillis();

        // Lấy dữ liệu từ Firebase
        mDatabase.child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long monthlyRevenue = 0;
                long dailyRevenue = 0;
                long previousDayRevenue = 0;

                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    String status = orderSnapshot.child("status").getValue(String.class);
                    if (!"completed".equals(status)) {
                        continue; // Chỉ tính đơn hàng đã hoàn thành
                    }

                    long createdAt = orderSnapshot.child("createdAt").getValue(Long.class);
                    long totalPrice = orderSnapshot.child("totalPrice").getValue(Long.class);

                    // Tính doanh thu tháng
                    if (createdAt >= startOfMonth && createdAt <= endOfMonth) {
                        monthlyRevenue += totalPrice;
                    }

                    // Tính doanh thu ngày hiện tại
                    if (createdAt >= startOfDay && createdAt <= endOfDay) {
                        dailyRevenue += totalPrice;
                    }

                    // Tính doanh thu ngày hôm trước
                    if (createdAt >= startOfPreviousDay && createdAt <= endOfPreviousDay) {
                        previousDayRevenue += totalPrice;
                    }
                }

                // Định dạng số tiền
                DecimalFormat formatter = new DecimalFormat("#,###");
                binding.home.txtBalance.setText(formatter.format(monthlyRevenue) + "đ"); // Doanh thu tháng
                binding.home.txtRevenue.setText(formatter.format(dailyRevenue)); // Doanh thu ngày

                // Tính phần trăm tăng trưởng
                double percentageIncrease = 0;
                if (previousDayRevenue > 0) {
                    percentageIncrease = ((double) (dailyRevenue - previousDayRevenue) / previousDayRevenue) * 100;
                } else if (dailyRevenue > 0) {
                    percentageIncrease = 100; // Nếu ngày trước không có doanh thu, mà hôm nay có, thì tăng 100%
                }

                // Làm tròn phần trăm đến 1 chữ số thập phân và loại bỏ dấu âm
                String percentageText = String.format(Locale.getDefault(), "%.1f%%", Math.abs(percentageIncrease));

                // Cập nhật phần trăm tăng trưởng
                binding.home.txtPercentage.setText(percentageText);

                // Kiểm tra nếu doanh thu giảm (percentageIncrease < 0)
                if (percentageIncrease < 0) {
                    // Đặt màu đỏ cho phần trăm
                    binding.home.txtPercentage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    // Đặt icon mũi tên đi xuống
                    binding.home.iconArrowUp.setImageResource(R.drawable.ic_down);
                } else {
                    // Giữ màu xanh nếu tăng hoặc không đổi
                    binding.home.txtPercentage.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    // Đặt icon mũi tên đi lên
                    binding.home.iconArrowUp.setImageResource(R.drawable.ic_up);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "Lỗi tải dữ liệu doanh thu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                Intent intent = new Intent(HomeActivity.this, AddItemActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.nav_track_order) {
                Intent intent = new Intent(HomeActivity.this, OrderProcessActivity.class );
                startActivity(intent);
            } else if (itemId == R.id.nav_order_history) {
                Intent intent = new Intent(HomeActivity.this, OrderHistoryActivity.class );
                startActivity(intent);
            } else if (itemId == R.id.nav_revenue_statistics) {
                Intent intent = new Intent(HomeActivity.this, StatisticActivity.class);
                startActivity(intent);
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupViewModels() {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        categoryViewModel.getCategoriesLiveData().observe(this, categoryList -> {
            categoryAdapter.setCategoryList(categoryList);
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