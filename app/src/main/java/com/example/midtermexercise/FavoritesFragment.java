package com.example.midtermexercise;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midtermexercise.adapters.FavoriteManageAdapter;
import com.example.midtermexercise.api.ApiService;
import com.example.midtermexercise.api.RetrofitClient;
import com.example.midtermexercise.models.ContactResponse;
import com.example.midtermexercise.models.User;
import com.example.midtermexercise.viewmodel.FavoritesViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesFragment extends Fragment {

    private RecyclerView rvFavorites;
    private EditText etSearchFav;
    private ImageButton btnClearSearchFav;
    private TextView tvFavoriteCount;
    private LinearLayout llEmptyFavorites;
    private ProgressBar progressBarFav;

    private FavoriteManageAdapter adapter;
    private final List<User> allList = new ArrayList<>();
    private final List<User> filteredList = new ArrayList<>();

    private FavoritesViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFavorites = view.findViewById(R.id.rvFavorites);
        etSearchFav = view.findViewById(R.id.etSearchFav);
        btnClearSearchFav = view.findViewById(R.id.btnClearSearchFav);
        tvFavoriteCount = view.findViewById(R.id.tvFavoriteCount);
        llEmptyFavorites = view.findViewById(R.id.llEmptyFavorites);
        progressBarFav = view.findViewById(R.id.progressBarFav);

        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FavoriteManageAdapter(filteredList, this::removeFavorite, user -> {
            // Optional: mở chi tiết liên hệ khi bấm item
            ContactDetailFragment detailFragment = new ContactDetailFragment();
            Bundle args = new Bundle();
            args.putString("name", user.getFullName());
            args.putString("phone", user.getPhone());
            args.putString("id", user.getId());
            args.putBoolean("favorite", user.isFavorite());
            detailFragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvFavorites.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        progressBarFav.setVisibility(View.VISIBLE);
        llEmptyFavorites.setVisibility(View.GONE);

        viewModel.getFavorites().observe(getViewLifecycleOwner(), list -> {
            progressBarFav.setVisibility(View.GONE);
            allList.clear();
            if (list != null) allList.addAll(list);

            filteredList.clear();
            filteredList.addAll(allList);
            adapter.notifyDataSetChanged();

            updateCount();
            checkEmpty();
        });

        viewModel.loadFavorites(getContext());

        etSearchFav.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }
        });

        btnClearSearchFav.setOnClickListener(v -> {
            etSearchFav.setText("");
            btnClearSearchFav.setVisibility(View.GONE);
        });
    }

    private void filterList(String query) {
        filteredList.clear();
        if (!query.isEmpty()) {
            btnClearSearchFav.setVisibility(View.VISIBLE);
            for (User u : allList) {
                if ((u.getFullName() != null && u.getFullName().toLowerCase().contains(query.toLowerCase()))
                        || (u.getPhone() != null && u.getPhone().contains(query))) {
                    filteredList.add(u);
                }
            }
        } else {
            btnClearSearchFav.setVisibility(View.GONE);
            filteredList.addAll(allList);
        }
        adapter.notifyDataSetChanged();
        updateCount();
        checkEmpty();
    }

    private void updateCount() {
        tvFavoriteCount.setText(filteredList.size() + " mục yêu thích");
    }

    private void checkEmpty() {
        boolean empty = filteredList.isEmpty();
        llEmptyFavorites.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvFavorites.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void removeFavorite(User user) {
        if (getContext() == null || user == null || user.getId() == null) return;
        ApiService api = RetrofitClient.getApiService(getContext());
        api.removeFavorite(user.getId()).enqueue(new Callback<ContactResponse>() {
            @Override
            public void onResponse(Call<ContactResponse> call, Response<ContactResponse> response) {
                if (response.isSuccessful()) {
                    allList.removeIf(u -> user.getId().equals(u.getId()));
                    filteredList.removeIf(u -> user.getId().equals(u.getId()));
                    adapter.notifyDataSetChanged();
                    updateCount();
                    checkEmpty();
                    Toast.makeText(getContext(), "Đã xoá khỏi yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Xoá thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ContactResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null) viewModel.loadFavorites(getContext());
    }
}