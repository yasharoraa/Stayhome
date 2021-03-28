package com.stayhome.user.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stayhome.user.Activities.HomeActivityUser;
import com.stayhome.user.Activities.MainActivity;
import com.stayhome.user.Adapters.ChatAdapter;
import com.stayhome.user.Interfaces.ChatActionListener;
import com.stayhome.user.R;
import com.stayhome.user.Utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChatFragment extends Fragment implements ChatActionListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;


    public ChatAdapter chatAdapter;
    private LinearLayoutManager layoutManager;
    private Unbinder unbinder;
    private HomeActivityUser activity;
    private String token;
    private int offset;

    private final String SAVED_TOKEN = "saved_token";
    private final String SAVED_OFFSET = "saved_offset";

    private final String TAG = this.getClass().getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        activity = (getActivity() instanceof HomeActivityUser) ? (HomeActivityUser) getActivity() : null;
        if (activity == null) return null;
        unbinder = ButterKnife.bind(this,rootView);
        if (savedInstanceState == null) {
            token = Constants.token(activity);
        } else {
            token = savedInstanceState.getString(SAVED_TOKEN);
            offset = savedInstanceState.getInt(SAVED_OFFSET);
        }
        if (token == null) {
            startActivity(new Intent(activity, MainActivity.class));
            return null;
        }
        setUpRecyclerView();
        initData(offset);
        return rootView;
    }

    private void setUpRecyclerView() {
        if (chatAdapter == null){
            chatAdapter = new ChatAdapter(ChatFragment.this);
            chatAdapter.setHasStableIds(true);
        }

        if (layoutManager == null)
            layoutManager = new LinearLayoutManager(activity);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.addOnScrollListener(scrollListener());
        recyclerView.setPadding(0, 0, 0, Math.round(getResources().getDimension(R.dimen.recycler_view_padding)));
        recyclerView.setClipToPadding(false);
        int margin = Math.round(getResources().getDimension(R.dimen.defult_item_layout_margin_half));
    }

    private RecyclerView.OnScrollListener scrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
//                if (layoutManager.findLastCompletelyVisibleItemPosition() == storeAdapter.getList().size()-1) {
                    if (chatAdapter.getItemCount() >= offset) {
                        offset = offset + 10;
                        initData(offset);
                    }

                }
            }
        };
    }

    private void initData(int offset){
        this.offset = offset;
        activity.chatViewModel.init(token,offset,10);
        observeData();
    }

    private void observeData(){
        activity.chatViewModel.getChatRepo().observe(this, list -> {
            Log.i(TAG,list.toString());
            chatAdapter.addAll(list);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void OnClick() {

    }
}
