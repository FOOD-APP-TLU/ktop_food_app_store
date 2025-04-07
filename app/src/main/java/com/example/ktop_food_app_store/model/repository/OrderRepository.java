package com.example.ktop_food_app_store.model.repository;

import com.example.ktop_food_app_store.model.data.remote.FirebaseOrderData;

public class OrderRepository {
    private final FirebaseOrderData firebaseOrderData;

    public OrderRepository() {
        this.firebaseOrderData = new FirebaseOrderData();
    }

    public void getOrderList(FirebaseOrderData.OrderCallback callback) {

        firebaseOrderData.getOrders(callback);
    }
}

