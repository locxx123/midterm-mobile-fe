package com.example.midtermexercise.api;

import com.example.midtermexercise.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/signup")
    Call<User> registerUser(@Body User user);

    @POST("auth/login")
    Call<User> loginUser(@Body User user);
}