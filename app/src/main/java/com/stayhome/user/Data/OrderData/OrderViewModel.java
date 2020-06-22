package com.stayhome.user.Data.OrderData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stayhome.user.Models.Order.OrderElement;
import com.stayhome.user.Models.Order.OrderForUser;

import java.util.List;

public class OrderViewModel extends ViewModel {

    private MutableLiveData<OrderElement> orderLiveData;

    private OrdersRepo ordersRepo;

    private int initialOffset;

    private final String TAG = this.getClass().getSimpleName();

    public void init(String token,int offset,int limit,boolean clear){
        if (orderLiveData !=null && orderLiveData.getValue()!=null && initialOffset == offset && !clear)
            return;

        initialOffset = offset;

        ordersRepo = OrdersRepo.getInstance();
        orderLiveData = ordersRepo.getOrders(token,offset,limit);
    }

    public LiveData<OrderElement> getOrdersRepo() {
        return orderLiveData;
    }

    public void postValue(List<OrderForUser> orders, int count){
        orderLiveData = new MutableLiveData<>();
        orderLiveData.postValue(new OrderElement(orders,count));
    }
}
