package com.fieapps.wish.ModelsForAdapters;

import androidx.annotation.NonNull;

import com.fieapps.wish.Models.TopSlide;

import java.util.List;

public class TopSlideElement implements Comparable<TopSlideElement> {

    private List<TopSlide> topSlides;

    public TopSlideElement(List<TopSlide> topSlides) {
        this.topSlides = topSlides;
    }

    @Override
    public int compareTo(@NonNull TopSlideElement topSlideElement) {
        return 0;
    }

    public List<TopSlide> getTopSlides() {
        return topSlides;
    }
}
