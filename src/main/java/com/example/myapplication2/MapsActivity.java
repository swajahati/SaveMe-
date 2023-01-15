package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnCompleteListener;






import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication2.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public double maplat, maplong;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;


    public double lats, lgs;
    //----------------------------------save me Work -----------------------------//


    FusedLocationProviderClient mFusedLocationClient;
    SharedPreferences sharedPreferences;

    TextView latitudeTextView, longitTextView, detailsTextView;
    int PERMISSION_ID = 44;
    Button send , cleardetails;
    String phoneNumber, name, message, alldetails;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONENUMBER = "phonenumber";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        name = sharedPreferences.getString(KEY_NAME, null);
        phoneNumber = sharedPreferences.getString(KEY_PHONENUMBER, null);
        String message = null;

        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //---------------------Integration Work-----------------------///


//

        latitudeTextView = findViewById(R.id.latTextView);
        longitTextView = findViewById(R.id.lonTextView);
        detailsTextView = findViewById(R.id.data);
        send = findViewById(R.id.send);
        cleardetails = findViewById(R.id.clear);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SendSMS();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        SendSMS();
                    }
                }

            }
        });
        // method to get the location
        getLastLocation();


        cleardetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
//==================================================================================================///
//==================================================================================================///

    @SuppressLint("MissingPermission")
    private void getLastLocation() {

        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitudeTextView.setText(location.getLatitude() + "");
                            longitTextView.setText(location.getLongitude() + "");
                            lats = location.getLatitude();
                            lgs  = location.getLongitude();
                            detailsTextView.setText("Details of the contact to be sent"+System.getProperty("line.separator")+"Name : " + name +System.lineSeparator() +"Number : " + phoneNumber);
                            mMap.setMyLocationEnabled(true);
                            LatLng loc = new LatLng(lats,lgs);
                            mMap.addMarker(new MarkerOptions().position(loc).title("New Marker"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));

                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    public void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
//////////.........................................................
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = (Location) locationResult.getLocations();
            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
            maplat = mLastLocation.getLatitude();
            maplong = mLastLocation.getLongitude();
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /////////////////////////////////////////////////////////////////
    private void SendSMS() {




        message = "" + name + " ALERT ! ALERT !  I am sending you my location to update that I am here Please check my location at latitude : " + latitudeTextView.getText() + "Longitude : " + longitTextView.getText();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Message Sent to Your " + "", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed To Send Location, kindly grant permission to the application of sending text from settings ", Toast.LENGTH_SHORT).show();
        }
    }


//==================================================================================================///
//==================================================================================================///
//==================================================================================================///

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);

        // Add a marker in Sydney and move the camera
        mMap.setMyLocationEnabled(true);
        LatLng loc = new LatLng(lats,lgs);
        mMap.addMarker(new MarkerOptions().position(loc).title("Your Location"+loc+"."));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
    }
////////////
    public void clearData()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_NAME).commit();
        editor.remove(KEY_PHONENUMBER).commit();
        editor.apply();
        Toast.makeText(this, "Contact Details are cleared", Toast.LENGTH_LONG).show();
    }
    ///////////////////
    }


