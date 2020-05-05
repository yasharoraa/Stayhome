package com.fieapps.wish.WebServices;

import androidx.annotation.Keep;

import com.fieapps.wish.Models.Address;
import com.fieapps.wish.Models.Category;
import com.fieapps.wish.Models.CreateUser;
import com.fieapps.wish.Models.Order.CreateOrder;
import com.fieapps.wish.Models.Order.Order;
import com.fieapps.wish.Models.Order.OrderElement;
import com.fieapps.wish.Models.Order.OrderForStore;
import com.fieapps.wish.Models.Order.OrderForUser;
import com.fieapps.wish.Models.Store.CreateStore;
import com.fieapps.wish.Models.Store.Store;
import com.fieapps.wish.Models.TopSlide;
import com.fieapps.wish.Models.User.User;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

@Keep
public interface ApiInterface {

    String AUTH = "Authorization";
    String OFFSET = "offset";

    @GET("orders/user")
    Call<List<OrderForUser>> getUserOrders(@Header(AUTH) String token, @Query(OFFSET) int offset);

    @GET("orders/store")
    Call<List<OrderForStore>> getStoreOrders(@Header(AUTH) String token, @Query(OFFSET) int offset);

    @GET("orders/user/{id}")
    Call<OrderForUser> getUserOrderById(@Header(AUTH) String token, @Path(value = "id") String id);

    @GET("orders/store/{id}")
    Call<OrderForStore> getStoreOrderById(@Header(AUTH) String token, @Path(value = "id") String id);

    @GET("users")
    Call<User> getMyUserProfile(@Header(AUTH) String token);

    @PUT("users")
    Call<User> updateMyUserProfile(@Header(AUTH) String token, @Body User user);

    @POST("users")
    Call<User> createUserProfile(@Body CreateUser user);

    @POST("users/login")
    Call<User> loginUser(@Body CreateUser createUser);

    @GET("stores")
    Call<Store> getMyStoreProfile(@Header(AUTH) String token);

    @PUT("stores")
    Call<CreateStore> updateMyStoreProfile(@Header(AUTH) String token, @Body CreateStore store);

    @POST("stores")
    Call<Store> createStoreProfile(@Body CreateUser user);

    @POST("stores/login")
    Call<Store> loginStore(@Body CreateUser createUser);

    @GET("categories")
    Call<List<Category>> getCategories();

    @GET("stores/nearby")
    Call<List<Store>> getNearByStoresByCustomPin(@Query("pincode") String pincode);

    @Multipart
    @POST("upload")
    Call<JsonObject> postImage(@Header(AUTH) String token, @Part MultipartBody.Part image, @Query("sub") String sub);

    @GET("slides")
    Call<List<TopSlide>> getSlides();

    @GET("stores/nearby")
    Call<List<Store>> getstores(@Header(AUTH) String token,
                                @Query("offset") Integer offset,
                                @Query("limit") Integer limit,
                                @Query("lat") double latitude,
                                @Query("lon") double longitude,
                                @Query("pincode") int pincode,
                                @Query("city") String city,
                                @Query("cat") String catId,
                                @Query("search") String text);

    @GET("address")
    Call<List<Address>> getAddress(@Header(AUTH) String token);

    @POST("address")
    Call<Address> postAddress(@Header(AUTH) String token, @Body Address address);

    @POST("orders")
    Call<CreateOrder> postOrder(@Header(AUTH) String token, @Body CreateOrder order);

    @GET("orders/user")
    Call<OrderElement> getMyOrders(@Header(AUTH) String token,
                                   @Query("offset") Integer offset,
                                   @Query("limit") Integer limit);

    @GET("orders/user/{order_id}")
    Call<OrderForUser> getOrderById(@Path(value = "order_id",encoded = true) String orderId);

    @PUT("orders/cancel/{order_id}")
    Call<Order> cancelOrder(@Header(AUTH) String token,@Path(value = "order_id",encoded = true) String orderId);
}
