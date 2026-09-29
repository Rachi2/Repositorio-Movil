package com.example.proyecto1.presentation.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto1.R;
import com.example.proyecto1.domain.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final OnUserClickListener listener;
    private List<User> users = new ArrayList<>();

    public UserAdapter(OnUserClickListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = users.get(position);

        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvInitial.setText(AvatarUtils.getInitial(user.getName()));

        if (user.getPhotoUrl() != null) {
            holder.ivAvatar.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView).load(user.getPhotoUrl()).circleCrop().into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setVisibility(View.GONE);
            Glide.with(holder.itemView).clear(holder.ivAvatar);
        }

        holder.itemView.setOnClickListener(v -> listener.onUserClick(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitial;
        TextView tvName;
        TextView tvEmail;
        ImageView ivAvatar;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvInitial = itemView.findViewById(R.id.tvInitial);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
        }
    }
}
