package com.stayhome.user.Interfaces;

import com.stayhome.user.Models.Category;

public interface OnStorePageItemClick extends OnStoreItemClick{

    void onSearchBarClick();

    void onCategoryClick(Category category);

    void onTopSlideItemClick(String uri);


}
