package com.example.midtermexercise.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.midtermexercise.api.RetrofitClient;
import com.example.midtermexercise.models.FavoritesResponse;
import com.example.midtermexercise.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesViewModel extends ViewModel {

    private final MutableLiveData<List<User>> favorites = new MutableLiveData<>();

    public LiveData<List<User>> getFavorites() {
        return favorites;
    }

    public void loadFavorites(Context context) {
        RetrofitClient.getApiService(context).getFavorites().enqueue(new Callback<FavoritesResponse>() {
            @Override
            public void onResponse(Call<FavoritesResponse> call, Response<FavoritesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favorites.postValue(response.body().getFavorites());
                }
            }

            @Override
            public void onFailure(Call<FavoritesResponse> call, Throwable t) { }
        });
    }
}