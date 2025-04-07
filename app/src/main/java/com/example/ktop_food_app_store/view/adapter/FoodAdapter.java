package com.example.ktop_food_app_store.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ktop_food_app_store.databinding.ItemFoodBinding;
import com.example.ktop_food_app_store.model.data.entity.Food;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private Context context;
    private List<Food> foodList;
    private FoodItemClickListener listener;
    private final DecimalFormat decimalFormat;


    public FoodAdapter(Context context, List<Food> foodList, FoodItemClickListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.listener = listener;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,###", symbols);
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodBinding binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);

        // Sử dụng binding để gán dữ liệu
        holder.binding.foodTitle.setText(food.getTitle());
        holder.binding.foodPrice.setText(decimalFormat.format(food.getPrice()) + " d");
        holder.binding.foodTime.setText(food.getTimeValue());
        // Thêm Glide để tải hình ảnh
        Glide.with(context).load(food.getImagePath()).into(holder.binding.foodImage);

        holder.binding.editButton.setOnClickListener(v -> listener.onEditClick(food));
        holder.binding.deleteButton.setOnClickListener(v -> listener.onDeleteClick(food));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        private final ItemFoodBinding binding;

        public FoodViewHolder(@NonNull ItemFoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface FoodItemClickListener {
        void onEditClick(Food food);
        void onDeleteClick(Food food);
    }
}