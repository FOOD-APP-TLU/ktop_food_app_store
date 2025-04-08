package com.example.ktop_food_app_store.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ktop_food_app_store.databinding.ActivityFoodListBinding;
import com.example.ktop_food_app_store.model.data.entity.Category;
import com.example.ktop_food_app_store.model.data.entity.Food;
import com.example.ktop_food_app_store.view.adapter.FoodAdapter;
import com.example.ktop_food_app_store.viewmodel.FoodListViewModel;

import java.util.ArrayList;
import java.util.List;

public class FoodListActivity extends AppCompatActivity implements FoodAdapter.FoodItemClickListener {

    private ActivityFoodListBinding binding;
    private FoodAdapter foodAdapter;
    private List<Food> foodList;
    private FoodListViewModel viewModel;
    private ProgressDialog progressDialog;
    private Category category; // Khai báo biến category

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
        foodAdapter = new FoodAdapter(this, foodList, this);
        binding.recyclerViewFoodList.setAdapter(foodAdapter);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(FoodListViewModel.class);

        // Lấy category từ Intent và đặt tiêu đề
        getCategoryFromIntent();

        // Quan sát LiveData từ ViewModel
        observeViewModel();

        // Xử lý sự kiện nhấn nút Back
        binding.btnBack.setOnClickListener(v -> finish());

        // Gọi hàm lấy danh sách món ăn từ ViewModel
        viewModel.fetchFoodItems();
    }

    private void getCategoryFromIntent() {
        Object serializableExtra = getIntent().getSerializableExtra("category");
        if (serializableExtra instanceof Category) {
            category = (Category) serializableExtra;
            binding.txtCategoryTitle.setText(category.getName());
        }
    }

    private void observeViewModel() {
        // Quan sát danh sách món ăn
        viewModel.getFoodList().observe(this, foods -> {
            foodList.clear();
            if (category != null && category.getName() != null) {
                // Lọc theo tên category
                for (Food food : foods) {
                    if (food.getCategoryId()==(category.getId())) {
                        foodList.add(food);
                    }
                }
            }
            foodAdapter.notifyDataSetChanged();

            if (foodList.isEmpty()) {
                Toast.makeText(this, "Không tìm thấy món ăn nào trong danh mục", Toast.LENGTH_SHORT).show();
            }
        });

        // Quan sát thông báo lỗi
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            Toast.makeText(this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
        });

        // Quan sát trạng thái xóa
        viewModel.getDeleteSuccess().observe(this, success -> {
            progressDialog.dismiss();
            if (success) {
                Toast.makeText(this, "Xóa món ăn thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditClick(Food food) {
        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra("food", food);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Food food) {
        if (food.getFoodId() == null || food.getFoodId().isEmpty()) {
            Toast.makeText(this, "Không thể xóa: ID món ăn không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có muốn xóa món " + food.getTitle() + " khỏi danh sách không?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    progressDialog.show();
                    viewModel.deleteFood(food.getFoodId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}