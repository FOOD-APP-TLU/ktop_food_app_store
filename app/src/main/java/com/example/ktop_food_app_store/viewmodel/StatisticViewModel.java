package com.example.ktop_food_app_store.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ktop_food_app_store.model.data.entity.TopSellingItem;
import com.example.ktop_food_app_store.model.repository.StatisticRepository;


import java.util.List;

public class StatisticViewModel extends ViewModel {
    private StatisticRepository repository;

    public StatisticViewModel() {
        repository = new StatisticRepository();
    }

    public void loadStatistics(int selectedYear, int selectedMonth, int selectedDay) {
        repository.loadStatistics(selectedYear, selectedMonth, selectedDay);
    }

    public LiveData<Double> getMonthlyRevenue() {
        return repository.getMonthlyRevenueLiveData();
    }

    public LiveData<Double> getDailyRevenue() {
        return repository.getDailyRevenueLiveData();
    }

    public LiveData<Integer> getTotalOrders() {
        return repository.getTotalOrdersLiveData();
    }

    public LiveData<Integer> getCompletedOrders() {
        return repository.getCompletedOrdersLiveData();
    }

    public LiveData<Integer> getCancelledOrders() {
        return repository.getCancelledOrdersLiveData();
    }

    public LiveData<Integer> getTotalCustomers() {
        return repository.getTotalCustomersLiveData();
    }

    public LiveData<List<TopSellingItem>> getTopSellingItems() {
        return repository.getTopSellingItemsLiveData();
    }
}