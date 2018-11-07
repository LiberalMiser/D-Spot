package com.codedarts.joel.dspot;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.codedarts.joel.dspot.Utilities.FetchUrl;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private long locationUpdateIntervals = 1000;
    private long fastestLocationUpdateIntervals = 1000;

    public static String preferenceName = "com.example.myser.dspotalpha";
    public static String SELECTED_MAP_TYPE = "SELECTED_MAP_TYPE";
    private static final int LOCATION_PERMISSION_CODE = 0;
    private boolean isPermissionGranted = false;
    private ArrayList<LatLng> markerPoints;
    private SharedPreferences sharedPreferences;
    private int savedMapType;
    private int mapUpdateCount = 0;

    private SearchView searchView;

    private GoogleMap mMap;
    private LatLng latLng;
    private Location location;
    private GoogleApiClient googleApiClient;
    private Marker currLocationMarker;
    private LocationRequest locationRequest;

    private Intent intent;
    private LatLng destination;

    private SearchView.OnQueryTextListener onQueryListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        preferenceName = getPackageName();
        searchView = (SearchView) findViewById(R.id.searchView);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        mapFragment.getMapAsync(this);

        sharedPreferences = getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        savedMapType = sharedPreferences.getInt(SELECTED_MAP_TYPE, 0);
        markerPoints = new ArrayList<>();

        initializeLocationUpdateRequest();

        intent = getIntent();
        destination = new LatLng(intent.getDoubleExtra(DestinationActivity.LATITUDE, 0), intent.getDoubleExtra(DestinationActivity.LONGITUDE, 0));

        onQueryListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        };

        searchView.setOnQueryTextListener(onQueryListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        if (googleApiClient.isConnected()) {
            requestLocationUpdates();
            Toast.makeText(this, "onStart(): Connected", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "onStart(): Not connected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isPermissionGranted) {
            googleApiClient.disconnect();
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            Toast.makeText(this, "onStop(): Permission Granted", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "onStop(): Permission Not Granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isPermissionGranted) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPermissionGranted) {
            if (googleApiClient.isConnected()) {
                requestLocationUpdates();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void initializeLocationUpdateRequest () {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();

        locationRequest.setInterval(locationUpdateIntervals);
        locationRequest.setFastestInterval(fastestLocationUpdateIntervals);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


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

        setMapType();
        if (location != null) {
            //region older setup
            latLng = new LatLng(location.getLatitude(), location.getLongitude());

            markerPoints.add(latLng);

            mMap.addMarker(new MarkerOptions().position(latLng).title("You're here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                }
                return;
            }
            else {
                mMap.setMyLocationEnabled(true);
            }
            //endregion

            //region OnClickListener for Marker Adding
            /*mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {
                    // Already two locations
                    if (markerPoints.size() > 1) {
                        markerPoints.clear();
                        mMap.clear();
                    }

                    // Adding new item to the ArrayList
                    markerPoints.add(point);

                    // Creating MarkerOptions
                    MarkerOptions options = new MarkerOptions();

                    // Setting the position of the marker
                    options.position(point);

                     //* For the start location, the color of marker is GREEN and
                     //* for the end location, the color of marker is RED.
                    if (markerPoints.size() == 1) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    } else if (markerPoints.size() == 2) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }

                    // Add new marker to the Google Map Android API V2
                    mMap.addMarker(options);
                    // Checks, whether start and end locations are captured
                    if (markerPoints.size() >= 2) {
                        LatLng origin = markerPoints.get(0);
                        LatLng dest = markerPoints.get(1);
                        // Getting URL to the Google Directions API
                        String url = getUrl(origin, dest);
                        Log.d("onMapClick", url.toString());
                        FetchUrl FetchUrl = new FetchUrl(mMap);

                        // Start downloading json data from Google Directions API
                        FetchUrl.execute(url);
                        //move map camera
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 16.0f));
                        //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    }
                }
            });*/
            //endregion
            setDestinationMarker(destination);
            setMapType();
        }
    }

    private void setDestinationMarker (LatLng _destination) {
        markerPoints.add(_destination);

        MarkerOptions options = new MarkerOptions();

        options.position(_destination);

        if (markerPoints.size() == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else if (markerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        mMap.addMarker(options);

        if (markerPoints.size() >= 2) {
            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);
            // Getting URL to the Google Directions API
            String url = getUrl(origin, dest);
            Log.d("onMapClick", url.toString());
            FetchUrl FetchUrl = new FetchUrl(mMap);

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 16.0f));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }
    }

    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    public void changeMapType (View view) {
        savedMapType++;
        if (savedMapType >= 5) {
            savedMapType = 0;
        }

        setMapType();
        //Toast.makeText(this, String.valueOf(mMap.getMapType()), Toast.LENGTH_SHORT).show();
        saveSelectedMapType();
    }

    private void setMapType () {
        switch (savedMapType) {
            case 0:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case 3:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case 4:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
        }
    }

    private void saveSelectedMapType () {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(SELECTED_MAP_TYPE, savedMapType);
        editor.apply();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            }
            else {
                isPermissionGranted = true;
            }
            return;
        }
        Toast.makeText(this, "Location permissions granted.", Toast.LENGTH_SHORT).show();
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        //LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, googleApiClient, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted = true;
                }
                else {
                    isPermissionGranted = false;
                    Toast.makeText(this, "Location is required for this application to function properly", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void search (/*View view*/) {
        String location = searchView.getQuery().toString();

        if (TextUtils.isEmpty(location)) {
            Toast.makeText(this, "Search!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection suspended.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        mapUpdateCount++;

        if (mapUpdateCount <= 1) {
            onMapReady(mMap);
        }
    }
}
