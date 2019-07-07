package com.webrand.taxi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.webrand.taxi.helpers.MarkerClusterRenderer;
import com.webrand.taxi.helpers.NetworkService;
import com.webrand.taxi.models.CarsResponse;
import com.webrand.taxi.models.ClusterItems;
import com.webrand.taxi.models.CompaniesModel;
import com.webrand.taxi.models.DriversModel;
import com.webrand.taxi.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, Toolbar.OnMenuItemClickListener {
    private static final String TAG = "MapActivity";


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 9004;
    private static final int CALL_PHONE_PERMISSION_REQUEST_CODE = 9003;
    private static final int SETTING_REQUEST_CODE = 9002;

    private static final float DEFAULT_ZOOM = 13;

    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private LatLng currentLatLon;

    private Utils utils;

    private List<CompaniesModel> listOfCompanies;
    private ClusterManager<ClusterItems> clusterManager ;
    private  MaterialButton btnOrderTaxi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        utils = new Utils(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar);

        btnOrderTaxi = findViewById(R.id.btn_order_taxi);

        listOfCompanies = new ArrayList<>();

        btnOrderTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCallPermission();
            }
        });

    }

    private void setupToolbar(Toolbar toolbar) {
        toolbar.inflateMenu(R.menu.menu);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.showToastMessage("Refreshing");
                cleanMap();
            }
        });
    }

    private void cleanMap() {
        if(mMap!=null && clusterManager!=null) {
            mMap.clear();
            clusterManager.clearItems();
        }
        getLocationPermission();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted && utils.checkStatusOfGPS()) {
            getDeviceLocation();
            clusterManager = new ClusterManager<>(this, mMap);
            clusterManager.setRenderer(new MarkerClusterRenderer(this, mMap, clusterManager));

            mMap.setOnCameraIdleListener(clusterManager);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

        }else {
            showSettingsDialog();
        }
    }

    private void getDeviceLocation(){
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionsGranted ){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation!=null) {
                                currentLatLon = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                                utils.moveCamera(mMap,currentLatLon,
                                        DEFAULT_ZOOM);
                                getNearestCars(currentLocation.getLatitude(),currentLocation.getLongitude());

                            }
                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(MapsActivity.this);
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
            case CALL_PHONE_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    showScreenDialog();
                } else {
                    // permission denied
                    utils.showToastMessage("Permission denied");
                }
            }
            case SETTING_REQUEST_CODE:{
                if (grantResults.length>0){
                    if (utils.checkStatusOfGPS()){
                        getLocationPermission();
                    }
                }
            }

        }
    }

    private void getNearestCars(double latitude,double longitude){
        NetworkService
                .getInstance()
                .getJSONApi()
                .getCars(latitude,longitude)
                .enqueue(new Callback<CarsResponse>() {
                    @Override
                    public void onResponse(Call<CarsResponse> call, Response<CarsResponse> response) {
                        if (response.body() != null) {
                            listOfCompanies = response.body().companies;
                            showCars(listOfCompanies);
                            btnOrderTaxi.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<CarsResponse> call, Throwable t) {
                        utils.showToastMessage("Error occurred while getting request!");

                    }
                });
    }

    public void showCars(List<CompaniesModel> companies)  {
            for (final CompaniesModel company : companies) {
                Log.e(TAG,company.name+" "+company.icon);
                Picasso
                    .get()
                    .load(company.icon)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Log.e(TAG,company.name+" "+company.icon);
                            drawMarkers(company,bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            Log.e(TAG,company.name+" failure "+company.icon);
                            drawMarkers(company,utils.defaultBitmap(MapsActivity.this) );
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                           utils.showToastMessage("Loading");
                        }
                    });
            }
    }

    private void drawMarkers(CompaniesModel company, Bitmap icon)  {
        for ( DriversModel driver : company.drivers) {
            clusterManager.addItem(new ClusterItems(company.name,new LatLng(driver.latitude,driver.longitude),icon));
        }
        clusterManager.cluster();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId()==R.id.current_location){
            if (utils.checkStatusOfGPS() && currentLatLon!=null){
                utils.moveCamera(mMap,currentLatLon,
                    DEFAULT_ZOOM);
            }else {
                getLocationPermission();
            }

        }
        return true;
    }

    private void showScreenDialog() {
        OrderTaxiDialog orderTaxiDialog = new OrderTaxiDialog(listOfCompanies);
        orderTaxiDialog.show(getSupportFragmentManager(),TAG);
    }

    private void checkCallPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        CALL_PHONE_PERMISSION_REQUEST_CODE);
        } else {
            // Permission has already been granted
            showScreenDialog();
        }

    }

    private void showSettingsDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("GPS is settings");

        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent,SETTING_REQUEST_CODE);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                utils.showToastMessage("GPS is disable" );
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onResume() {
        Log.e(TAG,"Resume");
        btnOrderTaxi.setVisibility(View.GONE);
        cleanMap();
        super.onResume();
    }
}

