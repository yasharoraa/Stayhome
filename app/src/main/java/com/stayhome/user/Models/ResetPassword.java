package com.stayhome.user.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ResetPassword implements Parcelable{

    @SerializedName("phone")
    private String phoneNumber;

    @SerializedName("code")
    private int verificationCode;

    @SerializedName("password")
    private String password;

    @SerializedName("reset")
    private String resetId;

    public ResetPassword(String phoneNumber, int verificationCode, String password, String resetId) {
        this.phoneNumber = phoneNumber;
        this.verificationCode = verificationCode;
        this.password = password;
        this.resetId = resetId;
    }

    protected ResetPassword(Parcel in) {
        phoneNumber = in.readString();
        verificationCode = in.readInt();
        password = in.readString();
        resetId = in.readString();
    }

    public static final Creator<ResetPassword> CREATOR = new Creator<ResetPassword>() {
        @Override
        public ResetPassword createFromParcel(Parcel in) {
            return new ResetPassword(in);
        }

        @Override
        public ResetPassword[] newArray(int size) {
            return new ResetPassword[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phoneNumber);
        dest.writeInt(verificationCode);
        dest.writeString(password);
        dest.writeString(resetId);
    }
}
