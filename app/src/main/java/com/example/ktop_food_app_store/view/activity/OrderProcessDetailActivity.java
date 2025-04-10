package com.example.ktop_food_app_store.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ktop_food_app_store.model.data.entity.Order;
import com.example.ktop_food_app_store.R;
import com.example.ktop_food_app_store.databinding.ActivityOrderProcessDetailBinding;
import com.example.ktop_food_app_store.view.adapter.OrderProcessDetailAdapter;
import com.example.ktop_food_app_store.viewmodel.UserInforViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderProcessDetailActivity extends AppCompatActivity {

    private ActivityOrderProcessDetailBinding binding;
    private OrderProcessDetailAdapter itemAdapter;
    private Order order;
    private DecimalFormat decimalFormat;
    private DatabaseReference usersRef;
    private UserInforViewModel userInforViewModel;
    private DatabaseReference ordersRef;
    private DatabaseReference userCancelledRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding
        binding = ActivityOrderProcessDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,###", symbols);

        // Initialize ViewModel
        userInforViewModel = new ViewModelProvider(this).get(UserInforViewModel.class);

        usersRef = FirebaseDatabase
                .getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users");

        // Initialize Firebase Database Reference
        ordersRef = FirebaseDatabase.getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("orders");

        // Call methods
        if (!loadOrderData()) {
            return;
        }

        observeUserInfo();
        displayOrderDetails();
        setupRecyclerView();
        setupListeners();
        displayButton();

        userCancelledRef = usersRef.child(order.getUid()).child("cancelled");
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

        // Lấy thông tin người dùng qua ViewModel
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

        double totalPayment = order.getTotalPrice() + order.getDiscount();
        binding.txtTotalPriceItemsAmount.setText(decimalFormat.format(totalPayment) + " đ");
        binding.txtDiscountAmount.setText(decimalFormat.format(order.getDiscount()) + " đ");
        binding.txtTotalPayment.setText(decimalFormat.format(order.getTotalPrice()) + " đ");
    }

    private void observeUserInfo() {
        userInforViewModel.getUserNameLiveData().observe(this, name -> {
            binding.txtUserName.setText(name);
        });

        userInforViewModel.getPhoneNumberLiveData().observe(this, phone -> {
            binding.txtPhoneNumber.setText(phone);
        });

        userInforViewModel.getErrorLiveData().observe(this, error -> {
            Toast.makeText(this, "Lỗi tải thông tin người dùng: " + error, Toast.LENGTH_SHORT).show();
        });
    }

    // Setup RecyclerView
    private void setupRecyclerView() {
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            itemAdapter = new OrderProcessDetailAdapter(this, order.getItems());
            binding.recyclerViewOrderProcessDetal.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerViewOrderProcessDetal.setAdapter(itemAdapter);
        } else {
            Toast.makeText(this, "Đơn hàng không có sản phẩm!", Toast.LENGTH_SHORT).show();
        }
    }

    // Hiển thị nút
    public void displayButton() {
        String status = order.getStatus();
        if ("pending".equalsIgnoreCase(status)) {
            binding.btnOrderDelivery.setVisibility(View.VISIBLE);
            binding.btnCancelOrder.setVisibility(View.VISIBLE);
            binding.btnOrderCompleted.setVisibility(View.GONE);
        } else if ("shipping".equalsIgnoreCase(status)) {
            binding.btnOrderDelivery.setVisibility(View.GONE);
            binding.btnCancelOrder.setVisibility(View.VISIBLE);
            binding.btnOrderCompleted.setVisibility(View.VISIBLE);
        }
    }

    // Setup button listeners
    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnOrderDelivery.setOnClickListener(v -> {
            ordersRef.child(order.getOrderId()).child("status").setValue("shipping")
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Đơn hàng đang trong quá trình vận chuyển", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            finish(); // Quay lại màn hình Order Process
        });

        binding.btnOrderCompleted.setOnClickListener(v -> {
            ordersRef.child(order.getOrderId()).child("status").setValue("completed")
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Đơn hàng đã hoàn thành", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            finish(); // Quay lại màn hình Order Process
        });

        binding.btnCancelOrder.setOnClickListener(v -> {
            new AlertDialog.Builder(OrderProcessDetailActivity.this)
                    .setTitle("Huỷ đơn hàng")
                    .setMessage("Xác nhận đơn hàng này đã bị hủy")
                    .setPositiveButton("Có", (dialog, which) -> {
                        ordersRef.child(order.getOrderId()).child("status").setValue("cancelled")
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(OrderProcessDetailActivity.this, "Đơn hàng đã được huỷ", Toast.LENGTH_SHORT).show();
                                    increaseUserCancelCount();
                                    finish(); // Quay lại màn hình xử lý đơn hàng
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(OrderProcessDetailActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                    .show();
        });

    }

    private void increaseUserCancelCount() {
        DatabaseReference userCancelledRef = usersRef.child(order.getUid()).child("cancelled");
        userCancelledRef.get().addOnSuccessListener(snapshot -> {
            long cancelledCount = 0;
            if (snapshot.exists()) {
                Long value = snapshot.getValue(Long.class);
                if (value != null) {
                    cancelledCount = value;
                }
            }
            userCancelledRef.setValue(cancelledCount + 1);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi cập nhật số lần huỷ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

}