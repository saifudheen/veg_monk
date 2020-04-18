package com.tecmanic.storemanager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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

import com.bumptech.glide.Glide;
import com.tecmanic.storemanager.Config.BaseURL;
import com.tecmanic.storemanager.Dashboard.AllProducts;
//import com.tecmanic.storemanager.Dashboard.AppUserActivity;
import com.tecmanic.storemanager.Dashboard.EditProfile;
import com.tecmanic.storemanager.Dashboard.LoginActivity;
import com.tecmanic.storemanager.Dashboard.OrdersActivity;
import com.tecmanic.storemanager.Dashboard.StockList;
import com.tecmanic.storemanager.Dashboard.StockUpdate;
import com.tecmanic.storemanager.Fonts.CustomTypefaceSpan;
import com.tecmanic.storemanager.Fragments.Home_fragment;
import com.tecmanic.storemanager.NetworkConnectivity.NoInternetConnection;
import com.tecmanic.storemanager.util.ConnectivityReceiver;
import com.tecmanic.storemanager.util.Session_management;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView tv_name;
    private ImageView iv_profile;
    private Menu nav_menu;
    ImageView imageView;
    Toolbar toolbar;
    int padding = 0;
    private Session_management sessionManagement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setPadding(padding, toolbar.getPaddingTop(), padding, toolbar.getPaddingBottom());


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


        sessionManagement = new

                Session_management(MainActivity.this);
//        //Navigation Menu

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        View headerView = navigationView.getHeaderView(0);
        navigationView.getBackground().

                setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);
        navigationView.setNavigationItemSelectedListener(this);
        nav_menu = navigationView.getMenu();

        View header = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        iv_profile = (ImageView) header.findViewById(R.id.iv_header_img);
        tv_name = (TextView) header.findViewById(R.id.tv_header_name);
        updateHeader();
        sideMenu();

        if (savedInstanceState == null)

        {
            Fragment fm = new Home_fragment();
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
                                        Fragment fm = new Home_fragment();
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
            String getimage = sessionManagement.getUserDetails().get(BaseURL.KEY_IMAGE);
            Glide.with(this)
                    .load(BaseURL.IMG_PROFILE_URL + getimage)
                    .placeholder(R.drawable.icon)
                    .crossFade()
                    .into(iv_profile);
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
            nav_menu.findItem(R.id.nav_logout).setVisible(true);
        } else {
            tv_name.setText(getResources().getString(R.string.btn_login));
            tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            });
            nav_menu.findItem(R.id.nav_logout).setVisible(false);

        }
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fm = null;
        Bundle args = new Bundle();
        if (id == R.id.nav_dashboard) {
            Fragment fm_home = new Home_fragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.contentPanel, fm_home, "Home_fragment")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
//        } else if (id == R.id.nav_app_user) {
//            Intent intent = new Intent(MainActivity.this, AppUserActivity.class);
//            startActivity(intent);
        } else if (id == R.id.nav_edit_profile) {
            Intent intent = new Intent(MainActivity.this, EditProfile.class);
            startActivity(intent);
        } else if (id == R.id.nav_product_list) {
            Intent intent = new Intent(MainActivity.this, AllProducts.class);
            startActivity(intent);
        } else if (id == R.id.nav_stock_update) {
            Intent intent = new Intent(MainActivity.this, StockUpdate.class);
            startActivity(intent);
        } else if (id == R.id.nav_order) {
            Intent intent = new Intent(MainActivity.this, OrdersActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_stock) {
            Intent intent = new Intent(MainActivity.this, StockList.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
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
    public void startActivityForResult(Intent intent, int requestCode) {
        try {
            super.startActivityForResult(intent, requestCode);
        } catch (Exception ignored) {
        }

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



}
