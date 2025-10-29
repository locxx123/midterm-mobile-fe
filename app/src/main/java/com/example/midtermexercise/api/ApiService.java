package com.example.midtermexercise.api;

import androidx.constraintlayout.widget.Group;

import com.example.midtermexercise.models.GroupRequest;
import com.example.midtermexercise.models.GroupResponse;
import com.example.midtermexercise.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/signup")
    Call<User> registerUser(@Body User user);

    @POST("auth/login")
    Call<User> loginUser(@Body User user);

    @GET("phone")
    Call<List<User>> getContacts();

    @GET("phone/groups")
    Call<GroupResponse> getGroups(@Header("Authorization") String token);


    @POST("phone/groups")
    Call<GroupResponse.SingleGroup> createGroup(
            @Header("Authorization") String token,
            @Body GroupRequest group
    );

}