package com.example.midtermexercise;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.midtermexercise.api.ApiService;
import com.example.midtermexercise.api.RetrofitClient;
import com.example.midtermexercise.models.User;
import com.example.midtermexercise.viewmodel.ContactsViewModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        recyclerView = view.findViewById(R.id.rvContacts);
        etSearch = view.findViewById(R.id.etSearch);
        btnClearSearch = view.findViewById(R.id.btnClearSearch);
        tvContactCount = view.findViewById(R.id.tvContactCount);
        llEmptyState = view.findViewById(R.id.llEmptyState);
        progressBar = view.findViewById(R.id.progressBar);
        ImageButton btnAddContact = view.findViewById(R.id.btnAddContact);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        contactAdapter = new ContactAdapter(filteredList, user -> {
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
        recyclerView.setAdapter(contactAdapter);

        viewModel = new ViewModelProvider(this).get(ContactsViewModel.class);

        if (viewModel.getContacts().getValue() == null || viewModel.getContacts().getValue().isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
        }

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

        viewModel.loadContacts(getContext());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filterContacts(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            btnClearSearch.setVisibility(View.GONE);
        });

        btnAddContact.setOnClickListener(v -> showAddContactDialog());
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

    private void showAddContactDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_contact, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etPhone = dialogView.findViewById(R.id.etPhone);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        LinearLayout btnAdd = dialogView.findViewById(R.id.btnAdd);
        TextView tvAddText = dialogView.findViewById(R.id.tvAddText);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        ProgressBar spinnerAdd = dialogView.findViewById(R.id.progressSpinnerAdd);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ tên và số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }

            setAddButtonLoading(btnAdd, spinnerAdd, true);

            ApiService api = RetrofitClient.getApiService(requireContext());
            Map<String, String> body = new HashMap<>();
            body.put("fullName", name);
            body.put("phone", phone);
            body.put("photo", "");

            api.addContact(body).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    setAddButtonLoading(btnAdd, spinnerAdd, false);

                    if (response.isSuccessful() && response.body() != null) {
                        User newUser = response.body();
                        userList.add(newUser);
                        filteredList.add(newUser);
                        contactAdapter.notifyItemInserted(filteredList.size() - 1);
                        updateContactCount();
                        checkEmptyState();
                        Toast.makeText(requireContext(), "Đã thêm liên hệ: " + name, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(requireContext(), "Thêm thất bại (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    setAddButtonLoading(btnAdd, spinnerAdd, false);
                    Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    private void setAddButtonLoading(LinearLayout btnAdd, ProgressBar spinner, boolean isLoading) {
        btnAdd.setEnabled(!isLoading);
        btnAdd.setAlpha(isLoading ? 0.6f : 1f);
        spinner.setVisibility(isLoading ? View.VISIBLE : View.GONE);
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
