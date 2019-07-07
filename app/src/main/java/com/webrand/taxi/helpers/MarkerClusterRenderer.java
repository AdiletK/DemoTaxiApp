package com.webrand.taxi.helpers;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.webrand.taxi.models.ClusterItems;

public class MarkerClusterRenderer extends DefaultClusterRenderer<ClusterItems> {

    public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<ClusterItems> clusterManager) {
        super(context, map, clusterManager);
    }
    @Override
    protected void onBeforeClusterItemRendered(ClusterItems item, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(item.getIcon()));
        markerOptions.title(item.getTitle());
    }
}
