package com.stayhome.user.Data.ChatData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stayhome.user.Models.Message.LastMessage;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<List<LastMessage>> chatLiveData;

    private ChatRepo chatRepo;

    private int initialOffset =  0;

    private final String TAG = this.getClass().getSimpleName();

    public LiveData<List<LastMessage>> init(String token,int offset,int limit) {

        if (chatLiveData != null && chatLiveData.getValue() !=null && initialOffset == offset) return chatLiveData;

        initialOffset = offset;

        chatRepo = ChatRepo.getInstance();

        chatLiveData = chatRepo.getChatList(token,offset,limit);

        return chatLiveData;

    }

    public LiveData<List<LastMessage>> getChatRepo() { return chatLiveData; }

    public void postValue(List<LastMessage> chats) {
        chatLiveData = new MutableLiveData<>();
        chatLiveData.postValue(chats);
    }
}
