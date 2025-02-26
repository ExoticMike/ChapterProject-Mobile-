package com.example.chapterproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener gpsListener;
    final int PERMISSION_REQUEST_LOCATION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_map), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initGetLocationButton();
    }

    private void initGetLocationButton() {
        Button locationButton = findViewById(R.id.buttonGetLocation);
        locationButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Snackbar.make(findViewById(R.id.main), "Location permission is needed to display location", Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", v1 -> ActivityCompat.requestPermissions(MapActivity.this, new String[]
                                        {android.Manifest.permission.ACCESS_FINE_LOCATION}, 1)).show();

                    } else {
                        ActivityCompat.requestPermissions(MapActivity.this, new String[]
                                {android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                } else {
                    startLocationUpdates();
                }
            } else {
                startLocationUpdates();
            }
        });
    }

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        EditText editAddress = (EditText) findViewById(R.id.editAddress);
        EditText editCity = (EditText) findViewById(R.id.editCity);
        EditText editState = (EditText) findViewById(R.id.editState);
        EditText editZipCode = (EditText) findViewById(R.id.editZipCode);

        String address = editAddress.getText().toString() + ", " +
                editCity.getText().toString() + ", " +
                editState.getText().toString() + ", " +
                editZipCode.getText().toString();

        List<Address> addresses = null;
        Geocoder geo = new Geocoder(MapActivity.this);
        try {
            locationManager = (LocationManager) getBaseContext().
                    getSystemService(Context.LOCATION_SERVICE);
            gpsListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    TextView textLatitude = (TextView) findViewById(R.id.textLatitude);
                    TextView textLongitude = (TextView) findViewById(R.id.textLongitude);
                    TextView textAccuracy = (TextView) findViewById(R.id.textAccuracy);
                    textLatitude.setText(String.valueOf(location.getLatitude()));
                    textLongitude.setText(String.valueOf(location.getLongitude()));
                    textAccuracy.setText(String.valueOf(location.getAccuracy()));
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(),
                    "Error, Location not available", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        try {
            locationManager.removeUpdates(gpsListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
