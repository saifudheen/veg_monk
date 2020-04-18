//package com.tecmanic.storemanager.Dashboard;
//
//import android.app.SearchManager;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.DividerItemDecoration;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.SearchView;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonArrayRequest;
//import com.android.volley.toolbox.Volley;
//import com.tecmanic.storemanager.Adapter.AppUserAdapter;
//import com.tecmanic.storemanager.Config.BaseURL;
//import com.tecmanic.storemanager.MainActivity;
//import com.tecmanic.storemanager.Model.AppUserModel;
//import com.tecmanic.storemanager.R;
//import com.tecmanic.storemanager.util.ConnectivityReceiver;
//
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class AppUserActivity extends AppCompatActivity implements AppUserAdapter.AppUserAdapterListener {
//    private String url = "http://neerajbisht.com/grocery_test/store/index.php/api/all_users";
//    private RecyclerView mList;
//    private List<AppUserModel> movieList;
//    private AppUserAdapter adapter;
//    private SearchView searchView;
//    RelativeLayout back_button;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_app_user);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setTitle("App Users");
//        mList = findViewById(R.id.main_list);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AppUserActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        movieList = new ArrayList<>();
//        adapter = new AppUserAdapter(this, movieList, this);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        mList.setLayoutManager(mLayoutManager);
//        mList.setItemAnimator(new DefaultItemAnimator());
//        mList.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
//        mList.setAdapter(adapter);
//        if (ConnectivityReceiver.isConnected()) {
//            getData();
//        } else {
//            Toast.makeText(getApplicationContext(), "Network Issue", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void getData() {
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(BaseURL.APP_USER_URL, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                for (int i = 0; i < response.length(); i++) {
//                    try {
//                        JSONObject jsonObject = response.getJSONObject(i);
//                        AppUserModel movie = new AppUserModel();
//                        movie.setId(jsonObject.getString("user_id"));
//                        movie.setName(jsonObject.getString("user_fullname"));
//                        movie.setPhone(jsonObject.getString("user_phone"));
//                        movie.setEmail(jsonObject.getString("user_email"));
//                        movie.setWallet(jsonObject.getString("wallet"));
//                        movie.setRewards(jsonObject.getString("rewards"));
//                        movie.setStatus(jsonObject.getString("status"));
//                        movieList.add(movie);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                adapter.notifyDataSetChanged();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("Volley", error.toString());
//            }
//        });
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(jsonArrayRequest);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView = (SearchView) menu.findItem(R.id.action_search)
//                .getActionView();
//        searchView.setSearchableInfo(searchManager
//                .getSearchableInfo(getComponentName()));
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                adapter.getFilter().filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//                adapter.getFilter().filter(query);
//                return false;
//            }
//        });
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_search) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (!searchView.isIconified()) {
//            searchView.setIconified(true);
//            return;
//        }
//        super.onBackPressed();
//    }
//
//    private void whiteNotificationBar(View view) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            int flags = view.getSystemUiVisibility();
//            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
//            view.setSystemUiVisibility(flags);
//            getWindow().setStatusBarColor(Color.WHITE);
//        }
//    }
//
//    @Override
//    public void onContactSelected(AppUserModel contact) {
//        //   Toast.makeText(getApplicationContext(), "Selected: " + contact.getCatogary_id() + ", " + contact.getTitle(), Toast.LENGTH_LONG).show();
//
//
//    }
//}
