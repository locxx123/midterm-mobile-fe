package com.example.midtermexercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midtermexercise.adapters.ContactAdapter;
import com.example.midtermexercise.adapters.FavoriteAdapter;
import com.example.midtermexercise.models.User;
import com.example.midtermexercise.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerRecentContacts, recyclerFavorites;
    private ContactAdapter contactAdapter;
    private FavoriteAdapter favoriteAdapter;
    private List<User> userList, favoriteList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // --- RecyclerView GẦN ĐÂY ---
        recyclerRecentContacts = view.findViewById(R.id.rvRecent);
        recyclerRecentContacts.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        userList.add(new User("Nguyễn Văn A", "0912345678", "123456"));
        userList.add(new User("Trần Thị B", "0987654321", "abcdef"));
        userList.add(new User("Phạm Văn C", "0909090909", "654321"));
        userList.add(new User("Lê Thị D", "0933333333", "password"));
        userList.add(new User("Vũ Minh E", "0922222222", "mypassword"));

        contactAdapter = new ContactAdapter(userList);
        recyclerRecentContacts.setAdapter(contactAdapter);


        // --- RecyclerView YÊU THÍCH ---
        recyclerFavorites = view.findViewById(R.id.rvFavorites); // ✅ đúng id
        recyclerFavorites.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        favoriteList = new ArrayList<>();
        favoriteList.add(new User("Nguyễn Văn A", "0912345678", "123456"));
        favoriteList.add(new User("Trần Thị B", "0987654321", "abcdef"));
        favoriteList.add(new User("Phạm Văn C", "0909090909", "654321"));

        favoriteAdapter = new FavoriteAdapter(getContext(), favoriteList);
        recyclerFavorites.setAdapter(favoriteAdapter);

        return view;
    }
}
