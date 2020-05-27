package com.fieapps.stayhomeindia.Models.User;

import com.google.gson.annotations.SerializedName;


public class CreateUser {

    @SerializedName("user")
    User user;

    public CreateUser(String phone,String password) {
        this.user = new User(phone,password);
    }


    public User getUser() {
        return user;
    }

    public class User {
        @SerializedName("phone")
        private String phone;

        @SerializedName("password")
        private String password;

        User(String phone, String password) {
            this.phone = phone;
            this.password = password;
        }

        public String getPhone() {
            return phone;
        }

        public String getPassword() {
            return password;
        }
    }

}
