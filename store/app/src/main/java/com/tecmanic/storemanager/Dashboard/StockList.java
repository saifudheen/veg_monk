package com.tecmanic.storemanager.Dashboard;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.tecmanic.storemanager.Adapter.StockAdapter;
import com.tecmanic.storemanager.Config.BaseURL;
import com.tecmanic.storemanager.MainActivity;
import com.tecmanic.storemanager.Model.StockModel;
import com.tecmanic.storemanager.R;
import com.tecmanic.storemanager.util.ConnectivityReceiver;
import com.tecmanic.storemanager.util.CustomVolleyJsonArrayRequest;
import com.tecmanic.storemanager.util.CustomVolleyJsonRequest;
import com.tecmanic.storemanager.util.Session_management;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockList extends AppCompatActivity implements StockAdapter.StockAdapterListener {

    private String url = "http://neerajbisht.com/grocery_test/store/index.php/api/get_leftstock";
    private RecyclerView mList;
    private List<StockModel> movieList;
    private StockAdapter adapter;
    private SearchView searchView;
    private Session_management sessionManagement;

    ImageView back_button;
    String getuserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_list);

        sessionManagement = new Session_management(getApplicationContext());

        getuserid =sessionManagement.getUserDetails().get(BaseURL.KEY_ID);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Stock");
        mList = findViewById(R.id.main_list);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StockList.this, MainActivity.class);
                startActivity(intent);
            }
        });
        movieList = new ArrayList<>();
        adapter = new StockAdapter(this, movieList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mList.setLayoutManager(mLayoutManager);
        mList.setItemAnimator(new DefaultItemAnimator());
        mList.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        mList.setAdapter(adapter);
        if (ConnectivityReceiver.isConnected()) {
            getData();
        } else {
            Toast.makeText(getApplicationContext(), "Network Issue", Toast.LENGTH_SHORT).show();
        }
    }

    private void getData() {
        String tag_json_obj = "json_login_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", getuserid);

        CustomVolleyJsonArrayRequest jsonArrayRequest = new CustomVolleyJsonArrayRequest (Request.Method.POST, BaseURL.STOCK_LIST, params, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        StockModel movie = new StockModel();
                        movie.setId(jsonObject.getString("product_id"));
                        movie.setName(jsonObject.getString("product_name"));
                        movie.setUnit(jsonObject.getString("unit"));
                        movie.setPrice(jsonObject.getString("stock"));
                        movieList.add(movie);
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

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
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
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
    public void onContactSelected(StockModel contact) {

    }
}
