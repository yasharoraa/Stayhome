package com.stayhome.user.Utils;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.stayhome.user.BuildConfig;
import com.stayhome.user.Models.Message.LastMessage;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;


public class WebSocketUtil {

    private final String TAG = this.getClass().getSimpleName() + " : WEBSOCKET : ";

    private org.java_websocket.client.WebSocketClient webSocketClient;

    private Activity activity;

    private WebSocketListener webSocketListener;

    public WebSocketUtil(Activity activity) {
        this.activity = activity;
        this.webSocketListener = (WebSocketListener) activity;
        if (webSocketClient==null){
            connectWebSocket();
            return;
        }

        if (webSocketClient.getConnection().isOpen() || webSocketClient.getConnection().isConnecting())
            return;

        webSocketClient.connect();
    }

    private void connectWebSocket() {
        URI uri;
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<LastMessage>>(){}.getType();

        try {
            Log.i(TAG, "BASE URL: " + BuildConfig.BASE_URL);
            uri = new URI(BuildConfig.BASE_URL.replace("http", "ws"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new org.java_websocket.client.WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i(TAG, "SOCKET: " + "OPEN");
                JSONObject msg = new JSONObject();

                try {
                    msg.put("type", "authenticate");
                    msg.put("payload", new JSONObject().put("token",
                            Constants.token(activity))
                    .put("type",0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                webSocketClient.send(msg.toString());
            }

            @Override
            public void onMessage(String message) {

                Log.i(TAG , "STRING_MESSAGE : " +message);

                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String type = jsonObject.getString("type");
                    if (type.equals("chat")){
                        webSocketListener.onChats(gson.fromJson(jsonObject.getString("payload"),collectionType));
                    }else if(type.equals("message")){
                        webSocketListener.onMessage(gson.fromJson(jsonObject.getString("payload"),LastMessage.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i(TAG, "SOCKET: " + "CLOSE REASON = " + reason);
                webSocketListener.onSocketClose(code,reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.i(TAG, "SOCKET : ERROR = " + ex);
                webSocketListener.onSocketError(ex);
            }
        };
        webSocketClient.connect();
    }

    public void sendMessage(String message) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "chat");
            msg.put("payload", new JSONObject()
                    .put("message", message)
            .put("receiver","5ef2ec0f67cd032c27b80bb5"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webSocketClient.send(msg.toString());
    }
    public  interface WebSocketListener {
        void onSocketClose(int code,String error);
        void onSocketError(Exception ex);
        void onChats(List<LastMessage> list);
        void onMessage(LastMessage lastMessage);
    }
}
