package com.fieapps.wish.Utils;

import android.os.SystemClock;
import android.view.View;

public abstract class SafeClickListener implements View.OnClickListener {

    private long mLastClickTime = 0;
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
