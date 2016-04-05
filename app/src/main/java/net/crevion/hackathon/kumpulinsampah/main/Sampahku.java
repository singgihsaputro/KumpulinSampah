package net.crevion.hackathon.kumpulinsampah.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import net.crevion.hackathon.kumpulinsampah.R;
import net.crevion.hackathon.kumpulinsampah.adapter.CustomListAdapter;
import net.crevion.hackathon.kumpulinsampah.model.SampahModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Sampahku extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // Log tag
    private static final String TAG = PesananSampah.class.getSimpleName();
    static final String idPembeli = "201503002";
    // Billionaires json url
    private static final String url = "http://crevion.net/kumpulinsampah/public/index.php/sampah/list_sampahku/"+idPembeli;
    private ProgressDialog pDialog;
    private ArrayList<SampahModel> sampahModelList = new ArrayList<>();
    private ListView listView;
    private CustomListAdapter adapter;
    private Toolbar toolbar;
    private Menu menu;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSearch;
    private ArrayList<SampahModel> a;
    private ArrayList<SampahModel> data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sampahku);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sampahku");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        listView = (ListView) findViewById(R.id.listView);
        adapter = new CustomListAdapter(this, sampahModelList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(Sampahku.this, DetailSampah.class);
                SampahModel model = (SampahModel) parent.getAdapter().getItem(position);
                myIntent.putExtra("idSampah", model.getIdSampah());
                myIntent.putExtra("from", "sampahku");
                startActivity(myIntent);
            }
        });

        getNewSampah();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void getNewSampah(){
        sampahModelList.clear();
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Menunggu...");
        pDialog.show();
        // Creating volley request obj
        JsonArrayRequest getDataSampah = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                SampahModel sampahModel = new SampahModel();
                                sampahModel.setSampah(obj.getString("jenis_sampah"));
                                sampahModel.setThumbnailUrl("http://crevion.net/kumpulinsampah/public/sampah/images/" + obj.getString("gambar"));
                                if(obj.getString("status").equals("1")) {
                                    sampahModel.setNama("Tersedia");
                                }else{
                                    sampahModel.setNama("Terjual");
                                }
                                sampahModel.setTanggal(obj.getString("tanggal"));
                                sampahModel.setIdSampah(obj.getString("id_sampah"));

                                // adding Billionaire to sampahModel array
                                sampahModelList.add(sampahModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        a = sampahModelList;
                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
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
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        if (isSearchOpened) {
            handleMenuSearch();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            handleMenuSearch();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_daftar_sampah) {
            Intent myIntent = new Intent(Sampahku.this, MainActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(myIntent);
        } else if (id == R.id.nav_sampahku) {

        } else if (id == R.id.nav_pesanan) {
            Intent myIntent = new Intent(Sampahku.this, PesananSampah.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(myIntent);
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    protected void handleMenuSearch() {
        ActionBar action = getSupportActionBar(); //get the actionbar

        if (isSearchOpened) { //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_menu_search));

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSearch = (EditText) action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    adapter.getFilter().filter(s.toString());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }


            });


            edtSearch.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_menu_search_close));

            isSearchOpened = true;
        }
    }
//    public void searchItem(String textFind){
//        ArrayList<SampahModel> filters = new ArrayList<SampahModel>();
//
//        Log.d("Cobacoba", "size world = " + sampahModelList.size());
//        for (int i =0; i<a.size();i++) {
//            Log.d("Cobacoba", "size a = " + a.size());
//            SampahModel getObject = a.get(i);
//            Log.d("Cobacoba2", "catatan = " + getObject.getSampah());
//            if (getObject.getSampah().toLowerCase().contains(textFind.toLowerCase())) {
//                Log.d("Cobacoba3", "catatan = " + getObject.getSampah());
//                SampahModel w = new SampahModel();
//                w.getSampah();
//                w.getThumbnailUrl();
//                w.getNama();
//                w.getTanggal();
//                w.getIdSampah();
//                filters.add(w);
//            }
//            ;
//        }
//
//        sampahModelList = filters;
//        adapter.notifyDataSetChanged();
//    }
}
