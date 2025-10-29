package com.example.midtermexercise.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Group {

    // Các trường này dùng để nhận dữ liệu từ API
    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("photo")
    private String photo;

    // Dùng cho API 'Get All Groups'
    @SerializedName("contacts")
    private List<User> contacts; // Giả sử bạn đã có model 'User'

    /**
     * Constructor này dùng để TẠO một đối tượng Group mới và gửi lên server.
     * Dựa trên API, chỉ cần 'name' và 'photo' (tùy chọn).
     */
    public Group(String name, String photo) {
        this.name = name;
        this.photo = photo; // photo có thể là null
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public List<User> getContacts() {
        return contacts;
    }

    /**
     * Phương thức tiện ích để hiển thị số lượng thành viên.
     * Dữ liệu này được tính từ danh sách 'contacts' mà API trả về.
     */
    public String getMemberCount() {
        if (contacts != null) {
            return contacts.size() + " members";
        }
        return "0 members";
    }
}
