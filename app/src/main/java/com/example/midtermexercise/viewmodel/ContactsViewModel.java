package com.example.midtermexercise.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.midtermexercise.api.RetrofitClient;
import com.example.midtermexercise.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsViewModel extends ViewModel {

    private final MutableLiveData<List<User>> contacts = new MutableLiveData<>();

    public LiveData<List<User>> getContacts() {
        return contacts;
    }

    public void loadContacts(Context context) {
        if (contacts.getValue() != null && !contacts.getValue().isEmpty()) return;

        RetrofitClient.getApiService(context).getContacts().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contacts.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {}
        });
    }
}
