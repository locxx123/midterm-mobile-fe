package com.example.midtermexercise.models;

import java.util.List;

public class FavoritesResponse {
    private List<User> favorites;

    public List<User> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<User> favorites) {
        this.favorites = favorites;
    }
}