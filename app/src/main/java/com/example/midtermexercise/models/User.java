package com.example.midtermexercise.models;

public class User {
    private String fullName;
    private String phone;
    private String password;
    private String token;

    public User() {}

    public User(String fullName, String phone, String password) {
        this.fullName = fullName;
        this.phone = phone;
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // ✅ Thêm phần này
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
