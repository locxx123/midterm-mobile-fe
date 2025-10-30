package com.example.midtermexercise;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.midtermexercise.adapters.GroupExpandableAdapter;
import com.example.midtermexercise.api.ApiService;
import com.example.midtermexercise.api.RetrofitClient;
import com.example.midtermexercise.models.Group;
import com.example.midtermexercise.models.GroupRequest;
import com.example.midtermexercise.models.GroupResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupFragment extends Fragment {

    private ExpandableListView expandableListView;
    private GroupExpandableAdapter adapter;
    private List<Group> groupList = new ArrayList<>();
    private HashMap<Group, List<String>> memberMap = new HashMap<>();

    private EditText etSearchGroup;
    private ImageButton btnClearSearchGroup;
    private TextView tvGroupCount;
    private LinearLayout llEmptyGroupState;
    private FloatingActionButton fabAddGroup;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        expandableListView = view.findViewById(R.id.expandableListGroups);
        etSearchGroup = view.findViewById(R.id.etSearchGroup);
        btnClearSearchGroup = view.findViewById(R.id.btnClearSearchGroup);
        tvGroupCount = view.findViewById(R.id.tvGroupCount);
        llEmptyGroupState = view.findViewById(R.id.llEmptyGroupState);
        fabAddGroup = view.findViewById(R.id.fabAddGroup);
        progressBar = view.findViewById(R.id.progressBarGroups);

        adapter = new GroupExpandableAdapter(requireContext(), groupList, memberMap);
        expandableListView.setAdapter(adapter);

        fetchGroupsFromApi();

        etSearchGroup.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterGroups(s.toString());
            }
        });

        btnClearSearchGroup.setOnClickListener(v -> {
            etSearchGroup.setText("");
            btnClearSearchGroup.setVisibility(View.GONE);
        });

        fabAddGroup.setOnClickListener(v -> showCreateGroupDialog());

        return view;
    }

    private void fetchGroupsFromApi() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService api = RetrofitClient.getApiService(requireContext());

        api.getGroups().enqueue(new Callback<GroupResponse>() {
            @Override
            public void onResponse(Call<GroupResponse> call, Response<GroupResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    groupList.clear();
                    memberMap.clear();

                    List<GroupResponse.GroupItem> apiGroups = response.body().groups;
                    for (GroupResponse.GroupItem apiGroup : apiGroups) {
                        Group g = new Group(apiGroup.name, apiGroup.photo);
                        groupList.add(g);

                        List<String> members = new ArrayList<>();
                        if (apiGroup.contacts != null) {
                            for (GroupResponse.ContactItem c : apiGroup.contacts) {
                                members.add(c.fullName);
                            }
                        }
                        memberMap.put(g, members);
                    }

                    adapter.notifyDataSetChanged();
                    updateGroupCount();
                    checkEmptyState();
                    Log.d("GroupFragment", "✅ Tải nhóm thành công: " + groupList.size());
                } else {
                    Toast.makeText(requireContext(), "Không thể tải danh sách nhóm (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    Log.e("GroupFragment", "❌ Lỗi response: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GroupResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("GroupFragment", "❌ Lỗi API: " + t.getMessage());
            }
        });
    }

    private void showCreateGroupDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_group, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        EditText etGroupName = dialogView.findViewById(R.id.etGroupName);
        View btnCreate = dialogView.findViewById(R.id.btnCreateGroup);
        ProgressBar spinner = dialogView.findViewById(R.id.progressSpinnerCreate);

        btnCreate.setOnClickListener(v -> {
            String groupName = etGroupName.getText().toString().trim();
            if (groupName.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập tên nhóm", Toast.LENGTH_SHORT).show();
                return;
            }

            setLoading(btnCreate, spinner, true);

            ApiService api = RetrofitClient.getApiService(requireContext());
            GroupRequest request = new GroupRequest(groupName);

            api.createGroup(request).enqueue(new Callback<GroupResponse.SingleGroup>() {
                @Override
                public void onResponse(Call<GroupResponse.SingleGroup> call, Response<GroupResponse.SingleGroup> response) {
                    setLoading(btnCreate, spinner, false);

                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(requireContext(), "Tạo nhóm thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        fetchGroupsFromApi();
                    } else {
                        Toast.makeText(requireContext(), "Không thể tạo nhóm (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GroupResponse.SingleGroup> call, Throwable t) {
                    setLoading(btnCreate, spinner, false);
                    Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void filterGroups(String query) {
        List<Group> filteredGroups = new ArrayList<>();
        HashMap<Group, List<String>> filteredMap = new HashMap<>();

        if (query.isEmpty()) {
            filteredGroups.addAll(groupList);
            filteredMap.putAll(memberMap);
            btnClearSearchGroup.setVisibility(View.GONE);
        } else {
            btnClearSearchGroup.setVisibility(View.VISIBLE);
            for (Group g : groupList) {
                if (g.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredGroups.add(g);
                    filteredMap.put(g, memberMap.get(g));
                }
            }
        }

        adapter.updateData(filteredGroups, filteredMap);
        tvGroupCount.setText(filteredGroups.size() + " nhóm");
        llEmptyGroupState.setVisibility(filteredGroups.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void updateGroupCount() {
        tvGroupCount.setText(groupList.size() + " nhóm");
    }

    private void checkEmptyState() {
        llEmptyGroupState.setVisibility(groupList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void setLoading(View button, ProgressBar spinner, boolean isLoading) {
        button.setEnabled(!isLoading);
        button.setAlpha(isLoading ? 0.6f : 1f);
        spinner.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}
