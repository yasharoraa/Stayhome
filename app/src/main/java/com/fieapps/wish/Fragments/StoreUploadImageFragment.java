package com.fieapps.wish.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fieapps.wish.Activities.MainActivity;
import com.fieapps.wish.Interfaces.ImageUploadCallback;
import com.fieapps.wish.R;
import com.fieapps.wish.Utils.AsyncTasks;
import com.fieapps.wish.Utils.Constants;
import com.takusemba.cropme.CropLayout;
import com.takusemba.cropme.OnCropListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;

import static android.app.Activity.RESULT_OK;
import static com.fieapps.wish.Utils.Constants.getRealPathFromURI;

public class StoreUploadImageFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.button_select_image)
    Button selectImageButton;

    @BindView(R.id.button_upload_image)
    Button uploadImageButton;

    @BindView(R.id.crop_view)
    CropLayout cropView;

    private Unbinder unbinder;

    private MainActivity activity;

    private int PERMISSION_CODE = 101;

    private final String TAG = this.getClass().getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_store_image_upload, container, false);
        activity = (getActivity() instanceof MainActivity) ? (MainActivity) getActivity() : null;
        if (activity == null) return rootView;
        String token = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.TOKEN, null);
        if (token == null) return rootView;
        Log.i(TAG, token);
        unbinder = ButterKnife.bind(this, rootView);
        Log.i(TAG, "continue");
        cropView.addOnCropListener(new OnCropListener() {
            @Override
            public void onSuccess(@NotNull Bitmap bitmap) {
                uploadImage(activity.getCacheDir(), token, "stores", new ImageUploadCallback() {
                    @Override
                    public void onSuccess(String url) {
                        activity.toggleProgress(false, "");
                        makeToast("Image Uploaded successfully");
                    }

                    @Override
                    public void onCancel() {
                        activity.toggleProgress(false, "");
                        makeToast("Image Processing Error");
                    }

                    @Override
                    public void onError(String error) {
                        activity.toggleProgress(false, "");
                        makeToast("Error :" + error);
                    }

                    @Override
                    public void OnCallStart(Call call) {

                    }
                }, bitmap);
                activity.toggleProgress(true, "uploading image");
            }

            @Override
            public void onFailure(@NotNull Exception e) {

            }
        });
        selectImageButton.setOnClickListener(this);
        uploadImageButton.setOnClickListener(this);
        return rootView;
    }

    private void uploadImage(File cache, String token, String type, ImageUploadCallback callback, Bitmap bitmap) {
        new AsyncTasks.ImageConvertClass(cache, token, type, callback).execute(bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PICK_IMAGE && resultCode == RESULT_OK
                && null != data) {

            Uri selectedImage = data.getData();
            String imageEncoded = getRealPathFromURI(activity, selectedImage);
            Bitmap bitmap = BitmapFactory.decodeFile(imageEncoded);
            cropView.setBitmap(bitmap);
            Log.i("CROP", String.valueOf(cropView.isOffFrame()));

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_select_image:
                if (Constants.isStoragePermissionGranted(activity))
                    Constants.selectPicture(activity);
                Log.i(TAG, "tap on crop view");
                break;
            case R.id.button_upload_image:
                cropView.crop();
                Log.i(TAG, "tap on upload view");
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Constants.selectPicture(activity);
            //resume tasks needing this permission
        }
    }


    private void makeToast(String value) {
        Toast.makeText(activity, value, Toast.LENGTH_SHORT).show();
    }
}
