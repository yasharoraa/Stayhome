package com.fieapps.wish.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fieapps.wish.Activities.HomeActivityUser;
import com.fieapps.wish.Activities.MainActivity;
import com.fieapps.wish.Adapters.CatSelectGridAdapter;
import com.fieapps.wish.Data.CategoryData.CategoryViewModel;
import com.fieapps.wish.Interfaces.OnCategoryItemSelected;
import com.fieapps.wish.Models.Category;
import com.fieapps.wish.Models.Geocoding.Reverse;

import com.fieapps.wish.Models.Geocoding.UserLocation;
import com.fieapps.wish.Models.Store.CreateStore;
import com.fieapps.wish.Models.User.User;
import com.fieapps.wish.R;
import com.fieapps.wish.Utils.Constants;
import com.fieapps.wish.Utils.MyApplication;
import com.fieapps.wish.WebServices.ApiInterface;
import com.fieapps.wish.WebServices.ServiceGenerator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fieapps.wish.Utils.Constants.CITY;
import static com.fieapps.wish.Utils.Constants.PIN_CODE;
import static com.fieapps.wish.Utils.Constants.findComponent;

public class FillDetailsFragment extends Fragment implements OnCategoryItemSelected {

    private Unbinder unbinder;

    private MainActivity activity;

    private int i;

    @BindView(R.id.name_edit_text)
    TextInputEditText nameEditText;

    @BindView(R.id.address_edit_text)
    EditText addressEditText;

    @BindView(R.id.button_update_details)
    Button buttonUpdateDetails;

    @BindView(R.id.owner_name_layout)
    TextInputLayout ownerNameLayout;

    @BindView(R.id.gstin_layout)
    TextInputLayout gstinLayout;

    @BindView(R.id.formattedAddressTextView)
    TextView formattedAddressTextView;

    @BindView(R.id.gstin_edit_text)
    EditText gstinEditText;

    @BindView(R.id.recycler_view)
    RecyclerView gridView;

    @BindView(R.id.title_text_view)
    TextView categoryTitle;

    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;

    @BindView(R.id.owner_name_edit_text)
    EditText ownerNameEditText;
    private CategoryViewModel categoryViewModel;
    private List<Category> selectedCategories;
    private CatSelectGridAdapter categoryGridAdapter;

    private UserLocation userLocation;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fill__details, container, false);
        activity = (getActivity() instanceof MainActivity) ? (MainActivity) getActivity() : null;
        if (activity == null || getArguments() == null) return rootView;
//        i = getArguments().getInt(Constants.FILL_DETAILS_TYPE);
        i = 1;
        unbinder = ButterKnife.bind(this, rootView);
        if (i != 1) {
            nameEditText.setHint("Name");
            gstinLayout.setVisibility(View.GONE);
            ownerNameLayout.setVisibility(View.GONE);
        }
        buttonUpdateDetails.setOnClickListener(view -> {
            if (i == 1) {
                setStoreDetails();

            } else {
                setUserDetails();
            }
        });
        activity.getLastLocation(true);
        if (selectedCategories == null)
            selectedCategories = new ArrayList<>();

        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        getCategoryData();
        categoryTitle.setText("Select Category");
        categoryTitle.setTextColor(Color.WHITE);
        gstinEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE){
                scrollView.post(() -> scrollView.fullScroll(NestedScrollView.FOCUS_DOWN));
            }
            return false;
        });
        if (userLocation==null) userLocation = ((MyApplication)activity.getApplication()).getLocation();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void OnLocationDetected(UserLocation userLocation) {
        activity.toggleProgress(false,"");
        this.userLocation = userLocation;
        formattedAddressTextView.setText(userLocation.getFormattedAddress());
        String[] texts = userLocation.getFormattedAddress().split(",");
        if (texts.length<2) return;
        addressEditText.setText(String.format("%s, %s", texts[0], texts[1]));
    }

    private void setStoreDetails() {
        CreateStore store = getStore();
        if (store == null) return;
        String token = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.TOKEN, null);
        if (token == null) return;
        activity.toggleProgress(true,"Updating store details");
        ServiceGenerator.createService(ApiInterface.class).updateMyStoreProfile(token.trim(), store).enqueue(new Callback<CreateStore>() {
            @Override
            public void onResponse(Call<CreateStore> call, Response<CreateStore> response) {
                if (response.isSuccessful()) {
                    makeToast("Details updated successfully");
                    activity.change(Constants.MainEnum.UPLOAD_IMAGE_STORE);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        JSONObject errJson = jsonObject.getJSONObject(jsonObject.keys().next());
                        String key = errJson.getString(errJson.keys().next());
                        makeToast(key);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        makeToast("Details not updated. Please try again !");
                    }
                }
                activity.change(Constants.MainEnum.LOADING_OFF);
            }

            @Override
            public void onFailure(Call<CreateStore> call, Throwable t) {
                activity.change(Constants.MainEnum.LOADING_OFF);
                makeToast("Details not updated. Please try again !");
            }
        });

    }

    private void setUserDetails() {
        User user = getUser();
        if (user == null) return;
        String token = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.TOKEN, null);
        if (token == null) return;
        activity.toggleProgress(true,"Updating user details");
        ServiceGenerator.createService(ApiInterface.class).updateMyUserProfile(token, user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    makeToast("Details updated successfully");
                    activity.startActivity(new Intent(activity, HomeActivityUser.class));
                    return;
                } else {
                    if (response.errorBody() == null) {
                        makeToast("Details not updated. Please try again !");
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            JSONObject errJson = jsonObject.getJSONObject(jsonObject.keys().next());
                            String key = errJson.getString(errJson.keys().next());
                            makeToast(key);
                        } catch (JSONException | IOException | NullPointerException e) {
                            e.printStackTrace();
                            makeToast("Details not updated. Please try again !");
                        }
                    }

                }
                activity.change(Constants.MainEnum.LOADING_OFF);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                activity.change(Constants.MainEnum.LOADING_OFF);
                makeToast("Details not updated. Please try again !");
            }
        });

    }

    private void makeToast(String text) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }




    private User getUser() {
        nameEditText.setError(null);
        addressEditText.setError(null);
        boolean cancel = false;

        View focusView = null;

        String name = nameEditText.getText() != null ? nameEditText.getText().toString().trim() : "";
        String address = addressEditText.getText() != null ? addressEditText.getText().toString().trim() : "";

        if (name.isEmpty() || name.length() < 3) {
            nameEditText.setError("Please enter a valid name");
            focusView = nameEditText;
            cancel = true;
        } else if (address.isEmpty() || address.length() < 6) {
            addressEditText.setError("Please enter a valid address");
            focusView = nameEditText;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
            return null;
        }
        return null;
    }

    private CreateStore getStore() {



        nameEditText.setError(null);
        addressEditText.setError(null);
        ownerNameEditText.setError(null);
        gstinLayout.setError(null);

        boolean cancel = false;
        View focusView = null;

        String name = nameEditText.getText() != null ? nameEditText.getText().toString().trim() : "";
        String address = addressEditText.getText() != null ? addressEditText.getText().toString().trim() : "";
        String ownerName = ownerNameEditText.getText() != null ? ownerNameEditText.getText().toString().trim() : "";
        String Gstin = gstinEditText.getText() != null ? gstinEditText.getText().toString().trim() : "";

        if (name.isEmpty() || name.length() < 3) {
            nameEditText.setError("Please enter a valid name");
            focusView = nameEditText;
            cancel = true;
        } else if (address.isEmpty() || address.length() < 6) {
            addressEditText.setError("Please enter a valid address");
            focusView = nameEditText;
            cancel = true;
        } else if (ownerName.isEmpty() || ownerName.length() < 3) {
            ownerNameEditText.setError("Please enter a valid name");
            focusView = ownerNameEditText;
            cancel = true;
        } else if (!isValidGstin(Gstin)) {
            gstinEditText.setError("Please enter a valid gstin");
            focusView = gstinEditText;
            cancel = true;
        }

        String placeId = userLocation.getPlaceId();

        double[] coordinates = userLocation.getCoordinates();

        if (cancel) {
            focusView.requestFocus();
            return null;
        }

        if (selectedCategories.isEmpty()){
            makeToast("Select category to continue.");
            return null;
        }

        String[] categories = new String[selectedCategories.size()];

        for (int i=0; i<selectedCategories.size(); i++){
            categories[i] = selectedCategories.get(i).getId();
        }

        if (userLocation.getPincode()== null || userLocation.getCity()== null || placeId== null || coordinates == null) {
            makeToast("Location Error, Please try again later.");
            return null;
        }

        return new CreateStore(name,ownerName,Gstin,address,Integer.parseInt(userLocation.getPincode()),userLocation.getCity(),placeId,coordinates,categories);
    }

    private static boolean isValidGstin(String input) {
        Pattern pattern;
        Matcher matcher;
        String PATTERN = "\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}[A-Z\\d]{1}[Z]{1}[A-Z\\d]{1}";
        pattern = Pattern.compile(PATTERN);
        matcher = pattern.matcher(input);
        return matcher.matches();
    }


    private void getCategoryData() {
        Log.i("DATA", "STRATED");
        categoryViewModel.init();
        categoryViewModel.getCategoriesRepo().observe(this, categories -> {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, 3);
            gridView.setLayoutManager(gridLayoutManager);
            int margin = Math.round(activity.getResources().getDimension(R.dimen.defult_item_layout_margin));
            gridView.addItemDecoration(new Constants.SpacesItemDecoration(margin / 2));
            categoryGridAdapter = new CatSelectGridAdapter(this, categories, selectedCategories);
            gridView.setAdapter(categoryGridAdapter);
        });
    }


    @Override
    public void onCategoryItemSelected(Category category) {
        boolean value = (selectedCategories.contains(category)) ? selectedCategories.remove(category) : selectedCategories.add(category);
        categoryGridAdapter.notifyDataSetChanged();
    }


}
