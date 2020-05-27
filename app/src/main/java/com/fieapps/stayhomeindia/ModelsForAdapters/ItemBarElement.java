package com.fieapps.stayhomeindia.ModelsForAdapters;

import android.graphics.drawable.Drawable;
import android.view.View;

public class ItemBarElement implements Comparable<ItemBarElement>{

    private Drawable drawable;

    private String textView;

    private View.OnClickListener onClickListener;

    public ItemBarElement(Drawable drawable, String textView, View.OnClickListener onClickListener) {
        this.drawable = drawable;
        this.textView = textView;
        this.onClickListener = onClickListener;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public String getText() {
        return textView;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    @Override
    public int compareTo(ItemBarElement itemBarElement) {
        return 0;
    }
}
