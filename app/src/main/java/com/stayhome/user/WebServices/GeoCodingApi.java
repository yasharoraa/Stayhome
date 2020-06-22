package com.stayhome.user.WebServices;

import com.stayhome.user.Models.Geocoding.Reverse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeoCodingApi {

    @GET("json")
    Call<Reverse> executeReverseGeoCoding(@Query("latlng") String latLong,@Query("key") String apiKey);
}
