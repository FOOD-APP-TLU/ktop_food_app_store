package com.example.ktop_food_app_store.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ktop_food_app_store.model.data.entity.CartItem;
import com.example.ktop_food_app_store.model.data.entity.Order;
import com.example.ktop_food_app_store.view.adapter.OrderHistoryDetailsAdapter;
import com.example.ktop_food_app_store.R;
import com.example.ktop_food_app_store.databinding.ActivityOrderHistoryDetailsBinding;
import com.example.ktop_food_app_store.viewmodel.UserInforViewModel;
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
    private UserInforViewModel userInforViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding
        binding = ActivityOrderHistoryDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,###", symbols);

        userInforViewModel = new ViewModelProvider(this).get(UserInforViewModel.class);

        // Call methods
        if (!loadOrderData()) {
            return;
        }

        observeUserInfo();
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

        userInforViewModel.getUserInfo(order.getUid());

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

        double totalPriceOfItems = order.getTotalPrice() + order.getDiscount();
        binding.txtTotalPriceItemsAmount.setText(decimalFormat.format(totalPriceOfItems) + " d");
        binding.txtDiscountAmount.setText(decimalFormat.format(order.getDiscount()) + " d");
        binding.txtTotalPaymentDetails.setText(decimalFormat.format(order.getTotalPrice()) + " d");
    }

    private void observeUserInfo() {
        userInforViewModel.getUserNameLiveData().observe(this, name -> {
            binding.txtNameCustomer.setText(name);
        });

        userInforViewModel.getPhoneNumberLiveData().observe(this, phone -> {
            binding.txtPhone.setText(phone);
        });

        userInforViewModel.getErrorLiveData().observe(this, error -> {
            Toast.makeText(this, "Lỗi tải thông tin người dùng: " + error, Toast.LENGTH_SHORT).show();
        });
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