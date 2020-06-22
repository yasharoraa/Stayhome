package com.stayhome.user.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stayhome.user.Activities.MainActivity;
import com.stayhome.user.Interfaces.AbortDialogListener;
import com.stayhome.user.Models.Geocoding.Reverse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Constants {
    public static void setFullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static String findComponent(List<Reverse.Results.AddressComponents> list, String type) {

        for(Reverse.Results.AddressComponents addressComponents : list){
            if (addressComponents.getTypes().contains(type)){
                return addressComponents.getLongName();
            }
        }
        return null;
    }
    public final static String BASE_API_GEO_URL = "https://maps.googleapis.com/maps/api/geocode/";
    public final static String LOGIN_TYPE = "login_type";
    public final static String FILL_DETAILS_TYPE = "fill_details_type";
    public final static String SHARED_PREFERENCES = "com.fieapps.wish.shared_p";
    public final static String TOKEN = "token";
    public final static String IS_STORE = "is_store";
    public final static String PIN_CODE = "postal_code";
    public final static String CITY = "administrative_area_level_2";
    public final static String IS_SEARCH = "is_search";
    public final static String CAT = "cat";
    public final static String CAT_NAME = "cat_name";
    public final static String STORE_ID = "store_id";
    public final static String STORE_NAME = "store_name";
    public final static String IMAGE_URL = "image_url";
    public final static String ORDER_LIST = "order_list";
    public final static int PICK_IMAGE = 565;
    public final static String HOME_ACTIVITY_FRAGMENT = "home_fragment";
    public final static String ORDER_ID = "order_id";
    public final static String TEMP_ID = "temp_id";
    public final static String RESET_ID = "reset_id";
    public final static String PHONE_NUMBER = "phone_number";
    public final static String PASSWORD = "password";
    public final static String RESET_PASSWORD = "reset_password";
    public final static String IS_ORDER = "is_order";

    public static String token(Activity activity){
        String token  = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.TOKEN, null);
        if (token == null){
            activity.startActivity(new Intent(activity, MainActivity.class));
        }
        return token;
    }
    public static void showSofInput(Context context){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    // Only from activity
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showCancelDialog(Activity activity, String title, AbortDialogListener abortDialogListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (title != null) builder.setTitle(title);

        builder.setPositiveButton("Abort", (dialogInterface, i) -> {
            abortDialogListener.onAbort();
        });
        builder.setNegativeButton("No", null);
        builder.create().show();
    }



    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static class HorizontalSpacingDecoration extends RecyclerView.ItemDecoration {

        private int spacing;

        public HorizontalSpacingDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            final int itemPosition = parent.getChildAdapterPosition(view);
            if (itemPosition == RecyclerView.NO_POSITION) {
                return;
            }

            final int itemCount = state.getItemCount();

            if (itemPosition == 0) {
                outRect.set(spacing*4, 0, spacing, 0);
            }

            else if (itemCount > 0 && itemPosition == itemCount - 1) {
                outRect.set(spacing, 0, spacing*4, 0);
            }else{
                outRect.set(spacing, 0, spacing, 0);
            }



        }
    }
    public static class OrderSpacing extends RecyclerView.ItemDecoration {
        private int spacing;

        public OrderSpacing(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);

            // item position
            final int itemPosition = parent.getChildAdapterPosition(view);
            if (itemPosition == RecyclerView.NO_POSITION) {
                return;
            }

            final int itemCount = state.getItemCount();

            if (itemPosition == 0) {
                outRect.set(0, 0, 0, 0);
            }else if (itemPosition == 1){
                outRect.set(0, 0, 0, spacing);
            }
            else if (itemCount > 1 && itemPosition == itemCount - 1) {
                outRect.set(0, spacing, 0, 0);
            }

            else {
                outRect.set(0, spacing, 0, spacing);
            }
        }


    }

    public static class OrderItemSpacing extends RecyclerView.ItemDecoration {

        private int spacing;

        private int orientation;

        public OrderItemSpacing(int spacing, int orientation) {
            this.spacing = spacing;
            this.orientation = orientation;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);

            if (orientation == LinearLayoutManager.HORIZONTAL){
                // item position
                final int itemPosition = parent.getChildAdapterPosition(view);
                if (itemPosition == RecyclerView.NO_POSITION) {
                    return;
                }

                final int itemCount = state.getItemCount();

                if (itemPosition == 0) {
                    outRect.set(spacing*4, spacing, spacing, spacing);
                }

                else if (itemCount > 0 && itemPosition == itemCount - 1) {
                    outRect.set(spacing, spacing, spacing*4, spacing);
                }

                else {
                    outRect.set(spacing, spacing, spacing, spacing);
                }
            }else{
                outRect.set(spacing*2, spacing, spacing*2, spacing);
            }


        }
    }


    public static void checkReadPermission(Activity activity,int CODE){
        if(ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    CODE);
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        OutputStream out;
        File file = new File(getFilename(context));

        try {
            if (file.createNewFile()) {
                InputStream iStream = context != null ? context.getContentResolver().openInputStream(contentUri) : context.getContentResolver().openInputStream(contentUri);
                byte[] inputData = getBytes(iStream);
                out = new FileOutputStream(file);
                out.write(inputData);
                out.close();
                return file.getAbsolutePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static String getFilename(Context context) {
        File mediaStorageDir = new File(context.getExternalFilesDir(""), "patient_data");
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

        String mImageName = "IMG_" + String.valueOf(System.currentTimeMillis()) + ".png";
        return mediaStorageDir.getAbsolutePath() + "/" + mImageName;

    }



    public static void selectPicture(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public static  boolean isStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public static  boolean isCameraPermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }



    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            outRect.top = space;

            // Add top margin only for the first item to avoid double space between item
        }
    }

    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}
