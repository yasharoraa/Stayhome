package com.stayhome.user.Data.CategoryData;

import androidx.lifecycle.MutableLiveData;

import com.stayhome.user.Models.Category;
import com.stayhome.user.WebServices.ApiInterface;
import com.stayhome.user.WebServices.ServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class CategoriesRepo {
    private static CategoriesRepo categoriesRepo;
    static CategoriesRepo getInstance() {
        if (categoriesRepo == null) {
            categoriesRepo = new CategoriesRepo();
        }
        return categoriesRepo;
    }

    private ApiInterface apiInterface;
    private CategoriesRepo(){
        apiInterface = ServiceGenerator.createService(ApiInterface.class);
    }
    MutableLiveData<List<Category>> getCategories() {
        MutableLiveData<List<Category>> categoryData = new MutableLiveData<>();
        apiInterface.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()){
                    categoryData.setValue(response.body());
                }else {
                    categoryData.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                categoryData.setValue(null);
            }
        });
        return categoryData;
    }

}
