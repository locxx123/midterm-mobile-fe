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
import com.example.midtermexercise.models.FavoritesResponse;
import com.example.midtermexercise.models.User;
import com.example.midtermexercise.models.GroupDeleteResponse;
import com.example.midtermexercise.models.GroupAddResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupFragment extends Fragment {

    private ExpandableListView expandableListView;
    private GroupExpandableAdapter adapter;
    private List<Group> groupList = new ArrayList<>();
    private HashMap<Group, List<GroupResponse.ContactItem>> memberMap = new HashMap<>();
    // Map group -> groupId để thao tác API
    private final HashMap<Group, String> groupIdMap = new HashMap<>();

    private EditText etSearchGroup;
    private ImageButton btnClearSearchGroup;
    private TextView tvGroupCount;
    private LinearLayout llEmptyGroupState;
    private ProgressBar progressBar;

    // IDs các contact đang là yêu thích
    private final HashSet<String> favoriteIds = new HashSet<>();

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
        progressBar = view.findViewById(R.id.progressBarGroups);
        ImageButton btnAddGroup = view.findViewById(R.id.btnAddGroup);

        adapter = new GroupExpandableAdapter(requireContext(), groupList, memberMap);
        expandableListView.setAdapter(adapter);

        // Nhấn giữ header nhóm / contact con -> hỏi xóa
        expandableListView.setOnItemLongClickListener((parent, v, flatPos, id) -> {
            long packed = expandableListView.getExpandableListPosition(flatPos);
            int type = ExpandableListView.getPackedPositionType(packed);
            if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                int groupPos = ExpandableListView.getPackedPositionGroup(packed);
                Group g = (Group) adapter.getGroup(groupPos);
                String groupId = groupIdMap.get(g);
                if (groupId != null) {
                    confirmDeleteGroup(g, groupId);
                }
                return true;
            } else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                int groupPos = ExpandableListView.getPackedPositionGroup(packed);
                int childPos = ExpandableListView.getPackedPositionChild(packed);
                Group g = (Group) adapter.getGroup(groupPos);
                String groupId = groupIdMap.get(g);
                GroupResponse.ContactItem c = (GroupResponse.ContactItem) adapter.getChild(groupPos, childPos);
                if (groupId != null && c != null) {
                    confirmRemoveContactFromGroup(g, groupId, c);
                }
                return true;
            }
            return false;
        });

        // Click child -> mở chi tiết liên hệ với trạng thái favorite chính xác
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id1) -> {
            GroupResponse.ContactItem c = (GroupResponse.ContactItem) adapter.getChild(groupPosition, childPosition);
            if (c == null) return true;

            boolean isFav = c._id != null && favoriteIds.contains(c._id);

            ContactDetailFragment detailFragment = new ContactDetailFragment();
            Bundle args = new Bundle();
            args.putString("name", c.fullName);
            args.putString("phone", c.phone);
            args.putString("id", c._id);
            args.putBoolean("favorite", isFav);
            detailFragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        });

        fetchGroupsFromApi();
        fetchFavorites();

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

        if (btnAddGroup != null) {
            btnAddGroup.setOnClickListener(v -> showCreateGroupDialog());
        }

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
                    groupIdMap.clear();

                    List<GroupResponse.GroupItem> apiGroups = response.body().groups;
                    for (GroupResponse.GroupItem apiGroup : apiGroups) {
                        Group g = new Group(apiGroup.name, apiGroup.photo);
                        groupList.add(g);

                        List<GroupResponse.ContactItem> members = new ArrayList<>();
                        if (apiGroup.contacts != null) {
                            members.addAll(apiGroup.contacts);
                        }
                        memberMap.put(g, members);
                        groupIdMap.put(g, apiGroup._id);
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

    private void fetchFavorites() {
        ApiService api = RetrofitClient.getApiService(requireContext());
        api.getFavorites().enqueue(new Callback<FavoritesResponse>() {
            @Override
            public void onResponse(Call<FavoritesResponse> call, Response<FavoritesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getFavorites() != null) {
                    favoriteIds.clear();
                    for (User u : response.body().getFavorites()) {
                        if (u.getId() != null) favoriteIds.add(u.getId());
                    }
                }
            }

            @Override
            public void onFailure(Call<FavoritesResponse> call, Throwable t) {
                // Bỏ qua
            }
        });
    }

    private void confirmDeleteGroup(Group group, String groupId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa nhóm")
                .setMessage("Bạn có chắc muốn xóa nhóm \"" + group.getName() + "\"?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (d, w) -> {
                    ApiService api = RetrofitClient.getApiService(requireContext());
                    api.deleteGroup(groupId).enqueue(new Callback<GroupDeleteResponse>() {
                        @Override
                        public void onResponse(Call<GroupDeleteResponse> call, Response<GroupDeleteResponse> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(requireContext(),"Đã xóa nhóm",
                                        Toast.LENGTH_SHORT).show();
                                fetchGroupsFromApi();
                            } else {
                                Toast.makeText(requireContext(), "Xóa nhóm thất bại (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<GroupDeleteResponse> call, Throwable t) {
                            Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .show();
    }

    private void confirmRemoveContactFromGroup(Group group, String groupId, GroupResponse.ContactItem contact) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa khỏi nhóm")
                .setMessage("Bạn có muốn xóa \"" + (contact.fullName != null ? contact.fullName : "") +
                        "\" khỏi \"" + (group.getName() != null ? group.getName() : "nhóm") + "\"?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (d, w) -> {
                    ApiService api = RetrofitClient.getApiService(requireContext());
                    Map<String, String> body = new HashMap<>();
                    body.put("contactId", contact._id);

                    api.removeContactFromGroup(groupId, body).enqueue(new Callback<GroupAddResponse>() {
                        @Override
                        public void onResponse(Call<GroupAddResponse> call, Response<GroupAddResponse> response) {
                            if (response.isSuccessful()) {
                                List<GroupResponse.ContactItem> members = memberMap.get(group);
                                if (members != null) {
                                    for (int i = 0; i < members.size(); i++) {
                                        if (contact._id != null && contact._id.equals(members.get(i)._id)) {
                                            members.remove(i);
                                            break;
                                        }
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                Toast.makeText(requireContext(), "Đã xóa khỏi nhóm", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Xóa khỏi nhóm thất bại (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<GroupAddResponse> call, Throwable t) {
                            Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .show();
    }

    private void showCreateGroupDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_group, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        EditText etGroupName = dialogView.findViewById(R.id.etGroupName);
        View btnCreate = dialogView.findViewById(R.id.btnCreateGroup);
        ProgressBar spinner = dialogView.findViewById(R.id.progressSpinnerCreate);
        View btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

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
        HashMap<Group, List<GroupResponse.ContactItem>> filteredMap = new HashMap<>();

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