package com.example.ktop_food_app_store.model.repository;

import com.example.ktop_food_app_store.model.data.remote.FirebaseUserInforData;

public class UserInforRepository {
    private final FirebaseUserInforData firebaseUserInforData;

    public UserInforRepository() {
        firebaseUserInforData = new FirebaseUserInforData();
    }

    public void getUserInfo(String uid, FirebaseUserInforData.UserInfoCallback callback) {
        firebaseUserInforData.getUserInfo(uid, callback);
    }
}
