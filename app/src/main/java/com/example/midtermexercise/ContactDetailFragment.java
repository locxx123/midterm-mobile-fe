package com.example.midtermexercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.midtermexercise.api.RetrofitClient;
import com.example.midtermexercise.api.ApiService;
import com.example.midtermexercise.models.ContactResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactDetailFragment extends Fragment {

    private TextView tvFullName, tvPhone, tvNote, tvToggleFavorite;
    private ImageView imgAvatar;

    private String contactId;
    private boolean isFavorite;

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
        tvToggleFavorite = view.findViewById(R.id.tvToggleFavorite);

        // Nhận dữ liệu từ Bundle
        if (getArguments() != null) {
            tvFullName.setText(getArguments().getString("name"));
            tvPhone.setText(getArguments().getString("phone"));
            contactId = getArguments().getString("id");
            isFavorite = getArguments().getBoolean("favorite", false);
        }

        updateFavoriteActionText();

        tvToggleFavorite.setOnClickListener(v -> toggleFavorite());

        return view;
    }

    private void updateFavoriteActionText() {
        if (tvToggleFavorite != null) {
            tvToggleFavorite.setText(isFavorite ? "Xoá khỏi Mục ưa thích" : "Thêm vào Mục ưa thích");
        }
    }

    private void toggleFavorite() {
        if (contactId == null || getContext() == null) {
            Toast.makeText(getContext(), "Thiếu thông tin liên hệ", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiService api = RetrofitClient.getApiService(getContext());
        Call<ContactResponse> call = isFavorite
                ? api.removeFavorite(contactId)
                : api.addFavorite(contactId);

        call.enqueue(new Callback<ContactResponse>() {
            @Override
            public void onResponse(Call<ContactResponse> call, Response<ContactResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getContact() != null) {
                    isFavorite = response.body().getContact().isFavorite();
                    updateFavoriteActionText();
                    Toast.makeText(getContext(), isFavorite ? "Đã thêm vào yêu thích" : "Đã xoá khỏi yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Thao tác thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ContactResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}