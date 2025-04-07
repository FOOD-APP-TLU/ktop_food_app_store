package com.example.ktop_food_app_store.model.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ktop_food_app_store.model.data.entity.TopSellingItem;
import com.example.ktop_food_app_store.model.data.remote.FirebaseStatisticData;

import java.util.List;

public class StatisticRepository {
    private FirebaseStatisticData firebaseDataSource;
    private MutableLiveData<Double> monthlyRevenueLiveData = new MutableLiveData<>();
    private MutableLiveData<Double> dailyRevenueLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> totalOrdersLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> completedOrdersLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> cancelledOrdersLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> totalCustomersLiveData = new MutableLiveData<>();
    private MutableLiveData<List<TopSellingItem>> topSellingItemsLiveData = new MutableLiveData<>();

    public StatisticRepository() {
        firebaseDataSource = new FirebaseStatisticData();
    }

    public void loadStatistics(int selectedYear, int selectedMonth, int selectedDay) {
        firebaseDataSource.loadStatistics(selectedYear, selectedMonth, selectedDay,
                monthlyRevenue -> monthlyRevenueLiveData.setValue(monthlyRevenue),
                dailyRevenue -> dailyRevenueLiveData.setValue(dailyRevenue),
                totalOrders -> totalOrdersLiveData.setValue(totalOrders),
                completedOrders -> completedOrdersLiveData.setValue(completedOrders),
                cancelledOrders -> cancelledOrdersLiveData.setValue(cancelledOrders),
                totalCustomers -> totalCustomersLiveData.setValue(totalCustomers),
                topSellingItems -> topSellingItemsLiveData.setValue(topSellingItems));
    }

    public LiveData<Double> getMonthlyRevenueLiveData() {
        return monthlyRevenueLiveData;
    }

    public LiveData<Double> getDailyRevenueLiveData() {
        return dailyRevenueLiveData;
    }

    public LiveData<Integer> getTotalOrdersLiveData() {
        return totalOrdersLiveData;
    }

    public LiveData<Integer> getCompletedOrdersLiveData() {
        return completedOrdersLiveData;
    }

    public LiveData<Integer> getCancelledOrdersLiveData() {
        return cancelledOrdersLiveData;
    }

    public LiveData<Integer> getTotalCustomersLiveData() {
        return totalCustomersLiveData;
    }

    public LiveData<List<TopSellingItem>> getTopSellingItemsLiveData() {
        return topSellingItemsLiveData;
    }
}
