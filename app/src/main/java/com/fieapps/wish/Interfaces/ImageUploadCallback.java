package com.fieapps.wish.Interfaces;

import retrofit2.Call;

public interface ImageUploadCallback {
        void onSuccess(String url);
        void onCancel();
        void onError(String error);
        void OnCallStart(Call call);
}
