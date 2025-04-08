package com.example.ktop_food_app_store.view.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ktop_food_app_store.R;
import com.example.ktop_food_app_store.databinding.ItemOrderProcessDetailBinding;
import com.example.ktop_food_app_store.model.data.entity.CartItem;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class OrderProcessDetailAdapter extends RecyclerView.Adapter<OrderProcessDetailAdapter.OrderProcessDetailViewHolder> {

    private final Context context;
    private final List<CartItem> itemList;
    private final DecimalFormat decimalFormat;

    public OrderProcessDetailAdapter(Context context, List<CartItem> itemList) {
        this.context = context;
        this.itemList = itemList;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,###", symbols);
    }

    @NonNull
    @Override
    public OrderProcessDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng View Binding để inflate layout
        ItemOrderProcessDetailBinding binding = ItemOrderProcessDetailBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderProcessDetailViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderProcessDetailViewHolder holder, int position) {
        CartItem item = itemList.get(position);

        // Set item name
        holder.binding.txtFoodName.setText(item.getName() != null ? item.getName() : "Unknown Product");

        // Set quantity
        holder.binding.txtQuantity.setText(String.valueOf(item.getQuantity()));

        // Set price and total price
        holder.binding.txtPrice.setText(decimalFormat.format(item.getPrice()) + " d");
        holder.binding.txtTotalItemPrice.setText(decimalFormat.format(item.getTotalPrice()) + " d");

        // Load image using Glide
        Glide.with(context)
                .load(item.getImagePath())
                .placeholder(R.drawable.bingsuberry)
                .error(R.drawable.bingsuberry)
                .into(holder.binding.foodImageView);
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    static class OrderProcessDetailViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderProcessDetailBinding binding;

        public OrderProcessDetailViewHolder(@NonNull ItemOrderProcessDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

