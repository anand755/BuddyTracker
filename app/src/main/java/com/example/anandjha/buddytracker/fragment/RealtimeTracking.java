package com.example.anandjha.buddytracker.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.anandjha.buddytracker.Model.UserModel;
import com.example.anandjha.buddytracker.R;
import com.example.anandjha.buddytracker.constant.Constant;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LOCATION_SERVICE;
import static com.example.anandjha.buddytracker.constant.Constant.listAllUser;

/**
 * Created by anandjha on 06/03/17.
 */

public class RealtimeTracking extends Fragment {

    private SupportMapFragment fm;
    private String provider;
    private Criteria criteria;
    private LocationManager locationManager;
    private View view;
    private GoogleMap Gmap;
    private String mobileFriend;
    UserModel userModel1 = null;
    private double latitude = 0, longitude = 0;
    TimerTask mTimerTask;
    final Handler handler = new Handler();
    Timer t = new Timer();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.realtime_tracking_fragment_layout, container, false);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);

        if (getArguments() != null) {
            mobileFriend = getArguments().getString("mobile");
            if (mobileFriend != null) {
                UserModel userModel = getMobileToUser(mobileFriend);
                if (userModel != null) {
                    latitude = userModel.getLatitude();
                    longitude = userModel.getLongitude();
                }

            }
        }

        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {

            criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, true);


            if (fm != null) {
                fm.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        Gmap = googleMap;
                        getLocation(googleMap);
                        doTimerTask();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
            }

        } else {
            showSettingsAlert();
        }


    }

    private UserModel getMobileToUser(String mob) {

        List<UserModel> listUser = Constant.listAllUser;
        if (listUser != null) {
            for (int i = 0; i < listUser.size(); i++) {
                if (listUser.get(i).getMobile().equals(mob)) {
                    return listUser.get(i);
                }
            }
        }
        return null;

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    private void initializeViews(View view) {
        fm = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_real));
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("GPS is settings");

        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }


    public void getLocation(GoogleMap googleMap) {

        googleMap.clear();
        UserModel userModel = getUser();
        if (userModel != null) {
            latitude = userModel.getLatitude();
            longitude = userModel.getLongitude();
        }
        if (latitude == 0 && longitude == 0) {
            return;
        }

        Gmap = googleMap;
        LatLng latLng = new LatLng(latitude, longitude);

        LatLng latLngCurrent = new LatLng(Constant.CurrentUser.getLatitude(), Constant.CurrentUser.getLongitude());

        // LatLng latLngMid = new LatLng((latitude + Constant.CurrentUser.getLatitude()) / 2, (longitude + Constant.CurrentUser.getLongitude()) / 2);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(5));
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Friend's location " + "Latitude " + latitude + "  Longitude " + longitude).position(new LatLng(latitude, longitude));
        googleMap.addMarker(marker);

        MarkerOptions markerCurrent = new MarkerOptions().position(latLngCurrent).title("Your Current location " + "Latitude " + latLngCurrent.latitude + "  Longitude " + latLngCurrent.longitude).position(latLngCurrent);
        googleMap.addMarker(markerCurrent);

        googleMap.setMyLocationEnabled(true);

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);


    }

    public UserModel getUser() {

        Constant.FIREBASE_REF.child("buddy").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            UserModel userModel = postSnapshot.getValue(UserModel.class);
                            if (userModel.getMobile().toString().equals(mobileFriend)) {
                                userModel1 = userModel;
                            }

                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                }
        );
        return userModel1;
    }


    public void doTimerTask() {

        mTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                                 public void run() {
                                     getLocation(Gmap);

                                 }
                             }

                );
            }
        }

        ;

        t.schedule(mTimerTask, 500, 60000);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (t != null) {
            t.cancel();
        }
    }
}

