package com.fieapps.wish.Models;

import com.google.gson.annotations.SerializedName;

import java.util.AbstractMap;

public class ResError {

    @SerializedName("errors")
    private AbstractMap.SimpleEntry<Object,String> map;

    public AbstractMap.SimpleEntry<Object,String> getMap() {
        return map;
    }
}
