package com.example.ktop_food_app_store.model.data.remote;

import androidx.annotation.NonNull;

import com.example.ktop_food_app_store.model.data.entity.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseOrderData {
    private final DatabaseReference orderRef;

    public FirebaseOrderData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://ktop-food-database-default-rtdb.asia-southeast1.firebasedatabase.app");
        orderRef = database.getReference("orders");
    }

    public void getOrders(OrderCallback callback) {
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> orderList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Order order = data.getValue(Order.class);
                    if (order != null) {
                        order.setOrderId(data.getKey());
                        orderList.add(order);
                    }
                }
                callback.onSuccess(orderList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    // Interface callback để xử lý dữ liệu trả về
    public interface OrderCallback {
        void onSuccess(List<Order> orderList);
        void onFailure(String errorMessage);
    }
}
