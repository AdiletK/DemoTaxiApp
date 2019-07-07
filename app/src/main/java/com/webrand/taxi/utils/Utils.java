package com.webrand.taxi.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.webrand.taxi.R;

import java.util.Calendar;
import java.util.Objects;


import static android.content.Context.LOCATION_SERVICE;

public class Utils {

    private Context mContext;

    public Utils(Context mContext) {
        this.mContext = mContext;
    }

    public boolean checkStatusOfGPS(){
       LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        assert locationManager != null;

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }



    public void setLocalNotification(){
        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");

        PendingIntent broadcast = PendingIntent.getBroadcast(mContext, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        int TIMER=5;
        cal.add(Calendar.SECOND, TIMER);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
        }
    }

    public void moveCamera(GoogleMap mMap,LatLng latLng, float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void call(String number) {
        String phoneNum = Objects.requireNonNull(number);
        if(!TextUtils.isEmpty(phoneNum)) {
            String dial = "tel:" + phoneNum;
           mContext.startActivity(new Intent(Intent.ACTION_DIAL,
                    Uri.parse(dial)));
        }

    }

    public void sendSMS(String number){
        String phoneNum = Objects.requireNonNull(number);
        if(!TextUtils.isEmpty(phoneNum)) {
            String smsNumber = String.format("smsto: %s",
                    phoneNum);
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse(smsNumber));
            if (smsIntent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(smsIntent);
            }
        }
    }

    public Bitmap defaultBitmap(Context context) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_local_taxi_black_24dp);
        background.setBounds(0, 0, Objects.requireNonNull(background).getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_local_taxi_black_24dp);
        vectorDrawable.setBounds(40, 20, Objects.requireNonNull(vectorDrawable).getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    public void showToastMessage(String message){
        Toast.makeText(mContext,message,Toast.LENGTH_LONG).show();
    }
}
