package com.example.ktop_food_app_store.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ktop_food_app_store.model.data.entity.Food;
import com.example.ktop_food_app_store.model.data.remote.FirebaseEditItemData;
import com.example.ktop_food_app_store.model.repository.EditItemRepository;


import java.util.ArrayList;
import java.util.List;

public class EditItemViewModel extends ViewModel {
    private Food foodItem;
    private final EditItemRepository repository;
    private final MutableLiveData<Boolean> formValidity = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // Khởi tạo ViewModel với repository
    public EditItemViewModel() {
        repository = new EditItemRepository(new FirebaseEditItemData());
    }

    // Thiết lập món ăn hiện tại
    public void setFoodItem(Food food) {
        this.foodItem = food;
    }

    // Lấy món ăn hiện tại
    public Food getFoodItem() {
        return foodItem;
    }

    // Lấy danh sách tùy chọn thời gian
    public List<String> getTimeOptions() {
        List<String> timeOptions = new ArrayList<>();
        timeOptions.add("Select Time");
        timeOptions.add("5-10 mins");
        timeOptions.add("10-15 mins");
        timeOptions.add("15-20 mins");
        return timeOptions;
    }

    // Lấy danh sách tùy chọn danh mục
    public List<String> getCategoryOptions() {
        List<String> categoryOptions = new ArrayList<>();
        categoryOptions.add("Select Category");
        categoryOptions.add("Spaghetti");
        categoryOptions.add("Hotdog");
        categoryOptions.add("Chicken");
        categoryOptions.add("Hamburger");
        categoryOptions.add("Bingsu");
        categoryOptions.add("Pizza");
        categoryOptions.add("Drink");
        categoryOptions.add("Other");
        return categoryOptions;
    }

    // Kiểm tra tính hợp lệ của biểu mẫu
    public void validateForm(String name, String price, String url, String time, String category) {
        boolean isValid = true;

        if (name.isEmpty() || price.isEmpty() || url.isEmpty() || time.equals("Select Time") || category.equals("Select Category")) {
            isValid = false;
        } else {
            try {
                int priceValue = Integer.parseInt(price);
                if (priceValue <= 0) isValid = false;
            } catch (NumberFormatException e) {
                isValid = false;
            }
        }

        formValidity.setValue(isValid);
    }

    // Lưu món ăn vào cơ sở dữ liệu
    public void saveFoodItem(String name, String priceStr, String url, String description, String time, String category, boolean bestFood) {
        try {
            int price = Integer.parseInt(priceStr);
            int categoryId = getCategoryIdFromName(category);

            // Cập nhật thông tin món ăn
            foodItem.setTitle(name);
            foodItem.setPrice(price);
            foodItem.setImagePath(url);
            foodItem.setDescription(description.isEmpty() ? "" : description);
            foodItem.setTimeValue(time);
            foodItem.setCategoryId(categoryId);
            foodItem.setBestFood(bestFood);

            // Gọi repository để cập nhật món ăn
            repository.updateFoodItem(foodItem,
                    () -> saveSuccess.setValue(true),
                    () -> errorMessage.setValue("Failed to update item"));
        } catch (NumberFormatException e) {
            errorMessage.setValue("Invalid price format");
        }
    }

    // Lấy tên danh mục từ ID
    public String getCategoryNameFromId(int categoryId) {
        switch (categoryId) {
            case 0: return "Spaghetti";
            case 1: return "Hotdog";
            case 2: return "Chicken";
            case 3: return "Hamburger";
            case 4: return "Bingsu";
            case 5: return "Pizza";
            case 6: return "Drink";
            case 7: return "Other";
            default: return "Select Category";
        }
    }

    // Lấy ID danh mục từ tên
    private int getCategoryIdFromName(String categoryName) {
        switch (categoryName) {
            case "Spaghetti": return 0;
            case "Hotdog": return 1;
            case "Chicken": return 2;
            case "Hamburger": return 3;
            case "Bingsu": return 4;
            case "Pizza": return 5;
            case "Drink": return 6;
            case "Other": return 7;
            default: return -1;
        }
    }

    // Lấy trạng thái hợp lệ của biểu mẫu
    public LiveData<Boolean> getFormValidity() {
        return formValidity;
    }

    // Lấy trạng thái lưu thành công
    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccess;
    }

    // Lấy thông báo lỗi
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}