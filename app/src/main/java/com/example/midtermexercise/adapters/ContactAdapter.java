package com.example.midtermexercise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.midtermexercise.R;
import com.example.midtermexercise.models.User;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<User> userList;
    private OnItemClickListener listener;

    // Interface callback
    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public ContactAdapter(List<User> userList, OnItemClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact_vertical, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvName.setText(user.getFullName());
        holder.tvPhone.setText(user.getPhone());
        holder.imgMore.setImageResource(R.drawable.ic_more);

        // Gán sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(user);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar, imgMore;
        TextView tvName, tvPhone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            imgMore = itemView.findViewById(R.id.btnMore);
        }
    }
}
