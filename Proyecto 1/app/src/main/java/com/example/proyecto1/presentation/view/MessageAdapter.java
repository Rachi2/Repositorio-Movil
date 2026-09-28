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
import com.example.proyecto1.domain.model.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    private final String currentUserId;
    private List<Message> messages = new ArrayList<>();

    public MessageAdapter(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getSenderId().equals(currentUserId)) {
            return TYPE_SENT;
        } else {
            return TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        String timeStr = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date(message.getTimestamp()));

        if (holder instanceof SentViewHolder) {
            SentViewHolder sentHolder = (SentViewHolder) holder;
            sentHolder.tvText.setText(message.getText());
            sentHolder.tvTime.setText(timeStr);
            bindImage(sentHolder.ivImage, sentHolder.tvText, message);
        } else if (holder instanceof ReceivedViewHolder) {
            ReceivedViewHolder receivedHolder = (ReceivedViewHolder) holder;
            receivedHolder.tvName.setText(message.getSenderName());
            receivedHolder.tvText.setText(message.getText());
            receivedHolder.tvTime.setText(timeStr);
            bindImage(receivedHolder.ivImage, receivedHolder.tvText, message);
        }
    }

    private void bindImage(ImageView ivImage, TextView tvText, Message message) {
        if (message.getImageUrl() != null) {
            // Es un mensaje de imagen
            ivImage.setVisibility(View.VISIBLE);
            tvText.setVisibility(View.GONE);
            Glide.with(ivImage.getContext())
                    .load(message.getImageUrl())
                    .into(ivImage);
        } else {
            // Es un mensaje de texto normal, hay que limpiar el estado reciclado
            ivImage.setVisibility(View.GONE);
            tvText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class SentViewHolder extends RecyclerView.ViewHolder {
        TextView tvText, tvTime;
        ImageView ivImage;
        SentViewHolder(View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvMessageText);
            tvTime = itemView.findViewById(R.id.tvMessageTime);
            ivImage = itemView.findViewById(R.id.ivMessageImage);
        }
    }

    static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvText, tvTime;
        ImageView ivImage;
        ReceivedViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSenderName);
            tvText = itemView.findViewById(R.id.tvMessageText);
            tvTime = itemView.findViewById(R.id.tvMessageTime);
            ivImage = itemView.findViewById(R.id.ivMessageImage);
        }
    }
}