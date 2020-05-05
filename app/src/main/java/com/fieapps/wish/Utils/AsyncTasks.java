package com.fieapps.wish.Utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.graphics.BitmapCompat;

import com.fieapps.wish.Interfaces.ImageUploadCallback;
import com.fieapps.wish.WebServices.ApiInterface;
import com.fieapps.wish.WebServices.ServiceGenerator;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AsyncTasks {

    public static class ImageConvertClass extends AsyncTask<Bitmap, Void, MultipartBody.Part> {

        private File f;
        private String token;
        private String type;
        private ImageUploadCallback callback;
        Call<JsonObject> call;

        public ImageConvertClass(File cacheDir, String token, String type, ImageUploadCallback callback) {
            this.token = token;
            this.type = type;
            this.callback = callback;
            Random r = new Random();
            String randomNumber = String.format(Locale.US, "%04d", r.nextInt(1001));
            f = new File(cacheDir, randomNumber);
            try {
                f.createNewFile();
            } catch (IOException e) {
                this.cancel(true);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MultipartBody.Part doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                this.cancel(true);
            }
            try {
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                this.cancel(true);
            }
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), f);
            return MultipartBody.Part.createFormData("upload", f.getName(), reqFile);
        }


        @Override
        protected void onPostExecute(MultipartBody.Part body) {
            call = ServiceGenerator.createUploadService(ApiInterface.class).postImage(token, body, type);
            callback.OnCallStart(call);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null)
                            callback.onSuccess(response.body().get("image").getAsString());
                    } else {
                        callback.onError(String.valueOf(response.code()));
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    callback.onError("Check you internet connection");
                    Log.i("IMAGE_UPLOAD" ,t.getMessage() + t.getCause() + Arrays.toString(t.getStackTrace()));
                }
            });
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (call!=null)
                call.cancel();

            callback.onCancel();
        }
    }

}
