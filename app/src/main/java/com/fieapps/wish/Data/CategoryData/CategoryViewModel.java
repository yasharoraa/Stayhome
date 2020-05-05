package com.fieapps.wish.Data.CategoryData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fieapps.wish.Models.Category;

import java.util.List;

public class CategoryViewModel extends ViewModel {

        private MutableLiveData<List<Category>> categoryLiveData;
        private CategoriesRepo categorysRepo;
        public void init(){
            if (categoryLiveData != null && categoryLiveData.getValue()!=null) return;
            categorysRepo = CategoriesRepo.getInstance();
            categoryLiveData = categorysRepo.getCategories();
        }
        public LiveData<List<Category>> getCategoriesRepo() {
            return categoryLiveData;
        }
    


}
