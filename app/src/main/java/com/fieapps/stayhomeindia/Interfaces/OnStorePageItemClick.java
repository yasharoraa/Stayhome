package com.fieapps.stayhomeindia.Interfaces;

import com.fieapps.stayhomeindia.Models.Category;

public interface OnStorePageItemClick extends OnStoreItemClick{

    void onSearchBarClick();

    void onCategoryClick(Category category);

    void onTopSlideItemClick(String uri);


}
