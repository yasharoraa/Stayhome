package com.fieapps.wish.ViewHolders;

import android.os.SystemClock;
import android.view.View;

import androidx.annotation.NonNull;

public abstract class BaseViewHolderClickListener<T> extends BaseViewHolder<T> implements View.OnClickListener {

    private long mLastClickTime = 0;

    public BaseViewHolderClickListener(@NonNull View itemView) {
        super(itemView);
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
