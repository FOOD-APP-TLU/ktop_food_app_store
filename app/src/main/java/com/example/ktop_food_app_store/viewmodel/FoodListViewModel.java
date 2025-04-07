package com.example.ktop_food_app_store.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ktop_food_app_store.model.data.entity.Food;
import com.example.ktop_food_app_store.model.data.remote.FirebaseFoodData;
import com.example.ktop_food_app_store.model.repository.FoodRepository;

import java.util.List;

public class FoodListViewModel extends ViewModel {

    private FoodRepository foodRepository;
    private MutableLiveData<List<Food>> foodListLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> deleteSuccessLiveData = new MutableLiveData<>();

    public FoodListViewModel() {
        foodRepository = new FoodRepository();
    }

    public LiveData<List<Food>> getFoodList() {
        return foodListLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessageLiveData;
    }

    public LiveData<Boolean> getDeleteSuccess() {
        return deleteSuccessLiveData;
    }

    // Lấy danh sách món ăn
    public void fetchFoodItems() {
        foodRepository.fetchFoodItems(new FirebaseFoodData.FoodDataCallback() {
            @Override
            public void onSuccess(List<Food> foodList) {
                foodListLiveData.setValue(foodList);
            }

            @Override
            public void onError(String errorMessage) {
                errorMessageLiveData.setValue(errorMessage);
            }
        });
    }

    // Xóa món ăn
    public void deleteFood(String foodId) {
        foodRepository.deleteFood(foodId, new FirebaseFoodData.DeleteFoodCallback() {
            @Override
            public void onSuccess() {
                deleteSuccessLiveData.setValue(true);
            }

            @Override
            public void onError(String errorMessage) {
                errorMessageLiveData.setValue(errorMessage);
            }
        });
    }
}