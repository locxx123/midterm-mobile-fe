package com.example.midtermexercise;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.midtermexercise.R;

public class DialpadFragment extends Fragment {

    private TextView tvPhoneNumber;
    private GridLayout dialpadGrid;
    private ImageView btnCall, btnBackspace, btnAddContact;

    private final View.OnClickListener numberClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof TextView) {
                String current = tvPhoneNumber.getText().toString();
                String pressed = ((TextView) v).getText().toString();
                tvPhoneNumber.setText(current + pressed);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialpad, container, false);

        initViews(view);
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        tvPhoneNumber = view.findViewById(R.id.tv_phone_number);
        dialpadGrid = view.findViewById(R.id.dialpad_grid);
        btnCall = view.findViewById(R.id.btn_call);
        btnBackspace = view.findViewById(R.id.btn_backspace);
        btnAddContact = view.findViewById(R.id.btn_add_contact);
    }

    private void setupListeners() {
        // Gán listener cho tất cả nút số trong GridLayout
        int childCount = dialpadGrid.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = dialpadGrid.getChildAt(i);
            if (child instanceof TextView) {
                child.setOnClickListener(numberClickListener);
            }
        }

        // Nút gọi
        btnCall.setOnClickListener(v -> {
            String number = tvPhoneNumber.getText().toString().trim();
            if (TextUtils.isEmpty(number)) {
                Toast.makeText(getContext(), "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Đang gọi: " + number, Toast.LENGTH_SHORT).show();
                // TODO: Thêm logic gọi điện thực tế nếu cần
            }
        });

        // Nút xóa
        btnBackspace.setOnClickListener(v -> {
            String current = tvPhoneNumber.getText().toString();
            if (!TextUtils.isEmpty(current)) {
                tvPhoneNumber.setText(current.substring(0, current.length() - 1));
            }
        });

        // Nút thêm liên hệ
        btnAddContact.setOnClickListener(v ->
                Toast.makeText(getContext(), "Thêm liên hệ", Toast.LENGTH_SHORT).show()
        );
    }
}