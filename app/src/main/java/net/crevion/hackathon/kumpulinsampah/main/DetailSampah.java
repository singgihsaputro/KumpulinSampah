package net.crevion.hackathon.kumpulinsampah.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;
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
import net.crevion.hackathon.kumpulinsampah.model.SampahModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.AccessController;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailSampah extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    TextView coba;
    private ProgressDialog pDialog;
    private static final String url = "http://crevion.net/kumpulinsampah/public/index.php/sampah/get_detail_sampah/";
    private static final String url_pesan = "http://crevion.net/kumpulinsampah/public/index.php/sampah/beli_user";
    private static final String url_hapus = "http://crevion.net/kumpulinsampah/public/index.php/sampah/hapus_sampah";
    private static final String url_terjual = "http://crevion.net/kumpulinsampah/public/index.php/sampah/set_terjual";
    private static final String TAG = DetailSampah.class.getSimpleName();
    TextView jenisSampah;
    TextView namaPemilik;
    TextView tanggal;
    TextView alamat;
    TextView ket;
    TextView harga;
    TextView status;
    EditText inputtgl;
    EditText inputjam;
    EditText inputket;
    Marker markerMarker;

    private GoogleMap mMap;
    Button btn_beli;
    ImageView iv;
    String SjenisSampah;
    String SidSampah;
    String SnamaPemilik;
    String Stanggal;
    String Salamat;
    String Sket;
    String Sharga;
    String Slat;
    String Slon;
    String Sgambar;
    String Swaktufree;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    String tanggal_input;
    String jam_input;
    String keterangan_input;
    String idPembeli;
    private Context ctx;
    AlertDialog.Builder builder;
    LayoutInflater l;
    Button btn_hapus;
    Button btn_terjual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_sampah);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setTitle("Detail Sampah");
        final Intent intent = getIntent();
        btn_hapus = (Button) findViewById(R.id.btn_hapus);
        btn_terjual = (Button) findViewById(R.id.btn_terjual);
        btn_beli = (Button) findViewById(R.id.btn_beli);
        if(intent.getStringExtra("from").equalsIgnoreCase("main")){
            btn_hapus.setVisibility(View.GONE);
            btn_terjual.setVisibility(View.GONE);
        }else{
            btn_beli.setVisibility(View.GONE);
            btn_hapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hapus_data();
                }
            });
            btn_terjual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    terjual_data();
                }
            });
        }
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Menunggu...");
        pDialog.show();
        MainActivity m = new MainActivity();
        idPembeli = m.idPembeli;

        // Creating volley request obj
        jenisSampah = (TextView) findViewById(R.id.jenisSampah);
        namaPemilik = (TextView) findViewById(R.id.namaPemilik);
        alamat = (TextView) findViewById(R.id.alamat);
        tanggal = (TextView) findViewById(R.id.tanggal);
        harga = (TextView) findViewById(R.id.harga);
        ket = (TextView) findViewById(R.id.ket);
        iv = (ImageView) findViewById(R.id.detailImage);
        status = (TextView) findViewById(R.id.status);
        JsonArrayRequest getDataSampah = new JsonArrayRequest(url+intent.getStringExtra("idSampah"),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json

                            try {

                                JSONObject obj = response.getJSONObject(0);
                                SidSampah = obj.getString("id_sampah");
                                SjenisSampah = obj.getString("jenis_sampah");
                                SnamaPemilik = obj.getString("nama");
                                Salamat = obj.getString("alamat");
                                Stanggal = obj.getString("tanggal");
                                Sharga = obj.getString("harga");
                                Sket = obj.getString("keterangan");
                                Slat = obj.getString("lat");
                                Slon = obj.getString("lon");
                                Sgambar = "http://crevion.net/kumpulinsampah/public/sampah/images/"+obj.getString("gambar");
                                Swaktufree = obj.getString("waktu_free");
                                jenisSampah.setText(SjenisSampah);
                                namaPemilik.setText(SnamaPemilik);
                                alamat.setText(Salamat);
                                tanggal.setText(Stanggal);
                                ket.setText(Sket);
                                harga.setText(Sharga);
                                if(intent.getStringExtra("from").equalsIgnoreCase("main")){
                                    status.setVisibility(View.GONE);
                                }else{
                                    if(obj.getString("status").equalsIgnoreCase("1")) {
                                        status.setText("Tersedia");
                                    }else{
                                        status.setText("Terjual");
                                    }
                                }
                                NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.detailImage);
                                thumbNail.setImageUrl(Sgambar, imageLoader);
                                setMap(Double.parseDouble(Slat), Double.parseDouble(Slon), Salamat);
                                builder = new AlertDialog.Builder(DetailSampah.this);
                                builder.setTitle("Apakah anda yakin ingin membeli ?");
//                                final EditText inputtgl = new EditText(DetailSampah.this);
//                                final EditText inputjam = new EditText(DetailSampah.this);
//                                final EditText inputket = new EditText(DetailSampah.this);

                                //LayoutInflater inflater = getLayoutInflater();
//                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                                        LinearLayout.LayoutParams.MATCH_PARENT,
//                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                
//                                inputtgl.setLayoutParams(lp);
//                                inputjam.setLayoutParams(lp);
//                                inputket.setLayoutParams(lp);
//                                builder.setView(inputtgl);
//                                builder.setView(inputjam);
//                                builder.setView(inputket);
                                //builder.setView(inflater.inflate(R.layout.custom_dialog, null));
                                l = LayoutInflater.from(getBaseContext());
                                LinearLayout textEntryView = (LinearLayout) l.inflate(R.layout.custom_dialog, null);
                                inputtgl = (EditText) textEntryView.findViewById(R.id.editText);
                                inputjam = (EditText) textEntryView.findViewById(R.id.editText2);
                                inputket = (EditText) textEntryView.findViewById(R.id.editText3);

                                builder.setView(textEntryView);
                                builder.setMessage("Jadwal pembelian :\n" + Swaktufree);
                                builder.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                tanggal_input = inputtgl.getText().toString();
                                                jam_input = inputjam.getText().toString();
                                                keterangan_input = inputket.getText().toString();

                                                postRequest();
                                            }
                                        });
                                builder.setNegativeButton("Batal", null);

                                btn_beli.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        builder.show();
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(getDataSampah);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    public void postRequest(){

        //Log.d("CobaForm",m.idPembeli+" - "+SjenisSampah+" - "+tanggal_input+" - "+jam_input+" - "+keterangan_input);
        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest postReq = new StringRequest(Request.Method.POST, url_pesan, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Pesanan berhasil dikirim", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Pesanan gagal dikirim", Toast.LENGTH_LONG).show();

            }
        }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("id_pembeli", idPembeli);
                params.put("id_sampah", SidSampah);
                params.put("tanggal", tanggal_input);
                params.put("jam", jam_input);
                params.put("keterangan", keterangan_input);

                return params;
            }

        };
        rq.add(postReq);
    }
    public void terjual_data(){

        //Log.d("CobaForm",m.idPembeli+" - "+SjenisSampah+" - "+tanggal_input+" - "+jam_input+" - "+keterangan_input);
        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest postReq = new StringRequest(Request.Method.POST, url_terjual, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Status berhasil diubah", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Status gagal diubah", Toast.LENGTH_LONG).show();

            }
        }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("id_user", idPembeli);
                params.put("id_sampah", SidSampah);


                return params;
            }

        };
        rq.add(postReq);
    }
    public void hapus_data(){

        //Log.d("CobaForm",m.idPembeli+" - "+SjenisSampah+" - "+tanggal_input+" - "+jam_input+" - "+keterangan_input);
        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest postReq = new StringRequest(Request.Method.POST, url_hapus, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Postingan berhasil dihapus", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(DetailSampah.this, Sampahku.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(myIntent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Postingan gagal dihapus", Toast.LENGTH_LONG).show();

            }
        }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("id_user", idPembeli);
                params.put("id_sampah", SidSampah);


                return params;
            }

        };
        rq.add(postReq);
    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }
    public void setMap(final double lat, final double lon, final String al) {
        LatLng sydney = new LatLng(lat, lon);
        markerMarker = mMap.addMarker(new MarkerOptions().position(sydney).title("Lokasi penjual").draggable(false));
//        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//                Intent intent = new Intent(DetailSampah.this, MapsActivity.class);
//                startActivityForResult(intent, 5050);
//            }
//        });
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f, %f(%s)", lat, lon, lat, lon, al);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_daftar_sampah) {
            Intent myIntent = new Intent(DetailSampah.this, MainActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(myIntent);
        } else if (id == R.id.nav_sampahku) {
            Intent myIntent = new Intent(DetailSampah.this, Sampahku.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(myIntent);
        } else if (id == R.id.nav_pesanan) {
            Intent myIntent = new Intent(DetailSampah.this, PesananSampah.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(myIntent);
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
