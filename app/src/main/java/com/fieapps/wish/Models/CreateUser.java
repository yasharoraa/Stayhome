package com.fieapps.wish.Models;

import com.google.gson.annotations.SerializedName;


public class CreateUser {

    @SerializedName("user")
    User user;

    public CreateUser(String phone,String password) {
        this.user = new User(phone,password);
    }

    private class User {
        @SerializedName("phone")
        private String phone;

        @SerializedName("password")
        private String password;

        User(String phone, String password) {
            this.phone = phone;
            this.password = password;
        }
    }
}
