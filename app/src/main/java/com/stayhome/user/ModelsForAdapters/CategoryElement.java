package com.stayhome.user.ModelsForAdapters;

import com.stayhome.user.Models.Category;

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
