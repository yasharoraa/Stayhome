package com.stayhome.user.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.stayhome.user.Activities.HomeActivityUser;
import com.stayhome.user.Activities.OrderActivity;
import com.stayhome.user.Activities.StoresActivity;
import com.stayhome.user.Adapters.HomePageAdapter;
import com.stayhome.user.Interfaces.ActivityFragmentCommunication;
import com.stayhome.user.Interfaces.OnStorePageItemClick;
import com.stayhome.user.Models.Category;
import com.stayhome.user.Models.Geocoding.UserLocation;
import com.stayhome.user.Models.Store.BaseStore;
import com.stayhome.user.Models.Store.Store;
import com.stayhome.user.ModelsForAdapters.CategoryElement;
import com.stayhome.user.ModelsForAdapters.ItemBarElement;
import com.stayhome.user.ModelsForAdapters.StoreElement;
import com.stayhome.user.ModelsForAdapters.TitleElement;
import com.stayhome.user.ModelsForAdapters.TopSlideElement;
import com.stayhome.user.R;
import com.stayhome.user.Utils.Constants;
import com.stayhome.user.Utils.MyApplication;
import com.stayhome.user.Utils.SafeClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class StoresFragment extends Fragment implements OnStorePageItemClick, ActivityFragmentCommunication {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.shimmer_layout)
    ShimmerFrameLayout container;

    private Unbinder unbinder;
    private HomeActivityUser activity;
    private HomePageAdapter homePageAdapter;
    private String token;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stores_layout, container, false);
        activity = (getActivity() instanceof HomeActivityUser) ? (HomeActivityUser) getActivity() : null;
        if (activity == null) return null;
        activity.setActivityListener(this);
        unbinder = ButterKnife.bind(this, rootView);
        Log.i("DATA", "FRAGMENT_START");
        token = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.TOKEN, null);
        homePageAdapter = new HomePageAdapter(this);
        homePageAdapter.setHasStableIds(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(homePageAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        UserLocation userLocation = ((MyApplication) activity.getApplication()).getLocation();
        if (userLocation!=null){
            getSlideData();
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.showProgress(true);
    }

    private void getStoreData() {
        UserLocation userLocation = ((MyApplication) activity.getApplication()).getLocation();

        activity.storeViewModel.init(token, 0, 5, userLocation.getCoordinates()[0],
                userLocation.getCoordinates()[1], userLocation.getPincode() == null?0:Integer.parseInt(userLocation.getPincode()), userLocation.getCity());

        activity.storeViewModel.getStoresRepo().observe(this, stores -> {
            if (!isSafe()) return;
            if (stores != null) {
                List<StoreElement> list = new ArrayList<>();
                for (Store store : stores) {
                    list.add(new StoreElement(store));
                }
                int size = Math.round(activity.getResources().getDimension(R.dimen.defult_item_layout_margin_double));
                homePageAdapter.add(new TitleElement(getString(R.string.near_by_stores), Gravity.START, Typeface.BOLD, size, size / 2, size / 2, size, R.color.black900, null, null));
                homePageAdapter.addAllStores(list);
                activity.showProgress(false);
                if (list.isEmpty()) {
                    homePageAdapter.add(new TitleElement(getString(R.string.no_nearby_stores), Gravity.CENTER_HORIZONTAL, Typeface.NORMAL, 0, size * 2, 0, size * 2, R.color.color_offline, null, null));
                } else {
                    homePageAdapter.add(new TitleElement(getString(R.string.view_all_near_by_stores), Gravity.CENTER_HORIZONTAL, Typeface.BOLD, 0, size, 0, size, R.color.jalapino_red, R.drawable.store_background, new SafeClickListener() {
                        @Override
                        public void onSafeClick(View view) {
                            startActivity(new Intent(activity,StoresActivity.class));
                        }
                    }));
                }
            } else {
                activity.showProgress(false);
                activity.showError(true);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void getSlideData() {
        activity.slideViewModel.init();
        activity.slideViewModel.getSlidesRepo().observe(this, topSlides -> {
            if (!isSafe()) return;
            if (topSlides != null) {
                TopSlideElement topSlideElement = new TopSlideElement(topSlides);
                homePageAdapter.add(new ItemBarElement(ContextCompat.getDrawable(activity, R.drawable.search), getString(R.string.search_for_store), null));
                homePageAdapter.add(topSlideElement);
                getCategoryData();
            } else {
                activity.showProgress(false);
                activity.showError(true);
                recyclerView.setVisibility(View.GONE);
            }


        });
    }

    private void getCategoryData() {
        Log.i("DATA", "STRATED");
        activity.categoryViewModel.init();
        activity.categoryViewModel.getCategoriesRepo().observe(this, categories -> {
            if (!isSafe()) return;
            if (categories != null) {
                Log.i("DATA", "GOT" + categories.size());
                homePageAdapter.add(new CategoryElement(categories));
                getStoreData();
            } else {
                activity.showProgress(false);
                activity.showError(true);
                recyclerView.setVisibility(View.GONE);
            }

        });
    }

    public void OnLocationDetected() {
        homePageAdapter.clearList();
        getSlideData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        activity.setActivityListener(null);
    }


    @Override
    public void onSearchBarClick() {
        Intent intent = new Intent(new Intent(activity, StoresActivity.class));
        intent.putExtra(Constants.IS_SEARCH,true);
        activity.startActivity(intent);
    }

    @Override
    public void onCategoryClick(Category category) {
        Intent intent = new Intent(activity,StoresActivity.class);
        intent.putExtra(Constants.CAT,category.getId());
        intent.putExtra(Constants.CAT_NAME,category.getName());
        startActivity(intent);
    }

    @Override
    public void onTopSlideItemClick(String uri) {
        if (uri == null) return;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(browserIntent);
    }

    @Override
    public void reloadFragment() {
        activity.showProgress(true);
        homePageAdapter.clearList();
        getSlideData();
    }

    @Override
    public void showProgress(boolean show) {
        if (!isSafe()) return;
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        container.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            container.startShimmer();
        } else {
            container.stopShimmer();
        }
    }

    private boolean isSafe() {
        return !(this.isRemoving() || this.getActivity() == null || this.isDetached() || !this.isAdded() || this.getView() == null);
    }

    @Override
    public void onStoreClick(int position) {
        Comparable store = homePageAdapter.getList().get(position);
        if (store instanceof StoreElement){
            if (((StoreElement) store).getId() == null )
                return;
            BaseStore st = (StoreElement)store;
            if (st.isOnline()){
                Intent intent = new Intent(activity, OrderActivity.class);
                intent.putExtra(Constants.STORE_ID,((StoreElement) store).getId());
                intent.putExtra(Constants.STORE_NAME,((Store) store).getName());
                startActivity(intent);
            }else{
                Toast.makeText(activity, R.string.store_offline,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
