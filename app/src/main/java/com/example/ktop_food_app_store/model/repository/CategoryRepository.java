package com.example.ktop_food_app_store.model.repository;

import com.example.ktop_food_app_store.model.data.remote.FirebaseCategoryData;

public class CategoryRepository {
    private final FirebaseCategoryData firebaseCategoryData;

    public CategoryRepository() {
        this.firebaseCategoryData = new FirebaseCategoryData();
    }

    public void getCategoryList(FirebaseCategoryData.CategoryCallback callback) {
        firebaseCategoryData.fetchCategories(callback);
    }
}
