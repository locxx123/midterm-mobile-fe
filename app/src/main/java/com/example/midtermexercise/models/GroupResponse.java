package com.example.midtermexercise.models;

import java.util.List;

public class GroupResponse {
    public List<GroupItem> groups;

    public static class GroupItem {
        public String _id;
        public String name;
        public String photo;
        public List<ContactItem> contacts;
        public String createdAt;
        public String updatedAt;
    }

    public static class ContactItem {
        public String _id;
        public String fullName;
        public String phone;
        public String photo;
    }

    // Cho POST /phone/groups
    public static class SingleGroup {
        public GroupItem group;
    }
}
