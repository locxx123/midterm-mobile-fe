package com.example.midtermexercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ContactDetailFragment extends Fragment {

    private TextView tvFullName, tvPhone, tvNote;
    private ImageView imgAvatar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_detail, container, false);

        tvFullName = view.findViewById(R.id.tvFullName);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvNote = view.findViewById(R.id.tvNote);
        imgAvatar = view.findViewById(R.id.imgAvatar);

        // Nhận dữ liệu từ Bundle
        if (getArguments() != null) {
            tvFullName.setText(getArguments().getString("name"));
            tvPhone.setText(getArguments().getString("phone"));
        }

        return view;
    }
}
