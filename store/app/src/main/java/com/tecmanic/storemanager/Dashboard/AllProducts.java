package com.tecmanic.storemanager.Dashboard;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.tecmanic.storemanager.Adapter.AllProductsAdapter;
import com.tecmanic.storemanager.Config.BaseURL;
import com.tecmanic.storemanager.MainActivity;
import com.tecmanic.storemanager.Model.AlllProductModel;
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

public class AllProducts extends AppCompatActivity implements AllProductsAdapter.AllProductsAdapterListener {
    private RecyclerView mList;
    private List<AlllProductModel> movieList;
    private AllProductsAdapter adapter;
    private SearchView searchView;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor1;

    String getUser_id;

    private Session_management sessionManagement;

    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        sessionManagement = new Session_management(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("All Products");
        mList = findViewById(R.id.main_list);

        getUser_id =sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

        sharedPreferences = getSharedPreferences("personinfo", MODE_PRIVATE);
        editor1 = sharedPreferences.edit();

        user_id = sharedPreferences.getString("user_id", "");


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllProducts.this, MainActivity.class);
                startActivity(intent);
            }
        });
        movieList = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mList.setLayoutManager(mLayoutManager);
//        mList.setItemAnimator(new DefaultItemAnimator());
//        mList.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));

        if (ConnectivityReceiver.isConnected()) {
            getData();
        } else {
            Toast.makeText(getApplicationContext(), "Network Issue", Toast.LENGTH_SHORT).show();
        }
    }


    private void getData() {

        String tag_json_obj = "json_login_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", getUser_id);

        Log.d("user" , getUser_id);
        CustomVolleyJsonArrayRequest jsonArrayRequest = new CustomVolleyJsonArrayRequest(Request.Method.POST, BaseURL.ALL_PRODUCTS_URL, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("fda",response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        AlllProductModel movie = new AlllProductModel();
                        movie.setId(jsonObject.getString("product_id"));
                        movie.setTitle(jsonObject.getString("product_name"));
                        movie.setCatogary_id(jsonObject.getString("category_id"));
                        movie.setStock(jsonObject.getString("in_stock"));
                        movie.setPrice(jsonObject.getString("price"));
                        movie.setUnit_value(jsonObject.getString("unit_value"));
                        movie.setUnit(jsonObject.getString("unit"));
                        movie.setIncrement(jsonObject.getString("increament"));
                        movie.setReward(jsonObject.getString("rewards"));
                        movie.setImage(jsonObject.getString("product_image"));

                        movieList.add(movie);
                      adapter=new AllProductsAdapter(AllProducts.this,movieList,AllProducts.this);
                        mList.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                adapter.notifyDataSetChanged();
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
    public void onContactSelected(AlllProductModel contact) {
        //   Toast.makeText(getApplicationContext(), "Selected: " + contact.getCatogary_id() + ", " + contact.getTitle(), Toast.LENGTH_LONG).show();

    }
}
