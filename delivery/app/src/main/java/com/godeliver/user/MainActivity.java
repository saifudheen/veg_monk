package com.godeliver.user;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import com.godeliver.user.Config.BaseURL;
import com.godeliver.user.Fonts.CustomTypefaceSpan;
import com.godeliver.user.Fragment.Home;
import com.godeliver.user.Fragment.Order_history;
import com.godeliver.user.NetworkConnectivity.NoInternetConnection;
import com.godeliver.user.util.ConnectivityReceiver;
import com.godeliver.user.util.CustomVolleyJsonRequest;
import com.godeliver.user.util.Session_management;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener,

        ConnectivityReceiver.ConnectivityReceiverListener, LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView iv_profile;

    private Menu nav_menu;

    private TextView tv_name;

    ImageView imageView;

    TextView mTitle;

    Toolbar toolbar;

    TextView locationText;

    int padding = 0;

    private Bitmap bitmap;

    private Session_management sessionManagement;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Handler handler;
    double lng;
    double lat;
    TextView _latitude, _longitude;

    LocationManager locationManager;

    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences=getSharedPreferences("person",MODE_PRIVATE);

        editor=preferences.edit();

        userid=preferences.getString("userid","");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);

            if (view instanceof TextView) {
                TextView textView = (TextView) view;

                Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "Font/Bold.otf");
                textView.setTypeface(myCustomFont);
            }

        }

        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

//        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        Menu m = navigationView.getMenu();

        for (
                int i = 0; i < m.size(); i++)

        {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            applyFontToMenuItem(mi);
        }

        sessionManagement = new

                Session_management(MainActivity.this);
        View headerView = navigationView.getHeaderView(0);
        navigationView.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);
        navigationView.setNavigationItemSelectedListener(this);
        nav_menu = navigationView.getMenu();
        View header = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        iv_profile = (ImageView) header.findViewById(R.id.iv_header_img);

        tv_name = (TextView) header.findViewById(R.id.tv_header_name);
        updateHeader();
        sideMenu();



        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }




        handler = new Handler();

        Runnable mStatusChecker = new Runnable() {
            @Override
            public void run() {
                try {

                    getLocation();
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
                handler.postDelayed(this, 3000);


            }
//                            handler.postDelayed(mStatusChecker, 3000);

        };

        mStatusChecker.run();


        if (savedInstanceState == null) {
            Fragment fm = new Home();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.contentPanel, fm, "Home_fragment")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
        getFragmentManager().
                addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        try {

                            InputMethodManager inputMethodManager = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            Fragment fr = getFragmentManager().findFragmentById(R.id.contentPanel);
                            final String fm_name = fr.getClass().getSimpleName();
                            Log.e("backstack: ", ": " + fm_name);
                            if (fm_name.contentEquals("Home_fragment")) {
                                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                                toggle.setDrawerIndicatorEnabled(true);
                                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                                toggle.syncState();

                            } else if (fm_name.contentEquals("My_order_fragment") ||
                                    fm_name.contentEquals("Thanks_fragment")) {
                                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                                toggle.setDrawerIndicatorEnabled(false);
                                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                toggle.syncState();
                                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Fragment fm = new Home();
                                        FragmentManager fragmentManager = getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                                .addToBackStack(null).commit();
                                    }
                                });
                            } else {
                                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                                toggle.setDrawerIndicatorEnabled(false);
                                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                toggle.syncState();
                                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        onBackPressed();
                                    }
                                });
                            }

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    public void updateHeader() {
        if (sessionManagement.isLoggedIn()) {
            String getname = sessionManagement.getUserDetails().get(BaseURL.KEY_NAME);
            tv_name.setText(getname);
        }
    }


    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "Font/Bold.otf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }


    public void sideMenu() {
        if (sessionManagement.isLoggedIn()) {
            nav_menu.findItem(R.id.nav_log_out).setVisible(true);
        } else {
//            tv_name.setText(getResources().getString(R.string.btn_login));
//            tv_name.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(MainActivity.this, LogInActivity.class);
//                    startActivity(i);
//                }
//            });
            nav_menu.findItem(R.id.nav_log_out).setVisible(false);

        }
    }


    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fm = null;
        Bundle args = new Bundle();
        if (id == R.id.nav_order) {
            Fragment fma = new Home();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.contentPanel, fma, "Home_fragment")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

        }else  if (id==R.id.nav_order_history){
            Fragment fma = new Order_history();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.contentPanel, fma, "Order_History")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

        }
        else if (id == R.id.nav_log_out) {
            sessionManagement.logoutSession();
            finish();
        }
        if (fm != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                    .addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {

        if (!isConnected) {
            Intent intent = new Intent(MainActivity.this, NoInternetConnection.class);
            startActivity(intent);
        }
    }




    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5,  this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }






    private  void setapiloction(final String lat, final String lng){

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("lat",lat);
//
//
//            jsonObject.put("lng", lng);
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        Map<String, String> params = new HashMap<>();
        params.put("lat", lat);
        params.put("user_id",userid);
        params.put("lng",lng);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST, BaseURL.Loction, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("dsd",response.toString());

                Log.d("latas", String.valueOf(lat+lng));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("dfs","asdfasdf");
            }
        });


        queue.add(jsonObjReq);

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d("sd",location.toString());
        //        locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());

//        Toast.makeText(this, ""+location.getLatitude()+location.getLongitude(), Toast.LENGTH_SHORT).show();

        lat= location.getLatitude();
        lng= location.getLongitude();




        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            Log.i("asd", String.valueOf(lat+lng));

            setapiloction(String.valueOf(lat),String.valueOf(lng));

//            locationText.setText(locationText.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+
//                    addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));
        }catch(Exception e)
        {

        }

    }

    //    @Override
//    public void onStatusChanged(String s, int i, Bundle bundle) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String s) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String s) {
//
//    }
//
    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }



}
