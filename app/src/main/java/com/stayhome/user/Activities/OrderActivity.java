package com.stayhome.user.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stayhome.user.Adapters.OrderItemAdapter;
import com.stayhome.user.Interfaces.ImageUploadCallback;
import com.stayhome.user.Interfaces.OrderItemAction;
import com.stayhome.user.Models.Order.OrderItem;
import com.stayhome.user.R;
import com.stayhome.user.Utils.AsyncTasks;
import com.stayhome.user.Utils.Constants;
import com.roger.catloadinglibrary.CatLoadingView;
import com.takusemba.cropme.CropLayout;
import com.takusemba.cropme.OnCropListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

import static com.stayhome.user.Utils.Constants.getRealPathFromURI;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener, OrderItemAction {

    @BindView(R.id.name)
    TextView nameTextView;

    @BindView(R.id.image_back)
    ImageView backImageView;

    @BindView(R.id.select_view)
    View selectView;

    @BindView(R.id.select_image_button)
    Button selectImageButton;

    @BindView(R.id.or_text_view)
    TextView orTextView;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.crop_view)
    CropLayout cropLayout;

    @BindView(R.id.manual_view)
    View manualView;

    @BindView(R.id.button_done)
    ImageButton buttonDone;

    @BindView(R.id.button_add)
    ImageButton buttonAdd;

    @BindView(R.id.item_name_edit_text)
    EditText itemNameEditText;

    @BindView(R.id.quantity_edit_text)
    EditText quantityEditText;

    @BindView(R.id.unit_edit_text)
    AutoCompleteTextView unitEditText;

    @BindView(R.id.button_continue)
    Button buttonContinue;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.item_count_text_view)
    TextView itemCountTextView;

    @BindView(R.id.button_clear)
    ImageButton buttonClear;

    private OrderItemAdapter adapter;

    private LinearLayoutManager linearLayoutManager;

    private String id;

    private String name;

    private boolean IS_IMAGE;

    private boolean IS_MANUAL;

    private String ImageUri;

    private final String SAVED_ID = "saved_id";

    private final String SAVED_NAME = "saved_name";

    private final String SAVED_IS_IMAGE = "saved_is_image";

    private final String SAVED_IS_MANUAL = "saved_is_manual";

    private final String SAVED_ITEM_EDIT_TEXT = "saved_item";

    private final String SAVED_QUANTITY_EDIT_TEXT = "saved_quantity";

    private final String SAVED_UNIT_EDIT_TEXT = "saved_unit";

    private final String SAVED_IS_FOCUS_LISTENER = "saved_is_focus_listener";

    private final String SAVED_IMAGE_UR = "saved_image_uri";

    private final String SAVED_ITEM_LIST = "saved_item_list";

    private boolean isFocusListener;

    private final String PROGRESS_TAG = "p_tag";

    private final String TAG = this.getClass().getSimpleName();

    private AsyncTasks.ImageConvertClass imageConvertClass;

    private boolean LOADING_PROCESS_RUNNING;

    private Call uploadCall;

    private static final String[] UNITS = new String[]{
            "Kg", "gram", "ml", "litre", "box", "dozen",
    };

    private long mLastClickTime = 0;

    private static final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        setUpRecyclerView();
        if (savedInstanceState == null) {
            id = getIntent().getStringExtra(Constants.STORE_ID);
            name = getIntent().getStringExtra(Constants.STORE_NAME);
        } else {
            id = savedInstanceState.getString(SAVED_ID);
            name = savedInstanceState.getString(SAVED_NAME);
            IS_IMAGE = savedInstanceState.getBoolean(SAVED_IS_IMAGE);
            IS_MANUAL = savedInstanceState.getBoolean(SAVED_IS_MANUAL);
            isFocusListener = savedInstanceState.getBoolean(SAVED_IS_FOCUS_LISTENER);
            if (IS_IMAGE) {

                ImageUri = savedInstanceState.getString(SAVED_IMAGE_UR);

                if (ImageUri != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(ImageUri);
                    setImage(bitmap);
                }else{
                    setLayout(true);
                }
                cropLayout.addOnCropListener(cropListener());

            }
            if (IS_MANUAL) {
                setLayout(false);
                itemNameEditText.setText(savedInstanceState.getString(SAVED_ITEM_EDIT_TEXT));
                quantityEditText.setText(savedInstanceState.getString(SAVED_QUANTITY_EDIT_TEXT));
                unitEditText.setText(savedInstanceState.getString(SAVED_UNIT_EDIT_TEXT));
                itemCountTextView.setText(String.valueOf(adapter.addAllItems(savedInstanceState.getParcelableArrayList(SAVED_ITEM_LIST))));
            }
        }
        nameTextView.setText(name);
        setClickListeners();

    }

    private void setLayout(boolean isImage) {
        IS_IMAGE = isImage;
        IS_MANUAL = !isImage;
        selectView.setVisibility(isImage ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isImage ? View.GONE : View.VISIBLE);
        manualView.setVisibility(isImage ? View.GONE : View.VISIBLE);
        cropLayout.setVisibility(isImage ? View.VISIBLE : View.GONE);
        itemCountTextView.setVisibility(isImage ? View.GONE : View.VISIBLE);
        buttonClear.setVisibility(isImage ? View.INVISIBLE : View.VISIBLE);
        if (isImage)
            orTextView.setVisibility(View.GONE);

    }

    public void setUpRecyclerView() {
        if (adapter == null)
            adapter = new OrderItemAdapter(this);

        if (linearLayoutManager == null)
            linearLayoutManager = new LinearLayoutManager(this,
                    getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? RecyclerView.VERTICAL:RecyclerView.HORIZONTAL,
                    false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        int margin = Math.round(getResources().getDimension(R.dimen.defult_item_layout_margin_half));
        recyclerView.addItemDecoration(new Constants.OrderItemSpacing(margin,linearLayoutManager.getOrientation()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void setClickListeners() {
        selectImageButton.setOnClickListener(this);
        buttonDone.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
        buttonContinue.setOnClickListener(this);
        backImageView.setOnClickListener(this);
        buttonClear.setOnClickListener(this);
        itemNameEditText.setOnEditorActionListener(this);
        quantityEditText.setOnEditorActionListener(this);
        unitEditText.setOnEditorActionListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, UNITS);
        unitEditText.setAdapter(adapter);

        unitEditText.setOnFocusChangeListener((view, b) -> {

            if (isFocusListener) {
                isFocusListener = false;
                return;
            }
            if (b)
                unitEditText.showDropDown();
            else
                unitEditText.dismissDropDown();
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_ID, id);
        outState.putString(SAVED_NAME, name);
        outState.putBoolean(SAVED_IS_IMAGE, IS_IMAGE);
        outState.putBoolean(SAVED_IS_MANUAL, IS_MANUAL);
        outState.putBoolean(SAVED_IS_FOCUS_LISTENER, unitEditText.isFocused());
        if (IS_MANUAL) {
            outState.putString(SAVED_ITEM_EDIT_TEXT, itemNameEditText.getText().toString());
            outState.putString(SAVED_QUANTITY_EDIT_TEXT, quantityEditText.getText().toString());
            outState.putString(SAVED_UNIT_EDIT_TEXT, unitEditText.getText().toString());
            outState.putParcelableArrayList(SAVED_ITEM_LIST,adapter.getList());
        }
        if (IS_IMAGE) {
            outState.putString(SAVED_IMAGE_UR, ImageUri);
        }

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.select_image_button:
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                chooser().create().show();
                break;
            case R.id.button_continue:
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (IS_IMAGE){
                    if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT){
                        makeToast(getString(R.string.rotate_screen));
                        return;
                    }
                    if (cropLayout.isOffFrame()){
                        makeToast(getString(R.string.crop_image));
                    }
                    cropLayout.crop();
                }else if (IS_MANUAL){
                    if (adapter.getItemCount() < 2){
                        makeToast(getString(R.string.minimum_items));
                        return;
                    }
                    Intent intent = new Intent(this, CompleteOrderActivity.class);
                    intent.putExtra(Constants.IS_ORDER,true);
                    intent.putExtra(Constants.STORE_ID, id);
                    intent.putExtra(Constants.ORDER_LIST, adapter.getList());
                    startActivity(intent);
                }
                break;
            case R.id.button_done:
                Constants.hideKeyboard(OrderActivity.this);
                add();
                break;
            case R.id.button_add:
                if ((TextUtils.isEmpty(itemNameEditText.getText()) &&
                        TextUtils.isEmpty(quantityEditText.getText()) &&
                        TextUtils.isEmpty(unitEditText.getText())) || add()) {
                    if (!itemNameEditText.isFocused())
                        itemNameEditText.requestFocus();
                    Constants.showSofInput(OrderActivity.this);
                }
                break;
            case R.id.button_clear:
                clear();
                break;
            case R.id.image_back:
                super.onBackPressed();
                break;
        }
    }

    public boolean add() {
        unitEditText.dismissDropDown();
        OrderItem item = getItem();
        if (item == null) return false;
        if (adapter.checkOne()) {
            setLayout(false);
        }
        itemCountTextView.setText(String.valueOf(adapter.addItem(item)));
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        clear();
        itemNameEditText.requestFocus();
        return true;
    }

    private OrderItem getItem() {
        itemNameEditText.setError(null);
        quantityEditText.setError(null);
        unitEditText.setError(null);

        boolean cancel = false;
        View focusView = null;

        String item = itemNameEditText.getText() != null ? itemNameEditText.getText().toString().trim() : "";
        String quantity = quantityEditText.getText() != null ? quantityEditText.getText().toString().trim() : "";
        String unit = unitEditText.getText() != null ? unitEditText.getText().toString().trim() : "";

        if (item.isEmpty() || item.length() < 3) {
            itemNameEditText.setError(getString(R.string.valid_item));
            focusView = itemNameEditText;
            cancel = true;
        } else if (quantity.isEmpty() || Integer.parseInt(quantity) <= 0) {
            quantityEditText.setError(getString(R.string.valid_quantity));
            focusView = quantityEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return null;
        }
        return new OrderItem(item, Integer.parseInt(quantity), unit.isEmpty() ? null : unit);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            chooseImageFromCamera();
        }
        if (requestCode==1&&grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Constants.selectPicture(this);
            //resume tasks needing this permission
        }
    }

    private void setImage(Bitmap bitmap) {
        if (!IS_IMAGE) {
            cropLayout.addOnCropListener(cropListener());
        }
        setLayout(true);
        cropLayout.setBitmap(bitmap);

    }

    private void setImageUri(Uri uri) {
        if (!IS_IMAGE) {
            cropLayout.addOnCropListener(cropListener());
        }
        setLayout(true);
        cropLayout.setUri(uri);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PICK_IMAGE && resultCode == RESULT_OK
                && null != data) {

            Uri selectedImage = data.getData();
            String imageEncoded = getRealPathFromURI(this, selectedImage);
            ImageUri = imageEncoded;
            Bitmap bitmap = BitmapFactory.decodeFile(imageEncoded);
            setImage(bitmap);
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
//            if (data.getExtras()==null)
//                return;

            File file = new File(getApplicationContext().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "stayhome.jpg");
            Uri uri =  FileProvider.getUriForFile(getApplicationContext(), getPackageName()+".fileprovider", file);
            setImageUri(uri);
        }
    }

    private OnCropListener cropListener() {
        return new OnCropListener() {
            @Override
            public void onSuccess(@NotNull Bitmap bitmap) {
                Log.i(TAG,"SUCCESS");
                String token = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.TOKEN, null);
                if (token == null) return;
                Log.i(TAG,"TOKEN_NOT_NULL");
                LOADING_PROCESS_RUNNING = true;
                progressBar.setVisibility(View.VISIBLE);
                buttonContinue.setVisibility(View.INVISIBLE);
                uploadImage(getCacheDir(), token, "orders", new ImageUploadCallback() {
                    @Override
                    public void onSuccess(String url) {
                        hideDialog(url);
                        makeToast(getString(R.string.upload_success));
                    }

                    @Override
                    public void onCancel() {
                        hideDialog(null);
                        makeToast(getString(R.string.upload_error));
                    }

                    @Override
                    public void onError(String error) {
                        hideDialog(null);
                        makeToast("Error :" + error);
                    }

                    @Override
                    public void OnCallStart(Call call) {
                        uploadCall = call;
                    }
                }, bitmap);
                CatLoadingView mView = new CatLoadingView();
                mView.setCancelable(false);
                mView.show(getSupportFragmentManager(), PROGRESS_TAG);
            }

            @Override
            public void onFailure(@NotNull Exception e) {
                Log.i(TAG,"Failure");
            }
        };
    }


    private void hideDialog(String url) {
        LOADING_PROCESS_RUNNING = false;
        progressBar.setVisibility(View.INVISIBLE);
        buttonContinue.setVisibility(View.VISIBLE);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(PROGRESS_TAG);
        if (fragment != null) {
            DialogFragment dialogFragment = (DialogFragment) fragment;
            dialogFragment.dismiss();
        }
        if (url == null) return;
        Intent intent = new Intent(this, CompleteOrderActivity.class);
        intent.putExtra(Constants.IS_ORDER,true);
        intent.putExtra(Constants.STORE_ID, id);
        intent.putExtra(Constants.IMAGE_URL, url);
        startActivity(intent);

    }

    private void uploadImage(File cache, String token, String type, ImageUploadCallback callback, Bitmap bitmap) {
        imageConvertClass = new AsyncTasks.ImageConvertClass(cache, token, type, callback);
        imageConvertClass.execute(bitmap);
    }


    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void showCancelDialog(Activity activity, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (title != null) builder.setTitle(title);

        builder.setPositiveButton(R.string.abort, (dialogInterface, i) -> {
            if (imageConvertClass != null)
                imageConvertClass.cancel(true);

            if (uploadCall != null)
                uploadCall.cancel();

            super.onBackPressed();
        });
        builder.setNegativeButton(R.string.no, null);
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        if (LOADING_PROCESS_RUNNING) {
            showCancelDialog(OrderActivity.this, getString(R.string.abort_title));
            return;
        }
        super.onBackPressed();
    }


    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        String text = textView.getText().toString();
        switch (textView.getId()) {
            case R.id.item_name_edit_text:
                if (text.length() < 3) {
                    itemNameEditText.setError(getString(R.string.valid_item));
                    itemNameEditText.requestFocus();
                    return true;
                }
                break;
            case R.id.quantity_edit_text:
                if (text.isEmpty() || !text.matches("\\d+(?:\\.\\d+)?") || Integer.parseInt(text) < 1) {
                    itemNameEditText.setError(getString(R.string.valid_quantity));
                    itemNameEditText.requestFocus();
                    return true;
                }
                break;
            case R.id.unit_edit_text:
                add();
                return true;
        }
        return false;
    }


    private void clear() {
        itemNameEditText.setText("");
        quantityEditText.setText("");
        unitEditText.setText("");
    }

    @Override
    public void onRemoved(int position) {
        itemCountTextView.setText(String.valueOf(adapter.removeItem(position)));
    }
    private AlertDialog.Builder chooser() {
        return new AlertDialog.Builder(this)
                .setTitle(R.string.image_chooser_title)
                .setItems(getResources().getStringArray(R.array.dialog_array), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 1) {
                            /*chooseImage();*/
                            if (Constants.isStoragePermissionGranted(OrderActivity.this))
                                Constants.selectPicture(OrderActivity.this);
                        } else {
                            chooseImageFromCamera();
                        }
                    }
                });
    }

  private void chooseImageFromCamera(){
        if (Constants.isCameraPermissionGranted(OrderActivity.this)){
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(getApplicationContext().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "stayhome.jpg");
            Uri uri =  FileProvider.getUriForFile(getApplicationContext(), getPackageName()+".fileprovider", file);
//            Uri uri = Uri.fromFile(file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(cameraIntent,CAMERA_REQUEST);
        }
  }

}
