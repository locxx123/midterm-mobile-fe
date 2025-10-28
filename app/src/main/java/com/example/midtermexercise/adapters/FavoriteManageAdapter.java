package com.example.midtermexercise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midtermexercise.R;
import com.example.midtermexercise.models.User;

import java.util.List;

public class FavoriteManageAdapter extends RecyclerView.Adapter<FavoriteManageAdapter.ViewHolder> {

    public interface OnRemoveClickListener {
        void onRemove(User user);
    }

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    private final List<User> data;
    private final OnRemoveClickListener removeListener;
    private final OnItemClickListener itemClickListener;

    public FavoriteManageAdapter(List<User> data,
                                 OnRemoveClickListener removeListener,
                                 OnItemClickListener itemClickListener) {
        this.data = data;
        this.removeListener = removeListener;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_vertical, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        User u = data.get(position);
        h.tvName.setText(u.getFullName());
        h.tvPhone.setText(u.getPhone());
        h.imgAvatar.setImageResource(R.drawable.ic_person);

        h.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) itemClickListener.onItemClick(u);
        });

        h.btnRemoveFav.setOnClickListener(v -> {
            if (removeListener != null) removeListener.onRemove(u);
        });
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvName, tvPhone;
        ImageButton btnRemoveFav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            btnRemoveFav = itemView.findViewById(R.id.btnRemoveFav);
        }
    }
}