package com.example.midtermexercise;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;

import com.example.midtermexercise.api.RetrofitClient;
import com.example.midtermexercise.api.ApiService;
import com.example.midtermexercise.models.User;
import com.google.gson.Gson;

import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etPhoneRegister, etPasswordRegister, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvGoToLogin;

    private ApiService apiService; // <-- Retrofit service

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ view
        etFullName = findViewById(R.id.etFullName);
        etPhoneRegister = findViewById(R.id.etPhoneRegister);
        etPasswordRegister = findViewById(R.id.etPasswordRegister);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Khởi tạo API (sử dụng RetrofitClient có CookieManager)
        apiService = (ApiService) RetrofitClient.getApiService(this);

        // Xử lý khi nhấn Đăng ký
        btnRegister.setOnClickListener(v -> validateRegister());

        // Chuyển sang màn hình đăng nhập
        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });
    }

    private void validateRegister() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhoneRegister.getText().toString().trim();
        String password = etPasswordRegister.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // ✅ Kiểm tra dữ liệu
        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fullName.length() < 3) {
            Toast.makeText(this, "Họ tên phải có ít nhất 3 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.matches("^0\\d{9}$")) {
            Toast.makeText(this, "Số điện thoại không hợp lệ (phải gồm 10 số)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Gọi API đăng ký
        registerUser(fullName, phone, password);
    }

    private void registerUser(String fullName, String phone, String password) {
        User newUser = new User(fullName, phone, password);
        Log.d("RegisterActivity", "Bắt đầu gọi API đăng ký");
        Call<User> call = apiService.registerUser(newUser);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("RegisterActivity", "Status code: " + response.code());
                try {
                    Log.d("RegisterActivity", "Response body: " + new Gson().toJson(response.body()));
                    Log.d("RegisterActivity", "Error body: " + response.errorBody().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Có lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("RegisterActivity", "Lỗi gọi API: " + t.getMessage());
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
