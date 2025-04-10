package com.example.ktop_food_app_store.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ktop_food_app_store.databinding.ItemOrderHistoryBinding;
import com.example.ktop_food_app_store.databinding.ItemOrderProcessBinding;
import com.example.ktop_food_app_store.model.data.entity.CartItem;
import com.example.ktop_food_app_store.model.data.entity.Order;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_ORDER_HISTORY = 1;
    public static final int VIEW_TYPE_ORDER_PROCESS = 2;

    private final List<Order> orderList;
    private final Context context;
    private final OnOrderClickListener listener;
    private final DecimalFormat decimalFormat;
    private final int viewType;
    private DatabaseReference ordersRef;

    public OrderAdapter(Context context, List<Order> orderList, OnOrderClickListener listener, int viewType) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
        this.viewType = viewType;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,###", symbols);
        ordersRef = FirebaseDatabase.getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("orders");
    }

    @Override
    public int getItemViewType(int position) {
        return viewType; // Trả về viewType được truyền vào constructor
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_ORDER_HISTORY) {
            ItemOrderHistoryBinding binding = ItemOrderHistoryBinding.inflate(inflater, parent, false);
            return new OrderHistoryViewHolder(binding);
        } else if (viewType == VIEW_TYPE_ORDER_PROCESS) {
            ItemOrderProcessBinding binding = ItemOrderProcessBinding.inflate(inflater, parent, false);
            return new OrderProcessViewHolder(binding);
        }
        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Order order = orderList.get(position);
        if (holder instanceof OrderHistoryViewHolder) {
            ((OrderHistoryViewHolder) holder).bind(order);
        } else if (holder instanceof OrderProcessViewHolder) {
            ((OrderProcessViewHolder) holder).bind(order);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    // Listener interface for click events
    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    // ViewHolder cho Order History
    public class OrderHistoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderHistoryBinding binding;

        public OrderHistoryViewHolder(ItemOrderHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Order order) {
            binding.txtOrderId.setText(order.getOrderId());
            binding.txtOrderStatus.setText(order.getStatus());
            binding.totalAllProductPrice.setText(decimalFormat.format(order.getTotalPrice()) + " đ");
            binding.totalAllProduct.setText("(" + order.getItems().size() + " Products)");

            if (order.getItems() != null && !order.getItems().isEmpty()) {
                CartItem item = order.getItems().get(0);
                Glide.with(context)
                        .load(item.getImagePath())
                        .into(binding.productImage);
                binding.productName.setText(item.getName());
                binding.productQuantity.setText("x" + item.getQuantity());

            }

            binding.getRoot().setOnClickListener(v -> listener.onOrderClick(order));
        }
    }

    // ViewHolder cho Order Process/Track
    public class OrderProcessViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderProcessBinding binding;

        public OrderProcessViewHolder(ItemOrderProcessBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Order order) {
            binding.txtOrderId.setText(order.getOrderId());
            binding.txtOrderStatus.setText(order.getStatus());
            binding.totalAllProductPrice.setText(decimalFormat.format(order.getTotalPrice()) + " đ");
            binding.totalAllProduct.setText("(" + order.getItems().size() + " Products)");

            if (order.getItems() != null && !order.getItems().isEmpty()) {
                CartItem item = order.getItems().get(0);
                Glide.with(context)
                        .load(item.getImagePath())
                        .into(binding.productImage);
                binding.productName.setText(item.getName());
                binding.productQuantity.setText("x" + item.getQuantity());
            }

            // Xử lý hiển thị nút theo trạng thái
            String status = order.getStatus().toLowerCase();
            if (status.equals("pending")) {
                binding.btnDelivery.setVisibility(View.VISIBLE);
                binding.btnOrderCompleted.setVisibility(View.GONE);
            } else if (status.equals("shipping")) {
                binding.btnDelivery.setVisibility(View.GONE);
                binding.btnOrderCompleted.setVisibility(View.VISIBLE);
            } else {
                binding.btnDelivery.setVisibility(View.GONE);
                binding.btnOrderCompleted.setVisibility(View.GONE);
            }

            // Xử lý nút Delivery
            binding.btnDelivery.setOnClickListener(v -> {
                ordersRef.child(order.getOrderId())
                        .child("status")
                        .setValue("shipping")
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(context, "Đơn hàng đang trong quá trình vận chuyển", Toast.LENGTH_SHORT).show();
                            order.setStatus("shipping");
                            notifyItemChanged(getAdapterPosition());
                        })
                        .addOnFailureListener(e -> Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            });

            // Xử lý nút Completed
            binding.btnOrderCompleted.setOnClickListener(v -> {
                ordersRef.child(order.getOrderId())
                        .child("status")
                        .setValue("completed")
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(context, "Đơn hàng đã hoàn thành", Toast.LENGTH_SHORT).show();
                            order.setStatus("completed");
                            int pos = getAdapterPosition();
                            if (pos != RecyclerView.NO_POSITION) {
                                orderList.remove(pos);
                                notifyItemRemoved(pos);
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            });

            binding.getRoot().setOnClickListener(v -> listener.onOrderClick(order));
        }
    }
}