package com.example.ktop_food_app_store.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ktop_food_app_store.R;
import com.example.ktop_food_app_store.model.data.entity.TopSellingItem;

import java.util.List;

public class TopSellingAdapter extends RecyclerView.Adapter<TopSellingAdapter.ViewHolder> {
    private Context context;
    private List<TopSellingItem> topSellingItems;

    public TopSellingAdapter(Context context, List<TopSellingItem> topSellingItems) {
        this.context = context;
        this.topSellingItems = topSellingItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_top_selling, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopSellingItem item = topSellingItems.get(position);
        holder.itemNameText.setText(item.getName());
        holder.itemQuantityText.setText("Sold: " + item.getQuantity());

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(holder.itemImage);
        } else {
            holder.itemImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() {
        return topSellingItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemNameText, itemQuantityText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.topItemImage);
            itemNameText = itemView.findViewById(R.id.topItemNameText);
            itemQuantityText = itemView.findViewById(R.id.topItemQuantityText);
        }
    }
}