package com.stayhome.user.Utils;

import android.os.SystemClock;
import android.view.View;

import androidx.fragment.app.Fragment;

public abstract class SafeClickFragment extends Fragment implements View.OnClickListener {
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
