package com.example.ktop_food_app_store.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ktop_food_app_store.model.data.entity.CartItem;
import com.example.ktop_food_app_store.model.data.entity.Order;
import com.example.ktop_food_app_store.view.adapter.OrderHistoryDetailsAdapter;
import com.example.ktop_food_app_store.R;
import com.example.ktop_food_app_store.databinding.ActivityOrderHistoryDetailsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderHistoryDetailsActivity extends AppCompatActivity {

    private ActivityOrderHistoryDetailsBinding binding;
    private OrderHistoryDetailsAdapter itemAdapter;
    private Order order;
    private DecimalFormat decimalFormat;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding
        binding = ActivityOrderHistoryDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,###d", symbols);

        usersRef = FirebaseDatabase
                .getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users");

        // Call methods
        if (!loadOrderData()) {
            return;
        }
        displayOrderDetails();
        setupRecyclerView();
        setupListeners();
    }

    // Load order data from Intent
    private boolean loadOrderData() {
        order = (Order) getIntent().getSerializableExtra("order");
        if (order == null) {
            Toast.makeText(this, "Không thể hiển thị chi tiết đơn hàng!", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        return true;
    }

    // Display order details
    private void displayOrderDetails() {
        binding.txtOrderId.setText(order.getOrderId());
        binding.txtOrderStatus.setText(order.getStatus());

        usersRef.child(order.getUid())
                .child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String customerName = dataSnapshot.child("displayName").getValue(String.class);
                String phoneNumber = dataSnapshot.child("phone").getValue(String.class);
                binding.txtNameCustomer.setText(customerName != null ? customerName : "N/A");
                binding.txtPhone.setText(phoneNumber != null ? phoneNumber : "N/A");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        binding.txtAddress.setText(order.getAddress());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss", Locale.getDefault());
        String orderTime;
        try {
            orderTime = dateFormat.format(new Date(order.getCreatedAt()));
        } catch (Exception e) {
            orderTime = "N/A";
        }
        binding.txtOrderTime.setText(orderTime);

        String paymentMethod = order.getPaymentMethod();
        if ("COD".equalsIgnoreCase(paymentMethod)) {
            binding.paymentMethod.setText("Cash on Delivery");
            binding.paymentIcon.setImageResource(R.drawable.ic_cod);
        } else if ("Banking(Zalo Pay)".equalsIgnoreCase(paymentMethod)) {
            binding.paymentMethod.setText(paymentMethod);
            binding.paymentIcon.setImageResource(R.drawable.ic_bank);
        } else {
            binding.paymentMethod.setText(paymentMethod);
            binding.paymentIcon.setVisibility(View.GONE);
        }

        double subtotal = order.getTotalPrice() - order.getDiscount();
        binding.txtTotalPriceItemsAmount.setText(decimalFormat.format(order.getTotalPrice()));
        binding.txtDiscountAmount.setText(decimalFormat.format(order.getDiscount()));
        binding.txtTotalPaymentDetails.setText(decimalFormat.format(subtotal));
    }

    // Setup RecyclerView
    private void setupRecyclerView() {
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            itemAdapter = new OrderHistoryDetailsAdapter(this, order.getItems());
            binding.recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerViewItems.setAdapter(itemAdapter);
        } else {
            Toast.makeText(this, "Đơn hàng không có sản phẩm!", Toast.LENGTH_SHORT).show();
        }
    }

    // Setup button listeners
    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
    }
}