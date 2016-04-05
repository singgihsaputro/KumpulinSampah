package net.crevion.hackathon.kumpulinsampah.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.crevion.hackathon.kumpulinsampah.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerDragListener {
    private GoogleMap mMap;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    // Declaring a Location Manager
    protected LocationManager locationManager;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    String alamat;

    Geocoder geocoder;
    List<Address> addresses;
    Location loc;

    Button pilihBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps);

        pilihBtn = (Button) findViewById(R.id.pilihBtn);
        pilihBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                Bundle bundle = new Bundle();
                Log.d("latitude", ""+latitude);
                Log.d("longitude", ""+longitude);
                bundle.putDouble("latitude", latitude);
                bundle.putDouble("longitude", longitude);
                returnIntent.putExtra("alamat", alamat);
                returnIntent.putExtras(bundle);
//                returnIntent.putExtra("latitude", String.valueOf(latitude));
//                returnIntent.putExtra("longitude", String.valueOf(longitude));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
//        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        // getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSEnabled || !isNetworkEnabled) {
            // no network provider is enabled
            Context context = getApplicationContext();
            CharSequence text = "GPS or NETWORK DISABLED";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
//            if (isGPSEnabled) {
            if (location == null) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Context context = getApplicationContext();
                CharSequence text = "GPS run";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
//            }
//            else if (isNetworkEnabled) {
//
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
//                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//                locationManager.requestLocationUpdates(
//                        LocationManager.GPS_PROVIDER,
//                        MIN_TIME_BW_UPDATES,
//                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//                Context context = getApplicationContext();
//                CharSequence text = "Network run";
//                int duration = Toast.LENGTH_LONG;
//
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();
//                if (locationManager != null) {
//                    location = locationManager
//                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                    if (location != null) {
//                        latitude = location.getLatitude();
//                        longitude = location.getLongitude();
//                    }
//                }
//            }
        }

//        double latitude = location.getLatitude();
//        double longitude = location.getLongitude();
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);

        geocoder = new Geocoder(MapsActivity.this, Locale.ENGLISH);
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            StringBuilder str = new StringBuilder();
            if (geocoder.isPresent()) {
                Toast.makeText(getApplicationContext(),
                        "geocoder present", Toast.LENGTH_SHORT).show();
                Address returnAddress = addresses.get(0);
                String street = returnAddress.getAddressLine(0);
                String localityString = returnAddress.getLocality();
                String city = returnAddress.getCountryName();
                String region_code = returnAddress.getCountryCode();
                String zipcode = returnAddress.getPostalCode();

                str.append(street + " - ");
                str.append(localityString + " - ");
                str.append(city + "" + region_code + " - ");
                str.append(zipcode + "");

                Toast.makeText(getApplicationContext(),
                        str, Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), str,
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(),
                        "geocoder not present", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block

            Log.e("tag", e.getMessage());
        }

        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Lokasi Anda").draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
        mMap.setOnMarkerDragListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng pos = marker.getPosition();
        Context context = getApplicationContext();
        latitude = pos.latitude;
        longitude = pos.longitude;
        CharSequence text = "Latitude : "+pos.latitude+" Longitude : "+pos.longitude;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        geocoder = new Geocoder(MapsActivity.this, Locale.ENGLISH);
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            StringBuilder str = new StringBuilder();
            if (geocoder.isPresent()) {
                Toast.makeText(getApplicationContext(),
                        "geocoder present", Toast.LENGTH_SHORT).show();
                Address returnAddress = addresses.get(0);
                String street = returnAddress.getAddressLine(0);
                String localityString = returnAddress.getLocality();
                String city = returnAddress.getCountryName();
                String region_code = returnAddress.getCountryCode();
                String zipcode = returnAddress.getPostalCode();

                str.append(street + " - ");
                str.append(localityString + " - ");
                str.append(city + "" + region_code + " - ");
                str.append(zipcode + "");
                alamat = str.toString();

                Toast.makeText(getApplicationContext(),
                        str, Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), str,
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(),
                        "geocoder not present", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block

            Log.e("tag", e.getMessage());
        }
        }
    @Override
    public void onBackPressed() {

            super.onBackPressed();


    }
}
