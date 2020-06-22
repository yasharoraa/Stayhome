package com.stayhome.user.WebServices;

import com.stayhome.user.BuildConfig;
import com.stayhome.user.Utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServiceGenerator {

    private static Retrofit retrofit;
    private static Retrofit geoRetrofit;
    private static Gson gson = new GsonBuilder().create();
    private static final String LOT_POT = "lot_pot";
    private static final String LOT_POT_FOR_REAL = "ZTUwYzZhMDgzNmQ3OTI0ODI0NzJhNzRi";
    private static final String KIT_MIT = "kit_mit";
    private static final String KIT_MIT_FOR_REAL = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkdOVE9lTHZOZXF3cVVvd3dpb3ZwOSJ9.eyJpc3MiOiJodHRwczovL3N0YXktaG9tZS5hdXRoMC5jb20vIiwic3ViIjoiRHBNenliZHBpM1VmUm5yb29Nd3hRcEo2aTc2NmdsWVNAY2xpZW50cyIsImF1ZCI6ImFkYWxiYWRhbC5jb20iLCJpYXQiOjE1ODk0NTY3MTcsImV4cCI6MTU4OTU0MzExNywiYXpwIjoiRHBNenliZHBpM1VmUm5yb29Nd3hRcEo2aTc2NmdsWVMiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.I9b1AQYOvZmxVy2lLoyhkMHoZAmfmOII8m0MPsX083cQ1qs8Wswpv2EbwKGDg9uNRIAcULqaPHQkt_cA0kFKzX-6mA_aBEk8_M9eFqh0NuRcoyJZPhVSqRabmb-e3_VHiaw8e_wQiO2k0QDdi07GsiWeyYCA-AmDLKgnE_FY_ZmMwZjBkNDllOGU3NDJiYmJiYWQ4ZDhjODM5NjFjNTc0MzkyNWJjZjE1YzVlN2MxYzk2MmY0M2Vipfqe4ICxyl9EXrN1fhHaIcbCoQuzGBNmvGbIuNusq66U8C-o-nB99T35pVIfdd6bnN3nE6cW38QgDgcpKRqH66NJc61kwbM1g";
    public static   <T> T createUploadService(Class<T> serviceClass){

        OkHttpClient.Builder logInBuilder = new OkHttpClient.Builder();
        logInBuilder.connectTimeout(30, TimeUnit.SECONDS);
        logInBuilder.readTimeout(30,TimeUnit.SECONDS);
        logInBuilder.writeTimeout(30,TimeUnit.SECONDS);
        logInBuilder.
                addInterceptor(chain -> {
            Request request = chain.request();
            Request newRequest;
            newRequest = request.newBuilder()
                    .addHeader(LOT_POT,LOT_POT_FOR_REAL)
                    .addHeader(KIT_MIT,KIT_MIT_FOR_REAL)
                    .build();
            return chain.proceed(newRequest);
        });

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .client(logInBuilder.build())
                    .baseUrl(BuildConfig.BASE_URL + "api/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(serviceClass);
    }


    public static   <T> T createService(Class<T> serviceClass){

        OkHttpClient.Builder logInBuilder = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request request = chain.request();
            Request newRequest;

            newRequest = request.newBuilder()
                    .addHeader(LOT_POT,LOT_POT_FOR_REAL)
                    .addHeader(KIT_MIT,KIT_MIT_FOR_REAL)
                    .build();
            return chain.proceed(newRequest);
        });

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .client(logInBuilder.build())
                    .baseUrl(BuildConfig.BASE_URL + "api/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(serviceClass);
    }

    public static   <T> T createGeoService(Class<T> serviceClass){

        OkHttpClient.Builder logInBuilder = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request request = chain.request();
            Request newRequest;

            newRequest = request.newBuilder()
                    .build();
            return chain.proceed(newRequest);
        });

        if(geoRetrofit == null){
            geoRetrofit = new Retrofit.Builder()
                    .client(logInBuilder.build())
                    .baseUrl(Constants.BASE_API_GEO_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return geoRetrofit.create(serviceClass);
    }



}
