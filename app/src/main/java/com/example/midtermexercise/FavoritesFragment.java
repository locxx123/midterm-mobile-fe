package com.example.midtermexercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midtermexercise.adapters.FavoriteAdapter;
import com.example.midtermexercise.models.User;
import com.example.midtermexercise.viewmodel.FavoritesViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView rvFavorites;
    private TextView tvEmptyFavorites;
    private FavoriteAdapter adapter;
    private final List<User> data = new ArrayList<>();
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
        tvEmptyFavorites = view.findViewById(R.id.tvEmptyFavorites);

        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new FavoriteAdapter(requireContext(), data);
        rvFavorites.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        viewModel.getFavorites().observe(getViewLifecycleOwner(), list -> {
            data.clear();
            if (list != null) data.addAll(list);
            adapter.notifyDataSetChanged();

            boolean empty = data.isEmpty();
            tvEmptyFavorites.setVisibility(empty ? View.VISIBLE : View.GONE);
            rvFavorites.setVisibility(empty ? View.GONE : View.VISIBLE);
        });

        viewModel.loadFavorites(getContext());
    }
}