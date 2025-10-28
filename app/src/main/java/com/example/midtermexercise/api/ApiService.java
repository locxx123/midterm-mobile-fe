package com.example.midtermexercise.api;

import com.example.midtermexercise.models.User;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    // Đăng ký
    @POST("auth/signup")
    Call<User> registerUser(@Body User user);

    // Đăng nhập
    @POST("auth/login")
    Call<User> loginUser(@Body User user);

    // ✅ Lấy danh sách liên hệ (GET /phone)
    @GET("phone")
    Call<List<User>> getContacts();

    // ✅ Thêm liên hệ (POST /phone/add)
    @POST("phone/add")
    Call<User> addContact(@Body Map<String, String> body);
}
