package com.example.midtermexercise.models;

public class User {
    private String _id;
    private String fullName;
    private String phone;
    private String photo;
    private boolean favorite;
    private String password;
    private String token;

    public User() {}

    // Dùng khi tạo user mới để đăng ký
    public User(String fullName, String phone, String password) {
        this.fullName = fullName;
        this.phone = phone;
        this.password = password;
    }

    public String getId() { return _id; }
    public void setId(String _id) { this._id = _id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
