package com.tecmanic.storemanager.Dashboard;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tecmanic.storemanager.Adapter.Orders_Adapter;
import com.tecmanic.storemanager.AppController;
import com.tecmanic.storemanager.Config.BaseURL;
import com.tecmanic.storemanager.MainActivity;
import com.tecmanic.storemanager.Model.OrdersModel;
import com.tecmanic.storemanager.R;
import com.tecmanic.storemanager.util.ConnectivityReceiver;
import com.tecmanic.storemanager.util.CustomVolleyJsonArrayRequest;
import com.tecmanic.storemanager.util.Session_management;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdersActivity extends AppCompatActivity implements Orders_Adapter.OrderAdpaterListener {
    private static String TAG = OrdersActivity.class.getSimpleName();
    private RecyclerView rv_items;
    private Session_management sessionManagement;
    String getuserid;
    private List<OrdersModel> movieList;
    private Orders_Adapter my_today_order_adapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        sessionManagement = new Session_management(getApplicationContext());

        getuserid =sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Orders");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrdersActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        rv_items = (RecyclerView) findViewById(R.id.rv_orderes);
        rv_items.setLayoutManager(new LinearLayoutManager(this));
        if (ConnectivityReceiver.isConnected()) {
            get_Today_Orders();
        } else {
            Toast.makeText(getApplicationContext(), "Network Issue", Toast.LENGTH_SHORT).show();
        }


    }

    private void get_Today_Orders() {

        final String tag_json_obj = "json_login_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", getuserid);

//        try {
//            RequestQueue rq = Volley.newRequestQueue(this);
        final CustomVolleyJsonArrayRequest postReq = new CustomVolleyJsonArrayRequest(Request.Method.POST,  BaseURL.My_orders, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                final List<OrdersModel> data = new ArrayList<>();
                Log.i("eclipse", "Response=" + response);
                try {
                    for (int i=0;i<response.length();i++) {

//                    }
                        JSONObject object = response.getJSONObject(i);
//                                JSONArray Jarray = object.getJSONArray("today_orders");
//                                for (int i = 0; i < Jarray.length(); i++) {
//                                    JSONObject json_data = Jarray.getJSONObject(i);
                        OrdersModel brandModel = new OrdersModel();
                        brandModel.sale_id = object.getString("sale_id");
                        brandModel.user_id = object.getString("user_id");
                        brandModel.on_date = object.getString("on_date");
                        brandModel.delivery_time_from = object.getString("delivery_time_from");
                        brandModel.delivery_time_to = object.getString("delivery_time_to");
                        brandModel.status = object.getString("status");
                        brandModel.note = object.getString("note");
                        brandModel.is_paid = object.getString("is_paid");
                        brandModel.total_amount = object.getString("total_amount");
                        brandModel.total_rewards = object.getString("total_rewards");
                        brandModel.total_kg = object.getString("total_kg");
                        brandModel.total_items = object.getString("total_items");
                        brandModel.socity_id = object.getString("socity_id");
                        brandModel.delivery_address = object.getString("delivery_address");
                        brandModel.location_id = object.getString("location_id");
                        brandModel.delivery_charge = object.getString("delivery_charge");
                        brandModel.new_store_id = object.getString("new_store_id");
                        brandModel.assign_to = object.getString("assign_to");
                        brandModel.payment_method = object.getString("payment_method");

                        data.add(brandModel);

                        movieList = new ArrayList<>();
                        my_today_order_adapter = new Orders_Adapter(OrdersActivity.this, data);
                        rv_items.setAdapter(my_today_order_adapter);
                        rv_items.refreshDrawableState();
                        rv_items.smoothScrollToPosition(0);
//                            } catch (JSONException e) {
//                                Toast.makeText(OrdersActivity.this, e.toString(), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(postReq, tag_json_obj);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);



        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                my_today_order_adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                my_today_order_adapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }


    @Override
    public void onContactSelected(OrdersModel contact) {

    }
}
