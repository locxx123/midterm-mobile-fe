package com.example.midtermexercise;

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
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.midtermexercise.adapters.GroupExpandableAdapter;
import com.example.midtermexercise.api.RetrofitClient;
import com.example.midtermexercise.models.Group;
import com.example.midtermexercise.models.GroupRequest;
import com.example.midtermexercise.models.GroupResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        // Ánh xạ view
        expandableListView = view.findViewById(R.id.expandableListGroups);
        etSearchGroup = view.findViewById(R.id.etSearchGroup);
        btnClearSearchGroup = view.findViewById(R.id.btnClearSearchGroup);
        tvGroupCount = view.findViewById(R.id.tvGroupCount);
        llEmptyGroupState = view.findViewById(R.id.llEmptyGroupState);
        fabAddGroup = view.findViewById(R.id.fabAddGroup);

        adapter = new GroupExpandableAdapter(requireContext(), groupList, memberMap);
        expandableListView.setAdapter(adapter);

        // Fetch nhóm từ API
        fetchGroupsFromApi();

        // Click listener
        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> false);
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            List<String> members = memberMap.get(groupList.get(groupPosition));
            if (members != null && childPosition < members.size()) {
                String memberName = members.get(childPosition);
                // TODO: xử lý khi click vào thành viên
            }
            return true;
        });

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

    private void showCreateGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_group, null);
        final EditText etGroupName = dialogView.findViewById(R.id.etGroupName);

        builder.setView(dialogView)
                .setTitle("Tạo nhóm mới")
                .setPositiveButton("Tạo", (dialog, id) -> {
                    String groupName = etGroupName.getText().toString().trim();
                    if (!groupName.isEmpty()) {
                        createGroupToApi(groupName, null);
                    }
                })
                .setNegativeButton("Hủy", (dialog, id) -> dialog.cancel());

        builder.create().show();
    }

    private void fetchGroupsFromApi() {
        Call<GroupResponse> call = RetrofitClient.getApiService(requireContext()).getGroups();

        call.enqueue(new retrofit2.Callback<GroupResponse>() {
            @Override
            public void onResponse(Call<GroupResponse> call, retrofit2.Response<GroupResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().groups != null) {
                    groupList.clear();
                    memberMap.clear();

                    for (GroupResponse.GroupItem apiGroup : response.body().groups) {
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

                } else {
                    Log.e("API", "❌ Lỗi response: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GroupResponse> call, Throwable t) {
                Log.e("API", "❌ Lỗi kết nối API: " + t.getMessage());
            }
        });
    }

    private void createGroupToApi(String groupName, @Nullable String photoUrl) {
        GroupRequest request = new GroupRequest(groupName, photoUrl);

        Call<GroupResponse.SingleGroup> call = RetrofitClient.getApiService(requireContext())
                .createGroup(request);

        call.enqueue(new retrofit2.Callback<GroupResponse.SingleGroup>() {
            @Override
            public void onResponse(Call<GroupResponse.SingleGroup> call,
                                   retrofit2.Response<GroupResponse.SingleGroup> response) {
                if (response.isSuccessful() && response.body() != null && response.body().group != null) {
                    // Thêm nhóm mới trực tiếp
                    GroupResponse.SingleGroup apiGroup = response.body();
                    Group g = new Group(apiGroup.group.name, apiGroup.group.photo);
                    groupList.add(g);
                    memberMap.put(g, new ArrayList<>());
                    adapter.notifyDataSetChanged();
                    updateGroupCount();
                    checkEmptyState();
                } else {
                    Log.e("API", "❌ Lỗi tạo nhóm: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GroupResponse.SingleGroup> call, Throwable t) {
                Log.e("API", "❌ Lỗi kết nối khi tạo nhóm: " + t.getMessage());
            }
        });
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
}
