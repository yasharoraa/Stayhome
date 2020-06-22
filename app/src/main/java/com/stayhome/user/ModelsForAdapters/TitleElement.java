package com.stayhome.user.ModelsForAdapters;

import android.view.View;

public class TitleElement implements Comparable<TitleElement> {
    private String title;
    private int gravity;
    private int typeface;
    private int left;
    private int top;
    private int right;
    private int bottom;
    private int color;
    private Integer background;
    private View.OnClickListener onClickListener;


    public TitleElement(String title, int gravity, int typeface, int left, int top, int right, int bottom, int color,Integer background, View.OnClickListener onClickListener) {
        this.title = title;
        this.gravity = gravity;
        this.typeface = typeface;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.color = color;
        this.background = background;
        this.onClickListener = onClickListener;
    }

    public String getTitle() {
        return title;
    }

    public int getGravity() {
        return gravity;
    }

    public int getTypeface() {
        return typeface;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

    public int getColor() {
        return color;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public Integer getBackground() {
        return background;
    }

    @Override
    public int compareTo(TitleElement titleElement) {
        return 0;
    }
}
