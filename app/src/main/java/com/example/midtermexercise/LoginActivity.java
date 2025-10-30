package com.example.midtermexercise;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.midtermexercise.api.ApiService;
import com.example.midtermexercise.api.RetrofitClient;
import com.example.midtermexercise.models.User;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etPhoneLogin, etPasswordLogin;
    private MaterialButton btnLogin;
    private TextView tvGoToRegister;
    private ProgressBar progressSpinner;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ view
        etPhoneLogin = findViewById(R.id.etPhoneLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        progressSpinner = findViewById(R.id.progressSpinner);

        // Khởi tạo Retrofit service
        apiService = RetrofitClient.getApiService(this);

        // Xử lý đăng nhập
        btnLogin.setOnClickListener(v -> validateLogin());

        // Chuyển sang màn hình đăng ký
        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void validateLogin() {
        String phone = etPhoneLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

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

        loginUser(phone, password);
    }

    private void setLoadingState(boolean isLoading) {
        btnLogin.setEnabled(!isLoading);
        btnLogin.setAlpha(isLoading ? 0.6f : 1f);
        progressSpinner.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void loginUser(String phone, String password) {
        User user = new User();
        user.setPhone(phone);
        user.setPassword(password);

        Log.d("LoginActivity", "Bắt đầu gọi API đăng nhập...");
        setLoadingState(true); // ✅ disable + hiện spinner

        apiService.loginUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                setLoadingState(false);

                if (response.isSuccessful() && response.body() != null) {
                    User loggedInUser = response.body();
                    String token = loggedInUser.getToken();
                    Log.d("LoginActivity", "Token nhận được: " + loggedInUser.getToken());
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    getSharedPreferences("app_prefs", MODE_PRIVATE)
                            .edit()
                            .putBoolean("isLoggedIn", true)
                            .putString("token", token)
                            .apply();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else {
                    Log.e("LoginActivity", "Đăng nhập thất bại: " + response.code());
                    Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                setLoadingState(false);
                Log.e("LoginActivity", "Lỗi kết nối API: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}