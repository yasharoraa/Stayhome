package com.fieapps.stayhomeindia.Models;

import com.google.gson.annotations.SerializedName;

public class TopSlide {

    @SerializedName("text")
    private String text;

    @SerializedName("small_text")
    private String smallText;

    @SerializedName("icon")
    private String icon;

    @SerializedName("uri")
    private String uri;

    @SerializedName("background")
    private String background;

    public TopSlide(String text, String smallText, String icon, String uri) {
        this.text = text;
        this.smallText = smallText;
        this.icon = icon;
        this.uri = uri;
    }

    public String getText() {
        return text;
    }

    public String getSmallText() {
        return smallText;
    }

    public String getIcon() {
        return icon;
    }

    public String getUri() {
        return uri;
    }

    public String getBackground() {
        return background;
    }
}
