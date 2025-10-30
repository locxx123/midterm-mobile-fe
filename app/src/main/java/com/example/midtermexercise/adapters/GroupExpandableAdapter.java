package com.example.midtermexercise.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.midtermexercise.R;
import com.example.midtermexercise.models.Group;
import com.example.midtermexercise.models.GroupResponse;

import java.util.HashMap;
import java.util.List;

public class GroupExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Group> groupList;
    private HashMap<Group, List<GroupResponse.ContactItem>> memberMap;

    public GroupExpandableAdapter(Context context, List<Group> groupList, HashMap<Group, List<GroupResponse.ContactItem>> memberMap) {
        this.context = context;
        this.groupList = groupList;
        this.memberMap = memberMap;
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return memberMap.get(groupList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return memberMap.get(groupList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    // Header nhóm
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group_header, parent, false);

        TextView tvGroupName = convertView.findViewById(R.id.tvGroupName);
        ImageView icon = convertView.findViewById(R.id.icExpand);

        Group group = groupList.get(groupPosition);
        tvGroupName.setText(group.getName());
        icon.setRotation(isExpanded ? 180 : 0);
        return convertView;
    }

    // Thành viên con
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group_child, parent, false);

        TextView tvMember = convertView.findViewById(R.id.tvMemberName);
        TextView tvPhone = convertView.findViewById(R.id.tvMemberPhone);

        GroupResponse.ContactItem contact = memberMap.get(groupList.get(groupPosition)).get(childPosition);
        tvMember.setText(contact.fullName != null ? contact.fullName : "");
        tvPhone.setText(contact.phone != null ? contact.phone : "");

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void updateData(List<Group> newGroups, HashMap<Group, List<GroupResponse.ContactItem>> newMap) {
        this.groupList = newGroups;
        this.memberMap = newMap;
        notifyDataSetChanged();
    }
}