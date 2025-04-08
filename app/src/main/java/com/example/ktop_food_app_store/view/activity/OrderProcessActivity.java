package com.example.ktop_food_app_store.view.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ktop_food_app_store.model.data.entity.Order;
import com.example.ktop_food_app_store.view.adapter.OrderAdapter;
import com.example.ktop_food_app_store.viewmodel.OrderViewModel;
import com.example.ktop_food_app_store.databinding.ActivityOrderProcessBinding;


import java.util.ArrayList;
import java.util.List;

public class OrderProcessActivity extends AppCompatActivity implements OrderAdapter.OnOrderClickListener {
    private final List<Order> orderList = new ArrayList<>();
    private ActivityOrderProcessBinding binding;
    private OrderViewModel orderViewModel;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderProcessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initRecyclerView();
        initViewModel();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (orderViewModel != null) {
            orderViewModel.reloadOrders();
        }
    }

    private void initRecyclerView() {
        // Use VIEW_TYPE_TRACK_ORDER for TrackOrderActivity
        adapter = new OrderAdapter(this, orderList, this, OrderAdapter.VIEW_TYPE_ORDER_PROCESS);
        binding.orderProcess.setLayoutManager(new LinearLayoutManager(this));
        binding.orderProcess.setAdapter(adapter);
    }

    private void initViewModel() {
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.getOrderListLiveData().observe(this, orders -> {
            orderList.clear();
            for (Order order : orders) {
                if ("pending".equals(order.getStatus()) || "shipping".equals(order.getStatus())) {
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
        Intent intent = new Intent(OrderProcessActivity.this, OrderProcessDetailActivity.class);
        intent.putExtra("order", order);
        startActivity(intent);
    }
}
