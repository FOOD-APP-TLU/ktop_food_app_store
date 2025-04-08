package com.example.ktop_food_app_store.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ktop_food_app_store.model.data.remote.FirebaseUserInforData;
import com.example.ktop_food_app_store.model.repository.UserInforRepository;

public class UserInforViewModel extends ViewModel {

    private final MutableLiveData<String> userNameLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> phoneNumberLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private final UserInforRepository userInforRepository = new UserInforRepository();

    public void getUserInfo(String uid) {
        userInforRepository.getUserInfo(uid, new FirebaseUserInforData.UserInfoCallback() {
            @Override
            public void onSuccess(String name, String phone) {
                userNameLiveData.setValue(name != null ? name : "N/A");
                phoneNumberLiveData.setValue(phone != null ? phone : "N/A");
            }

            @Override
            public void onFailure(String errorMessage) {
                errorLiveData.setValue(errorMessage);
            }
        });
    }

    public LiveData<String> getUserNameLiveData() {
        return userNameLiveData;
    }

    public LiveData<String> getPhoneNumberLiveData() {
        return phoneNumberLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
}
