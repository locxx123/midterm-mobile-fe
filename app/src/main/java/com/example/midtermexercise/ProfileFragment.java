package com.example.midtermexercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private ImageView imgProfileAvatar;
    private CardView btnEditAvatar;
    private TextView tvProfileName, tvProfileStatus, tvPhoneNumber, tvEmail;
    private SwitchCompat switchNotifications, switchDarkMode;

    private LinearLayout llEditProfile, llPhoneNumber, llEmail,
            llNotifications, llDarkMode, llBackup, llPrivacy,
            llHelp, llAbout, llLogout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Ánh xạ View ---
        imgProfileAvatar = view.findViewById(R.id.imgProfileAvatar);
        btnEditAvatar = view.findViewById(R.id.btnEditAvatar);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileStatus = view.findViewById(R.id.tvProfileStatus);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        tvEmail = view.findViewById(R.id.tvEmail);

        switchNotifications = view.findViewById(R.id.switchNotifications);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);

        llEditProfile = view.findViewById(R.id.llEditProfile);
        llPhoneNumber = view.findViewById(R.id.llPhoneNumber);
        llEmail = view.findViewById(R.id.llEmail);
        llNotifications = view.findViewById(R.id.llNotifications);
        llDarkMode = view.findViewById(R.id.llDarkMode);
        llBackup = view.findViewById(R.id.llBackup);
        llPrivacy = view.findViewById(R.id.llPrivacy);
        llHelp = view.findViewById(R.id.llHelp);
        llAbout = view.findViewById(R.id.llAbout);
        llLogout = view.findViewById(R.id.llLogout);

        // --- Gán sự kiện ---
        setupListeners();
    }

    private void setupListeners() {

        btnEditAvatar.setOnClickListener(v ->
                Toast.makeText(getContext(), "Chỉnh sửa ảnh đại diện", Toast.LENGTH_SHORT).show()
        );

        llEditProfile.setOnClickListener(v ->
                Toast.makeText(getContext(), "Mở trang chỉnh sửa hồ sơ", Toast.LENGTH_SHORT).show()
        );

        llPhoneNumber.setOnClickListener(v ->
                Toast.makeText(getContext(), "Cập nhật số điện thoại", Toast.LENGTH_SHORT).show()
        );

        llEmail.setOnClickListener(v ->
                Toast.makeText(getContext(), "Cập nhật email", Toast.LENGTH_SHORT).show()
        );

        llBackup.setOnClickListener(v ->
                Toast.makeText(getContext(), "Sao lưu & đồng bộ", Toast.LENGTH_SHORT).show()
        );

        llPrivacy.setOnClickListener(v ->
                Toast.makeText(getContext(), "Cài đặt quyền riêng tư", Toast.LENGTH_SHORT).show()
        );

        llHelp.setOnClickListener(v ->
                Toast.makeText(getContext(), "Trợ giúp & hỗ trợ", Toast.LENGTH_SHORT).show()
        );

        llAbout.setOnClickListener(v ->
                Toast.makeText(getContext(), "Thông tin ứng dụng", Toast.LENGTH_SHORT).show()
        );

        llLogout.setOnClickListener(v ->
                Toast.makeText(getContext(), "Đăng xuất tài khoản", Toast.LENGTH_SHORT).show()
        );

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String msg = isChecked ? "Bật thông báo" : "Tắt thông báo";
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String msg = isChecked ? "Bật chế độ tối" : "Tắt chế độ tối";
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });
    }
}
