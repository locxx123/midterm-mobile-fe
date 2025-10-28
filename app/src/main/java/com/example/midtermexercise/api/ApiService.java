package com.example.midtermexercise.api;

import com.example.midtermexercise.models.User;
import com.example.midtermexercise.models.FavoritesResponse;
import com.example.midtermexercise.models.ContactResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface ApiService {

    @POST("auth/signup")
    Call<User> registerUser(@Body User user);

    @POST("auth/login")
    Call<User> loginUser(@Body User user);

    @GET("phone")
    Call<List<User>> getContacts();

    @GET("phone/favorites")
    Call<FavoritesResponse> getFavorites();

    @POST("phone/{id}/favorite")
    Call<ContactResponse> addFavorite(@Path("id") String id);

    @DELETE("phone/{id}/favorite")
    Call<ContactResponse> removeFavorite(@Path("id") String id);
}