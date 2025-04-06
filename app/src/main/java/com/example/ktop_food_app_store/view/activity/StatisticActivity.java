package com.example.ktop_food_app_store.view.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ktop_food_app_store.R;
import com.example.ktop_food_app_store.model.data.entity.CartItem;
import com.example.ktop_food_app_store.model.data.entity.Order;
import com.example.ktop_food_app_store.model.data.entity.TopSellingItem;
import com.example.ktop_food_app_store.view.adapter.TopSellingAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class StatisticActivity extends AppCompatActivity {
    private TextView dateText, yearlyRevenueText, monthlyRevenueText, totalOrdersText, completedOrdersText,
            cancelledOrdersText, totalCustomersText, title;
    private ImageView prevMonthButton, nextMonthButton, backButton;
    private RecyclerView topSellingRecyclerView;
    private TopSellingAdapter topSellingAdapter;
    private List<TopSellingItem> topSellingItems;
    private DatabaseReference ordersRef;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private DecimalFormat decimalFormat; // Thêm DecimalFormat để định dạng tiền

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        // Khởi tạo các view
        title = findViewById(R.id.title);
        dateText = findViewById(R.id.dateText);
        yearlyRevenueText = findViewById(R.id.yearlyRevenueText);
        monthlyRevenueText = findViewById(R.id.monthlyRevenueText);
        totalOrdersText = findViewById(R.id.totalOrdersText);
        completedOrdersText = findViewById(R.id.completedOrdersText);
        cancelledOrdersText = findViewById(R.id.cancelledOrdersText);
        totalCustomersText = findViewById(R.id.totalCustomersText);
        topSellingRecyclerView = findViewById(R.id.topSellingRecyclerView);
        prevMonthButton = findViewById(R.id.prevMonthButton);
        nextMonthButton = findViewById(R.id.nextMonthButton);
        backButton = findViewById(R.id.backButton);

        // Khởi tạo RecyclerView
        topSellingItems = new ArrayList<>();
        topSellingAdapter = new TopSellingAdapter(this, topSellingItems);
        topSellingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        topSellingRecyclerView.setAdapter(topSellingAdapter);

        // Khởi tạo Firebase
        ordersRef = FirebaseDatabase.getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("orders");

        // Khởi tạo thời gian (lấy ngày hiện tại động từ hệ thống)
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        updateDateText();

        // Khởi tạo DecimalFormat để định dạng tiền
        decimalFormat = new DecimalFormat("#,###");
        decimalFormat.setGroupingSize(3); // Đặt nhóm 3 chữ số

        // Xử lý sự kiện nút điều hướng (chuyển đổi ngày)
        prevMonthButton.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            updateDateText();
            loadStatistics();
        });

        nextMonthButton.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            updateDateText();
            loadStatistics();
        });

        // Xử lý sự kiện nhấn vào dateText để hiển thị DatePickerDialog
        dateText.setOnClickListener(v -> showDatePickerDialog());

        backButton.setOnClickListener(v -> finish());

        // Tải dữ liệu thống kê
        loadStatistics();
    }

    private void updateDateText() {
        dateText.setText(dateFormat.format(calendar.getTime()));
    }

    // Hiển thị DatePickerDialog
    private void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Cập nhật calendar với ngày được chọn
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                    updateDateText();
                    loadStatistics(); // Tải lại thống kê với ngày mới
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    // Phương thức định dạng tiền thành dạng 100.000 đ
    private String formatCurrency(double amount) {
        return decimalFormat.format(amount) + " đ";
    }

    private void loadStatistics() {
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double monthlyRevenue = 0; // Doanh thu tháng được chọn
                double dailyRevenue = 0; // Doanh thu ngày được chọn
                int totalOrders = 0;
                int completedOrders = 0;
                int cancelledOrders = 0;
                Set<String> customers = new HashSet<>();
                Map<String, Integer> itemQuantities = new HashMap<>();
                Map<String, String> itemImages = new HashMap<>();

                // Lấy thông tin ngày được chọn
                int selectedYear = calendar.get(Calendar.YEAR);
                int selectedMonth = calendar.get(Calendar.MONTH); // 0-11
                int selectedDay = calendar.get(Calendar.DAY_OF_MONTH);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order == null) continue;

                    // Chuyển timestamp createdAt thành ngày tháng
                    Calendar orderCalendar = Calendar.getInstance();
                    orderCalendar.setTimeInMillis(order.getCreatedAt());

                    int orderYear = orderCalendar.get(Calendar.YEAR);
                    int orderMonth = orderCalendar.get(Calendar.MONTH);
                    int orderDay = orderCalendar.get(Calendar.DAY_OF_MONTH);

                    // Thống kê doanh thu tháng được chọn
                    if (order.getStatus().equals("completed") && orderYear == selectedYear && orderMonth == selectedMonth) {
                        monthlyRevenue += order.getTotalPrice();
                    }

                    // Thống kê đơn hàng trong ngày được chọn
                    if (orderYear == selectedYear && orderMonth == selectedMonth && orderDay == selectedDay) {
                        totalOrders++;
                        if (order.getStatus().equals("completed")) {
                            completedOrders++;
                            dailyRevenue += order.getTotalPrice();
                        } else if (order.getStatus().equals("cancelled")) {
                            cancelledOrders++;
                        }

                        // Thống kê khách hàng
                        customers.add(order.getUid());

                        // Thống kê món ăn bán chạy (chỉ tính trong ngày được chọn)
                        for (CartItem item : order.getItems()) {
                            String itemName = item.getName();
                            itemQuantities.put(itemName, itemQuantities.getOrDefault(itemName, 0) + item.getQuantity());
                            itemImages.put(itemName, item.getImagePath());
                        }
                    }
                }

                // Tìm top 5 món ăn bán chạy nhất
                List<Map.Entry<String, Integer>> itemList = new ArrayList<>(itemQuantities.entrySet());
                Collections.sort(itemList, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())); // Sắp xếp giảm dần theo số lượng

                // Cập nhật danh sách top 5 món ăn
                topSellingItems.clear();
                for (int i = 0; i < Math.min(5, itemList.size()); i++) {
                    Map.Entry<String, Integer> entry = itemList.get(i);
                    String itemName = entry.getKey();
                    int quantity = entry.getValue();
                    String imageUrl = itemImages.get(itemName);
                    topSellingItems.add(new TopSellingItem(itemName, quantity, imageUrl));
                }
                topSellingAdapter.notifyDataSetChanged();

                // Cập nhật giao diện với định dạng tiền mới
                yearlyRevenueText.setText(formatCurrency(monthlyRevenue));
                monthlyRevenueText.setText(formatCurrency(dailyRevenue));
                totalOrdersText.setText(String.valueOf(totalOrders));
                completedOrdersText.setText(String.valueOf(completedOrders));
                cancelledOrdersText.setText(String.valueOf(cancelledOrders));
                totalCustomersText.setText(String.valueOf(customers.size()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });
    }
}