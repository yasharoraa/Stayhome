package com.fieapps.wish.WebServices;

import com.fieapps.wish.Utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServiceGenerator {

    private static Retrofit retrofit;
    private static Retrofit geoRetrofit;
    private static Gson gson = new GsonBuilder().create();

    private static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);

            builder.hostnameVerifier((hostname, session) -> true);
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
                    .build();
            return chain.proceed(newRequest);
        });

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .client(logInBuilder.build())
                    .baseUrl(Constants.BASE_API_URL)
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
                    .build();
            return chain.proceed(newRequest);
        });

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .client(logInBuilder.build())
                    .baseUrl(Constants.BASE_API_URL)
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
