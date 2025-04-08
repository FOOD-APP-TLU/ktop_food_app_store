package com.example.ktop_food_app_store.model.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ktop_food_app_store.model.data.remote.FirebaseAddItemData;

import java.util.Map;

public class AddItemRepository {

    private final FirebaseAddItemData firebaseAddItemData;

    public AddItemRepository() {
        this.firebaseAddItemData = new FirebaseAddItemData();
    }

    public LiveData<String> addItem(Map<String, Object> newFood) {
        MutableLiveData<String> result = new MutableLiveData<>();
        firebaseAddItemData.addItem(newFood, result);
        return result;
    }
}