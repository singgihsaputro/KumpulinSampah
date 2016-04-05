package net.crevion.hackathon.kumpulinsampah.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.crevion.hackathon.kumpulinsampah.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TambahActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerDragListener {
    private Bitmap bitmap;
    private String encoded_string, image_name;


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

    String alamat;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    Geocoder geocoder;
    List<Address> addresses;
    Location loc;

    EditText alamatEdit, latitudeEdit, longitudeEdit, keteranganEdit;

    Marker markerMarker;
    Spinner spinner;
    String jenisNama;
    String count;
    String idPembeli;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        MainActivity m = new MainActivity();
        idPembeli=m.idPembeli;
        Button buttonKirim = (Button) findViewById(R.id.kirimBtn);
        alamatEdit = (EditText) findViewById(R.id.alamatEdit);
        latitudeEdit = (EditText) findViewById(R.id.latitudeEdit);
        longitudeEdit = (EditText) findViewById(R.id.longitudeEdit);
        keteranganEdit = (EditText) findViewById(R.id.keteranganEdit);

        buttonKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new encodeImage().execute();

            }
        });

        Intent intent = getIntent();
        image_name = intent.getStringExtra("filepath");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8; // down sizing image as it throws OutOfMemory Exception for larger images
        File imgFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
                File.separator + image_name);
        if (imgFile.exists()) {
            ImageView imageView = (ImageView) findViewById(R.id.fotoView);
            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
            imageView.setImageBitmap(bitmap);
        }
        new JSONTask().execute("http://crevion.net/kumpulinsampah/public/index.php/sampah/get_jenis_sampah");

        spinner = (Spinner) findViewById(R.id.spinnerOk);

        final String values[] = getResources().getStringArray(R.array.values);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                jenisNama = parent.getItemAtPosition(position).toString();
                count = values[position]; //this would give you the id of the selected item
                Log.d("idi", "" + count);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

    }

    public class JSONTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                JSONArray parentArray = new JSONArray(finalJson);
                StringBuffer finalBufferedReader = new StringBuffer();
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    String movieName = finalObject.getString("jenis_sampah");
                    Log.d("jenis", movieName);
                }
//                ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, );
//                spinner.setAdapter();
                return finalBufferedReader.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            tvData.setText(result);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5050) {
            Bundle bundle = data.getExtras();
            latitude = bundle.getDouble("latitude");
            longitude = bundle.getDouble("longitude");
            alamat = data.getStringExtra("alamat");
            latitudeEdit.setText("" + latitude);
            longitudeEdit.setText("" + longitude);
            alamatEdit.setText(alamat);
            markerMarker.setPosition(new LatLng(latitude, longitude));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
        }
    }

    private class encodeImage extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
//            bitmap = BitmapFactory.decodeFile(filepath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, 0);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            makeRequest();
        }
    }

    private void makeRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://crevion.net/kumpulinsampah/public/index.php/sampah/add_sampah",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        if(response == "success"){
                        Intent intent = new Intent(TambahActivity.this, MainActivity.class);
                        Toast.makeText(TambahActivity.this, "Tambah Sampah Berhasil",
                                Toast.LENGTH_LONG).show();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", "ok");
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
//                            startActivityForResult(intent,100);
//                        }
                        Log.d("myTag", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                map.put("encode_string", encoded_string);
                map.put("image_name", image_name);
                map.put("gambar", image_name);
                map.put("lat", "" + latitude);
                map.put("lon", "" + longitude);
                map.put("id_user",idPembeli);
                map.put("keterangan", keteranganEdit.getText().toString());
                map.put("alamat", alamatEdit.getText().toString());
                map.put("jenis", count);
//                map.


                String text = spinner.getSelectedItem().toString();
                Log.d("spiner", "" + count);

                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

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

                        latitudeEdit.setText("" + latitude);
                        longitudeEdit.setText("" + longitude);
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

        geocoder = new Geocoder(TambahActivity.this, Locale.ENGLISH);
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            StringBuilder str = new StringBuilder();
            if (geocoder.isPresent()) {
//                Toast.makeText(getApplicationContext(),
//                        "geocoder present", Toast.LENGTH_SHORT).show();
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

                alamatEdit.setText("" + str.toString());
//                Toast.makeText(getApplicationContext(),
//                        str, Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(), str,
//                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(),
                        "geocoder not present", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block

            Log.e("tag", e.getMessage());
        }

        setMap(latitude, longitude);
    }

    public void setMap(double lat, double lon) {
        LatLng sydney = new LatLng(lat, lon);
        markerMarker = mMap.addMarker(new MarkerOptions().position(sydney).title("Lokasi Anda, Silahkan Klik Disini untuk mengganti").draggable(true));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(TambahActivity.this, MapsActivity.class);
                startActivityForResult(intent, 5050);
            }
        });
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
        CharSequence text = "Latitude : " + pos.latitude + " Longitude : " + pos.longitude;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();
    }
}
