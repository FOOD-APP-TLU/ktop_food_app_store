package com.example.ktop_food_app_store.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ktop_food_app_store.databinding.ActivityFoodListBinding;
import com.example.ktop_food_app_store.model.data.entity.Food;
import com.example.ktop_food_app_store.view.adapter.FoodAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FoodListActivity extends AppCompatActivity implements FoodAdapter.FoodItemClickListener {

    private ActivityFoodListBinding binding; // Khai báo đối tượng binding
    private FoodAdapter foodAdapter; // Sử dụng FoodAdapter đã cung cấp
    private List<Food> foodList;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog; // Dialog hiển thị khi đang xóa
    private static final String TAG = "FoodListActivity"; // Tag để log

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo View Binding
        binding = ActivityFoodListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xóa món ăn...");
        progressDialog.setCancelable(false);

        // Khởi tạo RecyclerView
        binding.recyclerViewFoodList.setLayoutManager(new LinearLayoutManager(this));
        foodList = new ArrayList<>();
        foodAdapter = new FoodAdapter(this, foodList, this); // Khởi tạo FoodAdapter
        binding.recyclerViewFoodList.setAdapter(foodAdapter);

        // Khởi tạo Firebase Database
        databaseReference = FirebaseDatabase.getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("foods");

        // Xử lý sự kiện nhấn nút Back
        binding.btnBack.setOnClickListener(v -> {
            // Kết thúc activity và quay lại màn hình trước đó
            finish();
        });

        // Lấy danh sách món ăn từ danh mục "Chicken" (categoryId = 7)
        fetchFoodItems();
    }

    private void fetchFoodItems() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList.clear(); // Xóa danh sách để tránh trùng lặp
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    if (food != null && food.getCategoryId() == 7) { // Lọc danh mục Chicken (categoryId = 7)
                        foodList.add(food);
                    }
                }
                foodAdapter.notifyDataSetChanged(); // Thông báo adapter khi dữ liệu thay đổi

                if (foodList.isEmpty()) {
                    Toast.makeText(FoodListActivity.this, "Không tìm thấy món ăn nào trong danh mục Chicken", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FoodListActivity.this, "Không thể tải danh sách món ăn: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditClick(Food food) {
        // Chuyển đến màn hình chỉnh sửa món ăn
        Toast.makeText(FoodListActivity.this, "Chuyển đến trang chỉnh sửa món: " + food.getTitle(), Toast.LENGTH_SHORT).show();
        // TODO: Thêm Intent để chuyển đến màn hình chỉnh sửa nếu cần
    }

    @Override
    public void onDeleteClick(Food food) {
        // Kiểm tra foodId có hợp lệ không
        if (food.getFoodId() == null || food.getFoodId().isEmpty()) {
            Toast.makeText(FoodListActivity.this, "Không thể xóa: ID món ăn không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị dialog xác nhận trước khi xóa
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có muốn xóa món " + food.getTitle() + " khỏi danh sách không?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Hiển thị ProgressDialog khi bắt đầu xóa
                    progressDialog.show();

                    // Tìm node có foodId khớp
                    databaseReference.orderByChild("foodId").equalTo(food.getFoodId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                progressDialog.dismiss();
                                Toast.makeText(FoodListActivity.this, "Không tìm thấy món ăn với ID: " + food.getFoodId(), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Lấy key của node cần xóa
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String key = dataSnapshot.getKey();

                                // Xóa node bằng key
                                databaseReference.child(key).removeValue().addOnCompleteListener(task -> {
                                    // Ẩn ProgressDialog sau khi xóa xong
                                    progressDialog.dismiss();

                                    if (task.isSuccessful()) {
                                        Toast.makeText(FoodListActivity.this, "Đã xóa món: " + food.getTitle(), Toast.LENGTH_SHORT).show();
                                        // Dữ liệu sẽ tự động cập nhật qua addValueEventListener
                                    } else {
                                        Toast.makeText(FoodListActivity.this, "Xóa món thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break; // Chỉ xóa node đầu tiên tìm thấy (foodId nên là duy nhất)
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(FoodListActivity.this, "Lỗi khi tìm món ăn: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Giải phóng binding để tránh rò rỉ bộ nhớ
        binding = null;
    }
}