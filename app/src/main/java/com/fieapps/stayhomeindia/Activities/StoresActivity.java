package com.fieapps.stayhomeindia.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.fieapps.stayhomeindia.Adapters.StoreAdapter;
import com.fieapps.stayhomeindia.BuildConfig;
import com.fieapps.stayhomeindia.Data.StoreData.StoreActivityViewModel;
import com.fieapps.stayhomeindia.Interfaces.OnStoreItemClick;
import com.fieapps.stayhomeindia.Models.Geocoding.UserLocation;
import com.fieapps.stayhomeindia.Models.Store.Store;
import com.fieapps.stayhomeindia.R;
import com.fieapps.stayhomeindia.Utils.AppBarStateChangeListener;
import com.fieapps.stayhomeindia.Utils.Constants;
import com.fieapps.stayhomeindia.Utils.MyApplication;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class StoresActivity extends AppCompatActivity implements OnStoreItemClick, View.OnClickListener {


    @BindView(R.id.back_image_view)
    ImageView backImageView;

    @BindView(R.id.search_edit_text)
    EditText searchEditText;

    @BindView(R.id.button_search)
    Button buttonSearch;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.progress_bar)
    View progressBar;

    @BindView(R.id.error_button)
    Button errorButton;

    @BindView(R.id.error_layout)
    View errorLayout;

    @BindView(R.id.main_content)
    View contentMain;

    @BindView(R.id.progress_blocks)
    ShimmerFrameLayout progressBlocks;

    @BindView(R.id.fab_search)
    FloatingActionButton fabSearch;

    @BindView(R.id.no_item_found)
    View noItemFoundView;

    @BindView(R.id.category_text_view)
    TextView categoryTextView;

    @BindView(R.id.share_button)
    Button shareButton;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;

    private StoreAdapter storeAdapter;

    private UserLocation userLocation;

    private StoreActivityViewModel storeViewModel;

    private LinearLayoutManager layoutManager;

    Intent intent;

    private final String TAG = this.getClass().getSimpleName();

    private String category;

    String token;

    private String SEARCH_STRING;

    private int OFFSET;

    private boolean isSearch;

    private String categoryName;

    private final String SAVED_CAT = "saved_cat";
    private final String SAVED_CAT_NAME = "saved_cat_name";
    private final String SAVED_TOKEN = "saved_token";
    private final String SAVED_SEARCH = "saved_search";
    private final String SAVED_OFFSET = "saved_offset";
    private final String SAVED_IS_SEARCH = "saved_is_search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_stores);
        ButterKnife.bind(this);
        userLocation = ((MyApplication) getApplication()).getLocation();
        storeViewModel = ViewModelProviders.of(this).get(StoreActivityViewModel.class);
        setUpRecyclerView();
        intent = getIntent();
        if (savedInstanceState == null) {
            token = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.TOKEN, null);
            isSearch = intent.getBooleanExtra(Constants.IS_SEARCH, false);
            this.category = intent.getStringExtra(Constants.CAT);
            this.categoryName = intent.getStringExtra(Constants.CAT_NAME);
            if (isSearch) {
                searchEditText.requestFocus();
                Constants.showSofInput(this);
            }
        } else {
            category = savedInstanceState.getString(SAVED_CAT);
            token = savedInstanceState.getString(SAVED_TOKEN);
            SEARCH_STRING = savedInstanceState.getString(SAVED_SEARCH);
            OFFSET = savedInstanceState.getInt(SAVED_OFFSET);
            isSearch = savedInstanceState.getBoolean(SAVED_IS_SEARCH);
            categoryName = savedInstanceState.getString(SAVED_CAT_NAME);
        }

        if (category == null) {
            categoryTextView.setVisibility(GONE);
        } else {
            categoryTextView.setText(categoryName);
        }
        if (isSearch && (SEARCH_STRING == null || SEARCH_STRING.isEmpty()))
            return;

        initData(OFFSET, category, SEARCH_STRING, false);
    }


    private void setUpRecyclerView() {
        if (storeAdapter == null)
            storeAdapter = new StoreAdapter(this);

        if (layoutManager == null)
            layoutManager = new LinearLayoutManager(this);


        storeAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(storeAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        searchEditText.addTextChangedListener(searchWatcher());
        recyclerView.addOnScrollListener(scrollListener());
        recyclerView.setPadding(0,0,0,Math.round(getResources().getDimension(R.dimen.recycler_view_padding)));
        recyclerView.setClipToPadding(false);
        searchEditText.setOnEditorActionListener(editorListener());
        buttonSearch.setOnClickListener(this);
        backImageView.setOnClickListener(this);
        errorButton.setOnClickListener(this);
        fabSearch.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        appBarLayout.addOnOffsetChangedListener(getAppBarListener());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_CAT, category);
        outState.putString(SAVED_TOKEN, token);
        outState.putString(SAVED_SEARCH, SEARCH_STRING);
        outState.putInt(SAVED_OFFSET, OFFSET);
        outState.putBoolean(SAVED_IS_SEARCH, isSearch);
        outState.putString(SAVED_CAT_NAME, categoryName);
    }


    private void observeData(boolean clearFirst) {
        if (clearFirst) storeAdapter.clear();
        storeViewModel.getStoresRepo().observe(this, stores -> {

            if (stores == null) {
                showError(true);
                return;
            }
            if (OFFSET > 0) {
                showProgress(false, 1);
            } else {
                showProgress(false, 0);
                if (stores.isEmpty())
                    setNoItemsVisibility(true);
            }

            storeAdapter.addAll(stores);
            if (OFFSET > 0) {
                storeViewModel.postValue(storeAdapter.getList());
            }
        });
    }

    private RecyclerView.OnScrollListener scrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
//                if (layoutManager.findLastCompletelyVisibleItemPosition() == storeAdapter.getList().size()-1) {
                    if (storeAdapter.getItemCount() >= OFFSET) {
                        OFFSET = OFFSET + 10;
                        initData(OFFSET, category, SEARCH_STRING, false);
                    }

                }
            }
        };
    }


    private TextWatcher searchWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                buttonSearch.setVisibility(editable.length() > 2 ? View.VISIBLE : GONE);
                if (SEARCH_STRING != null && editable.length() < 3) {
                    SEARCH_STRING = null;
                    OFFSET = 0;

                    if (isSearch) {
                        storeAdapter.clear();
                    } else {
                        initData(OFFSET, category, SEARCH_STRING, true);
                    }
                    Log.i(TAG, "data clear");
                }
            }
        };
    }

    @Override
    public void onStoreClick(int position) {
        Store store = storeAdapter.getList().get(position);
        if (store.isOnline()){
            Intent intent = new Intent(this,OrderActivity.class);
            intent.putExtra(Constants.STORE_ID,store.getId());
            intent.putExtra(Constants.STORE_NAME,storeAdapter.getList().get(position).getName());
            startActivity(intent);
        }else{
            Toast.makeText(this,"Store offline",Toast.LENGTH_SHORT).show();
        }
    }

    void setNoItemsVisibility(boolean show) {
        if (noItemFoundView != null)
            noItemFoundView.setVisibility(show ? View.VISIBLE : GONE);
    }

    private void initData(int offset, String category, String search, boolean clearFirst) {
        showError(false);
        if (OFFSET > 0) {
            showProgress(true, 1);
        } else {
            showProgress(true, 0);
        }
        setNoItemsVisibility(false);
        storeViewModel.init(token,
                offset,
                10,
                userLocation.getCoordinates()[0],
                userLocation.getCoordinates()[1],
                Integer.parseInt(userLocation.getPincode()),
                userLocation.getCity(),
                category,
                search);
        observeData(clearFirst);
    }

    private void showError(boolean show) {
        if (contentMain != null)
            contentMain.setVisibility(show ? GONE : View.VISIBLE);

        if (errorLayout != null)
            errorLayout.setVisibility(show ? View.VISIBLE : GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_search:
                onSearchAction();
                break;
            case R.id.back_image_view:
                Constants.hideKeyboard(StoresActivity.this);
                super.onBackPressed();
                break;
            case R.id.error_button:
                initData(OFFSET, category, SEARCH_STRING, false);
                break;
            case R.id.fab_search:
                appBarLayout.setExpanded(true,true);
                break;
            case R.id.share_button:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Stay Home Vendor");
                    String shareMessage = "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + "com.fieapps.stayhomevendor" + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    //e.toString();
                }
                break;
        }
    }

    private void onSearchAction() {
        if (searchEditText.getText() == null || searchEditText.getText().length() < 3) {
            Toast.makeText(StoresActivity.this, R.string.minimum_three_char, Toast.LENGTH_SHORT).show();
            return;
        }
        SEARCH_STRING = searchEditText.getText().toString();
        OFFSET = 0;
        initData(OFFSET, category, SEARCH_STRING, true);
        Constants.hideKeyboard(StoresActivity.this);
    }

    private TextView.OnEditorActionListener editorListener() {
        return (textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                onSearchAction();
                return true;
            }
            return false;
        };
    }

    private void showProgress(boolean show, int type) {
        if (type == 0) {
            if (progressBlocks == null)
                return;

            progressBlocks.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            if (show) {
                progressBlocks.startShimmer();
            } else {
                progressBlocks.stopShimmer();
            }
        } else {
            if (progressBar != null)
                progressBar.setVisibility(show ? View.VISIBLE : GONE);
        }
    }

    private AppBarStateChangeListener getAppBarListener(){
        return new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.COLLAPSED){
                    fabSearch.setVisibility(View.VISIBLE);
                }else if (state == State.EXPANDED){
                    fabSearch.setVisibility(GONE);
                }
            }
        };
    }
}
