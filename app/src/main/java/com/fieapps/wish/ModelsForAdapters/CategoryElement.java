package com.fieapps.wish.ModelsForAdapters;

import com.fieapps.wish.Models.Category;

import java.util.List;

public class CategoryElement implements Comparable<CategoryElement> {

    private List<Category> list;

    public CategoryElement(List<Category> list) {
        this.list = list;
    }


    public List<Category> getList() {
        return list;
    }

    @Override
    public int compareTo(CategoryElement categoryElement) {
        return 0;
    }
}
