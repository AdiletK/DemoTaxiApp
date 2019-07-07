package com.webrand.taxi.models;



import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterItems implements ClusterItem {
   private String company;
   private LatLng latLng;
   private Bitmap icon;

    public Bitmap getIcon() {
        return icon;
    }

    public ClusterItems(String company, LatLng latLng, Bitmap bitmap) {
        this.company = company;
        this.latLng = latLng;
        this.icon = bitmap;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    @Override
    public String getTitle() {
        return company;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
