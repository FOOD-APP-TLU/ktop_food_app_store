package com.example.ktop_food_app_store.viewmodel;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.ktop_food_app_store.model.repository.AddItemRepository;

import java.util.HashMap;
import java.util.Map;

public class AddItemViewModel extends ViewModel {

    private final AddItemRepository repository;
    private final MutableLiveData<Boolean> isFormValid = new MutableLiveData<>(false);
    private final MutableLiveData<String> itemNameError = new MutableLiveData<>();
    private final MutableLiveData<String> itemPriceError = new MutableLiveData<>();
    private final MutableLiveData<String> urlImageError = new MutableLiveData<>();
    private final MutableLiveData<String> timeError = new MutableLiveData<>();
    private final MutableLiveData<String> categoryError = new MutableLiveData<>();
    private final MutableLiveData<String> addItemResult = new MutableLiveData<>();

    private String itemName = "";
    private String itemPrice = "";
    private String urlImage = "";
    private String time = "Select Time";
    private String category = "Select Category";
    private String description = "";

    public AddItemViewModel(AddItemRepository repository) {
        this.repository = repository;
    }

    public LiveData<Boolean> getIsFormValid() {
        return isFormValid;
    }

    public LiveData<String> getItemNameError() {
        return itemNameError;
    }

    public LiveData<String> getItemPriceError() {
        return itemPriceError;
    }

    public LiveData<String> getUrlImageError() {
        return urlImageError;
    }

    public LiveData<String> getTimeError() {
        return timeError;
    }

    public LiveData<String> getCategoryError() {
        return categoryError;
    }

    public LiveData<String> getAddItemResult() {
        return addItemResult;
    }

    public TextWatcher getTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateFields();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    public AdapterView.OnItemSelectedListener getSpinnerListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validateFields();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                validateFields();
            }
        };
    }

    public void setItemName(String itemName) {
        this.itemName = itemName.trim();
        validateFields();
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice.trim();
        validateFields();
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage.trim();
        validateFields();
    }

    public void setTime(String time) {
        this.time = time;
        validateFields();
    }

    public void setCategory(String category) {
        this.category = category;
        validateFields();
    }

    public void setDescription(String description) {
        this.description = description.trim();
    }

    private void validateFields() {
        itemNameError.setValue(null);
        itemPriceError.setValue(null);
        urlImageError.setValue(null);
        timeError.setValue("");
        categoryError.setValue("");

        boolean isValid = true;

        if (itemName.isEmpty()) {
            itemNameError.setValue("Please enter item name");
            isValid = false;
        }
        if (itemPrice.isEmpty()) {
            itemPriceError.setValue("Please enter item price");
            isValid = false;
        }
        if (urlImage.isEmpty()) {
            urlImageError.setValue("Please enter image URL");
            isValid = false;
        }
        if (time.equals("Select Time")) {
            timeError.setValue("Please select preparation time");
            isValid = false;
        }
        if (category.equals("Select Category")) {
            categoryError.setValue("Please select category");
            isValid = false;
        }
        if (isValid) {
            try {
                Double.parseDouble(itemPrice.replaceAll("[^0-9.]", ""));
            } catch (NumberFormatException e) {
                itemPriceError.setValue("Invalid price format");
                isValid = false;
            }
        }

        isFormValid.setValue(isValid);
    }

    public void addItem() {
        if (!isFormValid.getValue()) {
            return;
        }

        double itemPriceValue = Double.parseDouble(itemPrice.replaceAll("[^0-9.]", ""));
        int categoryId = getCategoryId(category);

        Map<String, Object> newFood = createFoodMap(itemName, itemPriceValue, urlImage, time, categoryId, description);
        repository.addItem(newFood).observeForever(result -> addItemResult.setValue(result));
    }

    private int getCategoryId(String category) {
        switch (category) {
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

    private Map<String, Object> createFoodMap(String itemName, double itemPrice, String urlImage, String time, int categoryId, String description) {
        Map<String, Object> newFood = new HashMap<>();
        newFood.put("bestFood", false);
        newFood.put("categoryId", categoryId);
        newFood.put("description", description.isEmpty() ? "No description" : description);
        newFood.put("imagePath", urlImage);
        newFood.put("price", itemPrice);
        newFood.put("star", 4.0);
        newFood.put("timeValue", time);
        newFood.put("title", itemName);
        return newFood;
    }
}