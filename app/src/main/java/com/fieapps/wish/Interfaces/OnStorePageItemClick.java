package com.fieapps.wish.Interfaces;

import com.fieapps.wish.Models.Category;

public interface OnStorePageItemClick extends OnStoreItemClick{

    void onSearchBarClick();

    void onCategoryClick(Category category);

    void onTopSlideItemClick(String uri);


}
