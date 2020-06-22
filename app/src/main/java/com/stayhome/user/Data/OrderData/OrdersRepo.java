package com.stayhome.user.Data.OrderData;

import androidx.lifecycle.MutableLiveData;

import com.stayhome.user.Models.Order.OrderElement;
import com.stayhome.user.WebServices.ApiInterface;
import com.stayhome.user.WebServices.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class OrdersRepo {

    private static OrdersRepo ordersRepo;

    static OrdersRepo getInstance() {
        if (ordersRepo == null){
            ordersRepo = new OrdersRepo();
        }
        return ordersRepo;
    }

    private ApiInterface apiInterface;

    private OrdersRepo() {
        apiInterface = ServiceGenerator.createService(ApiInterface.class);
    }

    MutableLiveData<OrderElement> getOrders(String token,int offset,int limit) {
        MutableLiveData<OrderElement> ordersData = new MutableLiveData<>();
        apiInterface.getMyOrders(token,offset,limit).enqueue(new Callback<OrderElement>() {
            @Override
            public void onResponse(Call<OrderElement> call, Response<OrderElement> response) {
                if (response.isSuccessful()) {
                    ordersData.setValue(response.body());
                } else {
                    ordersData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<OrderElement> call, Throwable t) {
                ordersData.setValue(null);
            }
        });
        return ordersData;
    }
}