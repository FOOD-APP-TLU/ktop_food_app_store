package com.example.ktop_food_app_store.model.data.remote;

import com.example.ktop_food_app_store.model.data.entity.CartItem;
import com.example.ktop_food_app_store.model.data.entity.Order;
import com.example.ktop_food_app_store.model.data.entity.TopSellingItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class FirebaseStatisticData {
    private DatabaseReference ordersRef;

    public FirebaseStatisticData() {
        ordersRef = FirebaseDatabase.getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("orders");
    }

    public void loadStatistics(int selectedYear, int selectedMonth, int selectedDay,
                               Consumer<Double> onMonthlyRevenue,
                               Consumer<Double> onDailyRevenue,
                               Consumer<Integer> onTotalOrders,
                               Consumer<Integer> onCompletedOrders,
                               Consumer<Integer> onCancelledOrders,
                               Consumer<Integer> onTotalCustomers,
                               Consumer<List<TopSellingItem>> onTopSellingItems) {
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double monthlyRevenue = 0;
                double dailyRevenue = 0;
                int totalOrders = 0;
                int completedOrders = 0;
                int cancelledOrders = 0;
                Set<String> customers = new HashSet<>();
                Map<String, Integer> itemQuantities = new HashMap<>();
                Map<String, String> itemImages = new HashMap<>();

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
                Collections.sort(itemList, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

                List<TopSellingItem> topSellingItems = new ArrayList<>();
                for (int i = 0; i < Math.min(5, itemList.size()); i++) {
                    Map.Entry<String, Integer> entry = itemList.get(i);
                    String itemName = entry.getKey();
                    int quantity = entry.getValue();
                    String imageUrl = itemImages.get(itemName);
                    topSellingItems.add(new TopSellingItem(itemName, quantity, imageUrl));
                }

                // Gửi dữ liệu qua callback
                onMonthlyRevenue.accept(monthlyRevenue);
                onDailyRevenue.accept(dailyRevenue);
                onTotalOrders.accept(totalOrders);
                onCompletedOrders.accept(completedOrders);
                onCancelledOrders.accept(cancelledOrders);
                onTotalCustomers.accept(customers.size());
                onTopSellingItems.accept(topSellingItems);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });
    }
}