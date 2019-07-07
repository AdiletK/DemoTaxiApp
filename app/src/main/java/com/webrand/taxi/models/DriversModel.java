package com.webrand.taxi.models;

import com.google.gson.annotations.SerializedName;

public class DriversModel {
    @SerializedName("lat")
    public double latitude;

    @SerializedName("lon")
    public double longitude;
}
