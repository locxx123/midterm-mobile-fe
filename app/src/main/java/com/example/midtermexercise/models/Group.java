package com.example.midtermexercise.models;

public class Group {
    private String name;
    private String memberCount;

    public Group(String name, String memberCount) {
        this.name = name;
        this.memberCount = memberCount;
    }

    public String getName() { return name; }
    public String getMemberCount() { return memberCount; }
}
