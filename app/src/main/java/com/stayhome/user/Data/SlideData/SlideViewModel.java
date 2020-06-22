package com.stayhome.user.Data.SlideData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stayhome.user.Models.TopSlide;

import java.util.List;

public class SlideViewModel extends ViewModel {

    private MutableLiveData<List<TopSlide>> slideLiveData;

    private SlidesRepo slideRepo;

    public void init() {
        if (slideLiveData !=null && slideLiveData.getValue()!=null) return;
        slideRepo = SlidesRepo.getInstance();
        slideLiveData = slideRepo.getSlides();
    }
    public LiveData<List<TopSlide>> getSlidesRepo() {
        return slideLiveData;
    }
}
