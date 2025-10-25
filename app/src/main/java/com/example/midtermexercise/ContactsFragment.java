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

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midtermexercise.adapters.ContactAdapter;
import com.example.midtermexercise.models.User;
import com.example.midtermexercise.viewmodel.ContactsViewModel;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<User> userList = new ArrayList<>();
    private List<User> filteredList = new ArrayList<>();

    private EditText etSearch;
    private ImageButton btnClearSearch;
    private TextView tvContactCount;
    private LinearLayout llEmptyState;
    private ProgressBar progressBar;

    private ContactsViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        // Ánh xạ view
        recyclerView = view.findViewById(R.id.rvContacts);
        etSearch = view.findViewById(R.id.etSearch);
        btnClearSearch = view.findViewById(R.id.btnClearSearch);
        tvContactCount = view.findViewById(R.id.tvContactCount);
        llEmptyState = view.findViewById(R.id.llEmptyState);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapter
        contactAdapter = new ContactAdapter(filteredList, user -> {
            ContactDetailFragment detailFragment = new ContactDetailFragment();
            Bundle args = new Bundle();
            args.putString("name", user.getFullName());
            args.putString("phone", user.getPhone());
            detailFragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(contactAdapter);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(ContactsViewModel.class);

        // Chỉ hiện spinner nếu dữ liệu chưa có
        if (viewModel.getContacts().getValue() == null || viewModel.getContacts().getValue().isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
        }

        // Quan sát LiveData
        viewModel.getContacts().observe(getViewLifecycleOwner(), list -> {
            progressBar.setVisibility(View.GONE);
            if (list != null) {
                userList.clear();
                userList.addAll(list);
                filteredList.clear();
                filteredList.addAll(list);
                contactAdapter.notifyDataSetChanged();
                updateContactCount();
                checkEmptyState();
            } else {
                Toast.makeText(getContext(), "Không thể tải danh bạ", Toast.LENGTH_SHORT).show();
            }
        });

        // Chỉ load API 1 lần
        viewModel.loadContacts(getContext());

        // Tìm kiếm
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterContacts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            btnClearSearch.setVisibility(View.GONE);
        });

        return view;
    }

    private void filterContacts(String query) {
        filteredList.clear();
        if (!query.isEmpty()) {
            btnClearSearch.setVisibility(View.VISIBLE);
            for (User user : userList) {
                if (user.getFullName().toLowerCase().contains(query.toLowerCase())
                        || user.getPhone().contains(query)) {
                    filteredList.add(user);
                }
            }
        } else {
            btnClearSearch.setVisibility(View.GONE);
            filteredList.addAll(userList);
        }

        contactAdapter.notifyDataSetChanged();
        updateContactCount();
        checkEmptyState();
    }

    private void updateContactCount() {
        tvContactCount.setText(filteredList.size() + " liên hệ");
    }

    private void checkEmptyState() {
        if (filteredList.isEmpty()) {
            llEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            llEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
