package com.fieapps.stayhomeindia.Data.SlideData;

import androidx.lifecycle.MutableLiveData;

import com.fieapps.stayhomeindia.Models.TopSlide;
import com.fieapps.stayhomeindia.WebServices.ApiInterface;
import com.fieapps.stayhomeindia.WebServices.ServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SlidesRepo {
    private static SlidesRepo SlidesRepo;
    static SlidesRepo getInstance() {
        if (SlidesRepo == null) {
            SlidesRepo = new SlidesRepo();
        }
        return SlidesRepo;
    }

    private ApiInterface apiInterface;
    private SlidesRepo(){
        apiInterface = ServiceGenerator.createService(ApiInterface.class);
    }
    MutableLiveData<List<TopSlide>> getSlides() {
        MutableLiveData<List<TopSlide>> slideData = new MutableLiveData<>();
        apiInterface.getSlides().enqueue(new Callback<List<TopSlide>>() {
            @Override
            public void onResponse(Call<List<TopSlide>> call, Response<List<TopSlide>> response) {
                if (response.isSuccessful()){
                    slideData.setValue(response.body());
                }else {
                    slideData.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<List<TopSlide>> call, Throwable t) {
                slideData.setValue(null);
            }
        });
        return slideData;
    }
}
