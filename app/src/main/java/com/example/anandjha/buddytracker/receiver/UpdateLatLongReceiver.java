package com.example.anandjha.buddytracker.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.anandjha.buddytracker.Model.UserModel;
import com.firebase.client.Firebase;
import com.google.gson.Gson;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by anandjha on 07/03/17.
 */

public class UpdateLatLongReceiver extends BroadcastReceiver  {
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private SharedPreferences.Editor editor;
    private LocationManager locationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGPSEnabled) {
            Log.e("hello", "jai");
            gson = new Gson();
            sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
            editor = sharedPreferences.edit();
            String json = sharedPreferences.getString("usermodel", null);


            UserModel userModelObject = gson.fromJson(json, UserModel.class);


            Location location = getLastKnownLocation(context);
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();


                userModelObject.setLatitude(lat);
                userModelObject.setLongitude(lon);

                String usermodelJson = gson.toJson(userModelObject);


                editor.clear();
                editor.putString("usermodel", usermodelJson);
                editor.commit();


                Firebase.setAndroidContext(context);

                new Firebase("https://buddytracker-d17d5.firebaseio.com/").child("buddy").child(userModelObject.getMobile()).setValue(userModelObject);
            }
        }
    }

    private Location getLastKnownLocation(Context context) {

        LocationManager mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }

            Location l = mLocationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }
}
