package com.example.anandjha.buddytracker.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.anandjha.buddytracker.R;
import com.example.anandjha.buddytracker.constant.Constant;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by anandjha on 28/02/17.
 */

public class TrackFragment extends Fragment implements LocationListener {
    SupportMapFragment fm;
    String provider;
    Criteria criteria;
    LocationManager locationManager;
    GoogleMap Gmap;
    Marker now;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.track_fragment_layout, container, false);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);

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
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
            }

        } else {
            showSettingsAlert();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    private void initializeViews(View view) {
        fm = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
    }

    private Location getLastKnownLocation(LocationManager mLocationManager) {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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


    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (now != null) {
            now.remove();
        }

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        now = Gmap.addMarker(new MarkerOptions().position(latLng).title("Latitude " + latitude + "  Longitude " + longitude).position(new LatLng(latitude, longitude)));
        Gmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    public void getLocation(GoogleMap googleMap) {
        Location location = getLastKnownLocation(locationManager);

        if (location != null) {
            onLocationChanged(location);
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            LatLng latLng = new LatLng(latitude, longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Latitude " + latitude + "  Longitude " + longitude).position(new LatLng(latitude, longitude));
            googleMap.addMarker(marker);

            /*googleMap.addCircle(new CircleOptions()
                    .center(new LatLng(location.getLatitude(), location.getLongitude()))
                    .radius(100)
                    .strokeColor(Color.RED)
                    .fillColor(Color.BLUE));*/
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
        }

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 20000, 0, this);

    }

}
