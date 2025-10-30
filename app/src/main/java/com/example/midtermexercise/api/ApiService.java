package com.example.midtermexercise.api;

import com.example.midtermexercise.models.GroupRequest;
import com.example.midtermexercise.models.GroupResponse;
import com.example.midtermexercise.models.User;
import com.example.midtermexercise.models.FavoritesResponse;
import com.example.midtermexercise.models.ContactResponse;
import com.example.midtermexercise.models.DeleteContactResponse;
import com.example.midtermexercise.models.GroupDeleteResponse;
import com.example.midtermexercise.models.GroupAddResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.DELETE;
import retrofit2.http.PUT;
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

    @POST("phone/add")
    Call<User> addContact(@Body Map<String, String> body);

    @PUT("phone/update/{id}")
    Call<ContactResponse> updateContact(@Path("id") String id, @Body Map<String, String> body);

    @DELETE("phone/delete/{id}")
    Call<DeleteContactResponse> deleteContact(@Path("id") String id);

    @GET("phone/groups")
    Call<GroupResponse> getGroups();

    @POST("phone/groups")
    Call<GroupResponse.SingleGroup> createGroup(@Body GroupRequest request);

    @DELETE("phone/groups/{groupId}")
    Call<GroupDeleteResponse> deleteGroup(@Path("groupId") String groupId);

    @POST("phone/groups/{groupId}/add")
    Call<GroupAddResponse> addContactToGroup(@Path("groupId") String groupId, @Body Map<String, String> body);

    // Remove Contact from Group: POST /phone/groups/{groupId}/remove
    @POST("phone/groups/{groupId}/remove")
    Call<GroupAddResponse> removeContactFromGroup(@Path("groupId") String groupId, @Body Map<String, String> body);
}