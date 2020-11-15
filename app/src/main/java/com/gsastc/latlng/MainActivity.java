package com.gsastc.latlng;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    int PERMISSION_ID = 42;
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }

    @SuppressLint({"MissingPermission"})
    private final void getLastLocation() {
        if (this.checkPermissions()) {
            if (this.isLocationEnabled()) {

                mFusedLocationClient.getLastLocation().addOnCompleteListener((Activity)this, task -> {
                    Location location = (Location)task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        List addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String cityName = ((Address)addresses.get(0)).getAddressLine(0);
                        TextView t1 = findViewById(R.id.fieldLatitude);
                        t1.setText(String.valueOf(location.getLatitude()));
                        TextView t2 = findViewById(R.id.fieldLongitude);
                        t2.setText(String.valueOf(location.getLongitude()));
                    }

                });
            } else {
                Toast.makeText((Context)this, (CharSequence)"Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
                this.startActivity(intent);
            }
        } else {
            this.requestPermissions();
        }

    }

    @SuppressLint("MissingPermission")
    private final void requestNewLocationData() {
        LocationRequest mLocationRequest = new  LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0L);
        mLocationRequest.setFastestInterval(0L);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, null,
                Looper.myLooper()
        );
    }

    private final boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private Boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true;
        }
        return false;
    }

    private final void requestPermissions() {
        ActivityCompat.requestPermissions((Activity)this, new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, this.PERMISSION_ID);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.length != 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation();
            }
            else
            {
                finish();
                Log.e("SSSSS", "Deny");
            }
        }
    }


}