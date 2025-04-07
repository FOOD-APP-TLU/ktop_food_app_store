package com.example.ktop_food_app_store.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ktop_food_app_store.model.data.entity.Category;
import com.example.ktop_food_app_store.databinding.ItemCategoryBinding;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends ArrayAdapter<Category> {

    private final Context context;
    private final List<Category> categoryList;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        super(context, 0, categoryList != null ? categoryList : new ArrayList<>());
        this.context = context;
        this.categoryList = categoryList != null ? categoryList : new ArrayList<>();
        Log.d("CategoryAdapter", "Adapter initialized with initial size: " + this.categoryList.size());
    }

    public void setCategoryList(List<Category> newCategoryList) {
        this.categoryList.clear();
        if (newCategoryList != null && !newCategoryList.isEmpty()) {
            this.categoryList.addAll(newCategoryList);
            Log.d("CategoryAdapter", "setCategoryList called with " + newCategoryList.size() + " items");
            for (Category category : newCategoryList) {
                Log.d("CategoryAdapter", "Category: ID=" + category.getId() + ", Name=" + category.getName() + ", ImagePath=" + category.getImagePath());
            }
        } else {
            Log.w("CategoryAdapter", "setCategoryList received null or empty list");
        }
        notifyDataSetChanged();
        Log.d("CategoryAdapter", "notifyDataSetChanged called, current size: " + categoryList.size());
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemCategoryBinding binding;

        if (convertView == null) {
            binding = ItemCategoryBinding.inflate(LayoutInflater.from(context), parent, false);
            convertView = binding.getRoot();
        } else {
            binding = ItemCategoryBinding.bind(convertView);
        }

        Category category = categoryList.get(position);
        Log.d("CategoryAdapter", "Rendering item at position " + position + ": ID=" + category.getId() + ", Name=" + category.getName());

        // Đặt tên danh mục
        binding.txtCategory.setText(category.getName() != null ? category.getName() : "Unnamed Category");

        // Xử lý URL ảnh với Glide
        String imageUrl = category.getImagePath();
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            Log.d("CategoryAdapter", "Loading image for " + category.getName() + ": " + imageUrl);
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(60, 60) // Resize ảnh để khớp với ImageView (60dp x 60dp)
                    .centerCrop() // Đảm bảo ảnh được cắt đúng tỷ lệ
                    .into(binding.imgCategory);
        } else {
            Log.w("CategoryAdapter", "Image URL is null or empty for " + category.getName());
            binding.imgCategory.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Xử lý sự kiện click
        binding.getRoot().setOnClickListener(v -> {
            Log.d("CategoryAdapter", "Clicked on category: " + category.getName());
//            Intent intent = new Intent(context, FoodListActivity.class);
//            intent.putExtra("category", category);
//            context.startActivity(intent);
        });

        return convertView;
    }
}