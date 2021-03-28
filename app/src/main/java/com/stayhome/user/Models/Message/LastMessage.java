package com.stayhome.user.Models.Message;

import com.google.gson.annotations.SerializedName;

public class LastMessage {

    @SerializedName("_id")
    private SenderInfo senderInfo;

    @SerializedName("text")
    private String text;

    @SerializedName("sent")
    private Boolean sent;

    @SerializedName("createdAt")
    private String timestamp;

    @SerializedName("read")
    private Boolean read;

    public SenderInfo getSenderInfo() {
        return senderInfo;
    }

    public String getText() {
        return text;
    }

    public Boolean getSent() {
        return sent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Boolean getRead() {
        return read;
    }

    public static class SenderInfo {

        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("image")
        private String image;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getImage() {
            return image;
        }
    }
}
