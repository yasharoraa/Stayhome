package com.stayhome.user.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

public class Category{

    @SerializedName("_id")
    private String id;

    @SerializedName("cat_id")
    private String categoryId;

    @SerializedName("image")
    private String image;

    @SerializedName("image_small")
    private String smallImage;

    @SerializedName("name")
    private String name;

    @SerializedName("name_hi")
    private String nameHindi;

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getSmallImage() {
        return smallImage;
    }

    public String getName() {

        return Locale.getDefault().getLanguage().equals("hi") ? nameHindi : name;
    }

    public String getCategoryId() {
        return categoryId;
    }

}
