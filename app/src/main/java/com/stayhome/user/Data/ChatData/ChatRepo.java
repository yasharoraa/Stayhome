package com.stayhome.user.Data.ChatData;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.annotations.SerializedName;
import com.stayhome.user.Models.Message.LastMessage;
import com.stayhome.user.Models.Order.OrderElement;
import com.stayhome.user.WebServices.ApiInterface;
import com.stayhome.user.WebServices.ServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepo {

    private static ChatRepo chatRepo;

    static ChatRepo getInstance() {
        if(chatRepo == null){
            chatRepo = new ChatRepo();
        }
        return chatRepo;
    }

    private ApiInterface apiInterface;

    private ChatRepo() {
        apiInterface = ServiceGenerator.createService(ApiInterface.class);
    }

    MutableLiveData<List<LastMessage>> getChatList(String token, int offset, int limit) {
        MutableLiveData<List<LastMessage>> chatData = new MutableLiveData<>();
        apiInterface.getChats(token).enqueue(new Callback<List<LastMessage>>() {
            @Override
            public void onResponse(Call<List<LastMessage>> call, Response<List<LastMessage>> response) {
                if (response.isSuccessful()){
                    chatData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<LastMessage>> call, Throwable t) {
                chatData.setValue(null);
            }
        });
        return chatData;
    }

}
