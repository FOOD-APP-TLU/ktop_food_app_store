package com.example.ktop_food_app_store.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ktop_food_app_store.model.data.entity.CartItem;
import com.example.ktop_food_app_store.model.data.entity.Order;
//import com.example.ktop_food_app_store.databinding.ItemOrderHistoryBinding;
import com.example.ktop_food_app_store.databinding.ItemOrderProcessBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.ktop_food_app_store.databinding.ItemOrderHistoryBinding;
//import com.example.ktop_food_app_store.databinding.ItemTrackOrdersBinding;

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
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == VIEW_TYPE_ORDER_HISTORY) {
//            ItemOrderHistoryBinding binding = ItemOrderHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//            return new OrderHistoryViewHolder(binding);
//        }
        if (viewType == VIEW_TYPE_ORDER_PROCESS) {
            ItemOrderProcessBinding binding = ItemOrderProcessBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new OrderProcessViewHolder(binding);
        if (viewType == VIEW_TYPE_ORDER_HISTORY) {
            ItemOrderHistoryBinding binding = ItemOrderHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new OrderHistoryViewHolder(binding);
        }
        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Order order = orderList.get(position);
//        if (holder instanceof OrderHistoryViewHolder) {
//            ((OrderHistoryViewHolder) holder).bind(order);
//        }
        if (holder instanceof OrderProcessViewHolder) {
            ((OrderProcessViewHolder) holder).bind(order);
        if (holder instanceof OrderHistoryViewHolder) {
            ((OrderHistoryViewHolder) holder).bind(order);
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

    // ViewHolder for Order History
//    public class OrderHistoryViewHolder extends RecyclerView.ViewHolder {
//        private final ItemOrderHistoryBinding binding;
//
//        public OrderHistoryViewHolder(ItemOrderProcessBinding binding) {
//            super(binding.getRoot());
//            this.binding = binding;
//        }
//
//        public void bind(Order order) {
//            binding.txtOrderId.setText(order.getOrderId());
//            binding.txtOrderStatus.setText(order.getStatus());
//            String allProduct = String.valueOf(order.getItems().size());
//            binding.totalAllProduct.setText("(" + allProduct + " Products):");
//            binding.totalAllProductPrice.setText(decimalFormat.format(order.getTotalPrice()) + " d");
//
//            if (order.getItems() != null && !order.getItems().isEmpty()) {
//                CartItem item = order.getItems().get(0);
//                Glide.with(context)
//                        .load(item.getImagePath())
//                        .into(binding.productFirstImage);
//                binding.productName.setText(item.getName());
//                String quantityOfFirstProduct = String.valueOf(item.getQuantity());
//                binding.productQuantity.setText("x" + quantityOfFirstProduct);
//            }
//
//            binding.getRoot().setOnClickListener(v -> listener.onOrderClick(order));
//        }
//    }

    // ViewHolder for Track Order
    public class OrderProcessViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderProcessBinding binding;

        public OrderProcessViewHolder(ItemOrderProcessBinding binding) {
    public class OrderHistoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderHistoryBinding binding;

        public OrderHistoryViewHolder(ItemOrderHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Order order) {
            binding.txtOrderId.setText(order.getOrderId());
            binding.txtOrderStatus.setText(order.getStatus());
            binding.totalAllProductPrice.setText(decimalFormat.format(order.getTotalPrice()) + " d");
            binding.txtTotalAllProduct.setText("(" + order.getItems().size() + " Products" + ")");

            String allProduct = String.valueOf(order.getItems().size());
            binding.totalAllProduct.setText("(" + allProduct + " Products):");
            binding.totalAllProductPrice.setText(decimalFormat.format(order.getTotalPrice()) + " d");


            if (order.getItems() != null && !order.getItems().isEmpty()) {
                CartItem item = order.getItems().get(0);
                Glide.with(context)
                        .load(item.getImagePath())

                        .into(binding.productImage);
                binding.productName.setText(item.getName());
                binding.txtProductQuantity.setText("x" + item.getQuantity());

            }

            // Xử lý hiển thị nút theo trạng thái
            String status = order.getStatus().toLowerCase(); // tránh sai chính tả hoa/thường
            if (status.equals("pending")) {
                binding.btnDelivery.setVisibility(View.VISIBLE);
                binding.btnOrderCompleted.setVisibility(View.GONE);
            } else if (status.equals("shipping")) {
                binding.btnDelivery.setVisibility(View.GONE);
                binding.btnOrderCompleted.setVisibility(View.VISIBLE);
            }

            // Initialize Firebase Database Reference
            ordersRef = FirebaseDatabase.getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("orders");
            binding.btnDelivery.setOnClickListener(v -> {
                if (ordersRef != null) {
                    ordersRef.child(order.getOrderId())
                            .child("status")
                            .setValue("shipping")
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(context, "Đơn hàng đã chuyển sang trạng thái Shipping", Toast.LENGTH_SHORT).show();
                                order.setStatus("shipping");
                                int pos = getAdapterPosition();
                                if (pos != RecyclerView.NO_POSITION) notifyItemChanged(pos);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            });

            // (Tùy chọn) xử lý click nút Completed
            binding.btnOrderCompleted.setOnClickListener(v -> {
                if (ordersRef != null) {
                    ordersRef.child(order.getOrderId())
                            .child("status")
                            .setValue("completed")
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(context, "Đơn hàng đã hoàn thành", Toast.LENGTH_SHORT).show();
                                order.setStatus("completed");
                                int pos = getAdapterPosition();
                                if (pos != RecyclerView.NO_POSITION) {
                                    orderList.remove(pos); // Xóa khỏi danh sách
                                    notifyItemRemoved(pos); // Cập nhật RecyclerView
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            });
            binding.getRoot().setOnClickListener(v -> listener.onOrderClick(order));
        }
    }
                        .into(binding.productFirstImage);
                binding.productName.setText(item.getName());
                String quantityOfFirstProduct = String.valueOf(item.getQuantity());
                binding.productQuantity.setText("x" + quantityOfFirstProduct);
            }

            binding.getRoot().setOnClickListener(v -> listener.onOrderClick(order));
        }
    }

    // ViewHolder for Track Order
//    public class TrackOrderViewHolder extends RecyclerView.ViewHolder {
//        private final ItemTrackOrdersBinding binding;
//
//        public TrackOrderViewHolder(ItemTrackOrdersBinding binding) {
//            super(binding.getRoot());
//            this.binding = binding;
//        }
//
//        public void bind(Order order) {
//            binding.txtOrderId.setText(order.getOrderId());
//            binding.txtOrderStatus.setText(order.getStatus());
//            binding.totalAllProductPrice.setText(decimalFormat.format(order.getTotalPrice()) + " d");
//
//            if (order.getItems() != null && !order.getItems().isEmpty()) {
//                CartItem item = order.getItems().get(0);
//                Glide.with(context)
//                        .load(item.getImagePath())
//                        .into(binding.productFirstImage);
//                binding.productName.setText(item.getName());
//            }
//            binding.getRoot().setOnClickListener(v -> listener.onOrderClick(order));
//        }
//    }
}