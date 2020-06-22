package com.stayhome.user.Interfaces;


import com.stayhome.user.Models.Address;

public interface AddressSelectListener {
    void onAddressSelected(Address address);
    void onAddressDelete(Address address);
}
