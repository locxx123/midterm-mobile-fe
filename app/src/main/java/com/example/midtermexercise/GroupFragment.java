package com.example.midtermexercise;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.fragment.app.Fragment;

import com.example.midtermexercise.adapters.GroupExpandableAdapter;
import com.example.midtermexercise.models.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupFragment extends Fragment {

    private ExpandableListView expandableListView;
    private GroupExpandableAdapter adapter;
    private List<Group> groupList = new ArrayList<>();
    private HashMap<Group, List<String>> memberMap = new HashMap<>();

    private EditText etSearchGroup;
    private ImageButton btnClearSearchGroup;
    private TextView tvGroupCount;
    private LinearLayout llEmptyGroupState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        // Ánh xạ view
        expandableListView = view.findViewById(R.id.expandableListGroups);
        etSearchGroup = view.findViewById(R.id.etSearchGroup);
        btnClearSearchGroup = view.findViewById(R.id.btnClearSearchGroup);
        tvGroupCount = view.findViewById(R.id.tvGroupCount);
        llEmptyGroupState = view.findViewById(R.id.llEmptyGroupState);

        // Dữ liệu mẫu
        setupDummyData();
        updateGroupCount();
        checkEmptyState();

        // Adapter
        adapter = new GroupExpandableAdapter(requireContext(), groupList, memberMap);
        expandableListView.setAdapter(adapter);

        // Sự kiện click
        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> false);
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String memberName = memberMap.get(groupList.get(groupPosition)).get(childPosition);
            // TODO: xử lý khi click vào thành viên
            return true;
        });

        // Tìm kiếm nhóm
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

        return view;
    }

    private void setupDummyData() {
        groupList.clear();
        memberMap.clear();

        Group g1 = new Group("Gia đình", "5 thành viên");
        Group g2 = new Group("Bạn bè", "12 thành viên");
        Group g3 = new Group("Đồng nghiệp", "8 thành viên");

        groupList.add(g1);
        groupList.add(g2);
        groupList.add(g3);

        List<String> members1 = List.of("Ba", "Mẹ", "Anh", "Em", "Tôi");
        List<String> members2 = List.of("Nam", "Hà", "Tú", "Vy", "Duy", "Lan", "Khoa");
        List<String> members3 = List.of("Hùng", "Trang", "Tâm", "Khánh");

        memberMap.put(g1, new ArrayList<>(members1));
        memberMap.put(g2, new ArrayList<>(members2));
        memberMap.put(g3, new ArrayList<>(members3));
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
