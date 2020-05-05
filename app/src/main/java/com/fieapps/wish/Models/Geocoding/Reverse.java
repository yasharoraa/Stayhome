package com.fieapps.wish.Models.Geocoding;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Reverse {

    @SerializedName("results")
    private Results[] results;

    public Results getResults() {
        return results[0];
    }

    public class Results{

        @SerializedName("address_components")
        private List<AddressComponents> addressComponents;

        @SerializedName("formatted_address")
        private String formattedAddress;

        @SerializedName("place_id")
        private String placeId;

        public List<AddressComponents> getAddressComponents() {
            return addressComponents;
        }

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public String getPlaceId() {
            return placeId;
        }

        public class AddressComponents {
            @SerializedName("long_name")
            private String longName;

            @SerializedName("short_name")
            private String shortName;

            @SerializedName("types")
            private List<String> types;

            public String getLongName() {
                return longName;
            }

            public String getShortName() {
                return shortName;
            }

            public List<String> getTypes() {
                return types;
            }
        }
    }

}

