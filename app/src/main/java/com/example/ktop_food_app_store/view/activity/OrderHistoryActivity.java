package com.example.ktop_food_app_store.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ktop_food_app_store.model.data.entity.Order;
import com.example.ktop_food_app_store.view.adapter.OrderAdapter;
import com.example.ktop_food_app_store.viewmodel.OrderViewModel;
import com.example.ktop_food_app_store.databinding.ActivityOrderHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity implements OrderAdapter.OnOrderClickListener {

    private final List<Order> orderList = new ArrayList<>();
    private ActivityOrderHistoryBinding binding;
    private OrderViewModel orderViewModel;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        initRecyclerView();
        initViewModel();
        setupListeners();
    }

    private void initRecyclerView() {
        // Use VIEW_TYPE_ORDER_HISTORY for OrderHistoryActivity
        adapter = new OrderAdapter(this, orderList, this, OrderAdapter.VIEW_TYPE_ORDER_HISTORY);
        binding.recyclerViewOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewOrderHistory.setAdapter(adapter);
    }

    private void initViewModel() {
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.getOrderListLiveData().observe(this, orders -> {
            orderList.clear();
            // Filter for completed orders only
            for (Order order : orders) {
                if ("completed".equalsIgnoreCase(order.getStatus()) || "cancelled".equals(order.getStatus())) {
                    orderList.add(order);
                }
            }
            adapter.notifyDataSetChanged();
        });

        orderViewModel.getErrorMassage().observe(this, error -> {
            Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    @Override
    public void onOrderClick(Order order) {
        Intent intent = new Intent(this, OrderHistoryDetailsActivity.class);
        intent.putExtra("order", order);
        startActivity(intent);
    }
}