package com.stayhome.user.ViewHolders;

import android.os.SystemClock;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.ButterKnife;

public abstract class SafeClickViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private long mLastClickTime = 0;

    public SafeClickViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        onSafeClick(view);
    }

    public abstract void onSafeClick(View view);
}
