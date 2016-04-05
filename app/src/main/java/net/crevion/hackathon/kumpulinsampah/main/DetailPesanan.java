package net.crevion.hackathon.kumpulinsampah.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;
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

public class DetailPesanan extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener  {
    TextView coba;
    private ProgressDialog pDialog;
    private static final String url = "http://crevion.net/kumpulinsampah/public/index.php/sampah/get_detail_pesanan/";

    private static final String TAG = DetailPesanan.class.getSimpleName();
    TextView jenisSampah;
    TextView namaPembeli;
    TextView tanggal;
    TextView tanggal_pesan;
    TextView catatan;
    TextView harga;
    TextView waktupengambilan;
    EditText inputtgl;
    EditText inputjam;
    EditText inputket;
    Marker markerMarker;

    private GoogleMap mMap;
    Button btn_beli;
    ImageView iv;
    String SjenisSampah;
    String SidSampah;
    String SnamaPembeli;
    String Stanggal;
    String Stanggal_pesan;
    String Scatatan;
    String Sharga;
    String Slat;
    String Slon;
    String Sgambar;
    String Swaktupengambilan;
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
        setContentView(R.layout.detail_pesanan);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setTitle("Detail Pesanan");
        Intent intent = getIntent();

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Menunggu...");
        pDialog.show();
        MainActivity m = new MainActivity();
        idPembeli = m.idPembeli;

        // Creating volley request obj
        jenisSampah = (TextView) findViewById(R.id.jenisSampah);
        namaPembeli = (TextView) findViewById(R.id.namaPembeli);
        tanggal_pesan = (TextView) findViewById(R.id.tanggal_pesan);
        tanggal = (TextView) findViewById(R.id.tanggal_posting);
        harga = (TextView) findViewById(R.id.harga);
        waktupengambilan = (TextView) findViewById(R.id.pengambilan);
        catatan = (TextView) findViewById(R.id.catatan);
        iv = (ImageView) findViewById(R.id.detailImage);

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
                                SnamaPembeli = obj.getString("nama");
                                Stanggal_pesan = obj.getString("tanggal_pesan");
                                Stanggal = obj.getString("tanggal");
                                Sharga = obj.getString("harga");
                                Scatatan = obj.getString("keterangan");
                                Swaktupengambilan = obj.getString("tanggal_ambil")+" "+obj.getString("jam");
                                Sgambar = "http://crevion.net/kumpulinsampah/public/sampah/images/"+obj.getString("gambar");
                                //Swaktupengambilan = obj.getString("waktu_free");
                                jenisSampah.setText(SjenisSampah);
                                namaPembeli.setText(SnamaPembeli);
                                tanggal_pesan.setText(Stanggal_pesan);
                                tanggal.setText(Stanggal);
                                waktupengambilan.setText(Swaktupengambilan);
                                catatan.setText(Scatatan);
                                harga.setText(Sharga);
                                NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.detailImage);
                                thumbNail.setImageUrl(Sgambar, imageLoader);


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



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_daftar_sampah) {
            Intent myIntent = new Intent(DetailPesanan.this, MainActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(myIntent);
        } else if (id == R.id.nav_sampahku) {
            Intent myIntent = new Intent(DetailPesanan.this, Sampahku.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(myIntent);
        } else if (id == R.id.nav_pesanan) {
            Intent myIntent = new Intent(DetailPesanan.this, PesananSampah.class);
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
