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
        holder.topItemNameText.setText(item.getName());
        holder.topItemQuantityText.setText("Sold: " + item.getQuantity());

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(holder.topItemImage);
        } else {
            holder.topItemImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() {
        return topSellingItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView topItemImage;
        TextView topItemNameText, topItemQuantityText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            topItemImage = itemView.findViewById(R.id.top_item_image);
            topItemNameText = itemView.findViewById(R.id.top_item_name_text);
            topItemQuantityText = itemView.findViewById(R.id.top_item_quantity_text);
        }
    }
}