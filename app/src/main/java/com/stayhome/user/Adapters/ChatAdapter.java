package com.stayhome.user.Adapters;

import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.stayhome.user.Interfaces.ChatActionListener;
import com.stayhome.user.Models.Message.LastMessage;
import com.stayhome.user.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<LastMessage> chat;

    private ChatActionListener chatActionListener;

    public ChatAdapter(ChatActionListener chatActionListener) {
        chat = new ArrayList<>();
        this.chatActionListener = chatActionListener;
    }

    public void addAll(List<LastMessage> list) {
        chat.addAll(list);
        notifyDataSetChanged();
    }

    public List<LastMessage> getChat() {
        return chat;
    }

    public void addNewMessage(LastMessage lastMessage) {
        chat.add(0,lastMessage);
        notifyItemInserted(0);
    }

    public void clear() {
        chat.clear();
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user,parent,false);
        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        LastMessage message = chat.get(position);

        holder.lastMessageTextView.setText(message.getText());

        if (message.getSent() && message.getRead()) {
            holder.readImageView.setVisibility(View.VISIBLE);
            holder.lastMessageTextView.setTypeface(holder.lastMessageTextView.getTypeface(), Typeface.NORMAL);
        } else if (!message.getSent() && !message.getRead()){
            holder.readImageView.setVisibility(View.INVISIBLE);
            holder.lastMessageTextView.setTypeface(holder.lastMessageTextView.getTypeface(), Typeface.BOLD);
        } else {
            holder.readImageView.setVisibility(View.INVISIBLE);
            holder.lastMessageTextView.setTypeface(holder.lastMessageTextView.getTypeface(), Typeface.NORMAL);
        }

        if (message.getSenderInfo() == null) return;

        holder.userNameTextView.setText(
                message.getSenderInfo().getName()!=null ?
                        message.getSenderInfo().getName() :
                        "user_" +
                                message.getSenderInfo().getId().substring(
                                        message.getSenderInfo().getId().length() - 3)
        );

        if (message.getSenderInfo().getImage() != null) {
            Uri uri = Uri.parse(null);
            Glide.with(holder.itemView.getContext())
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.ic_baseline_account_circle_24)
                    .into(holder.avatarImageView);

        }else {
            holder.avatarImageView.setImageResource(R.drawable.ic_baseline_account_circle_24);
        }


    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar_image_view)
        ImageView avatarImageView;

        @BindView(R.id.user_name_text_view)
        TextView userNameTextView;

        @BindView(R.id.last_message_text_view)
        TextView lastMessageTextView;

        @BindView(R.id.read_image_view)
        ImageView readImageView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
