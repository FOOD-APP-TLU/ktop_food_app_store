package com.example.ktop_food_app_store.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ktop_food_app_store.databinding.ItemFoodBinding; // Binding class cho item_food.xml
import com.example.ktop_food_app_store.model.data.entity.Food;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private Context context;
    private List<Food> foodList;
    private FoodItemClickListener listener;

    public FoodAdapter(Context context, List<Food> foodList, FoodItemClickListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng View Binding để inflate layout
        ItemFoodBinding binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        // Sử dụng binding để gán dữ liệu
        holder.binding.foodTitle.setText(food.getTitle());
        holder.binding.foodPrice.setText(food.getPrice());
        holder.binding.foodTime.setText(food.getDescription());
        holder.binding.editButton.setOnClickListener(v -> listener.onEditClick(food));
        holder.binding.deleteButton.setOnClickListener(v -> listener.onDeleteClick(food));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        private final ItemFoodBinding binding; // Binding object cho ViewHolder

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