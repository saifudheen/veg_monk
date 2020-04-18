package com.godeliver.user.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.godeliver.user.Activity.OrderDetail;
import com.godeliver.user.AppController;
import com.godeliver.user.Config.BaseURL;
import com.godeliver.user.Model.My_order_model;
import com.godeliver.user.R;
import com.godeliver.user.util.CustomVolleyJsonArrayRequest;
import com.godeliver.user.util.CustomVolleyJsonRequest;
import com.godeliver.user.util.Session_management;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends Fragment {

    private static String TAG = Home.class.getSimpleName();
    private RecyclerView rv_myorder;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private List<My_order_model> my_order_modelList = new ArrayList<>();
    private Session_management sessionManagement;
    String get_id, delivery_boy_id;
    TextView status;
    SwipeRefreshLayout refresh_layout;
    Handler handler = new Handler();
    Runnable timeCounter;
    public Home() {

    }


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,
                container, false);
        sessionManagement = new Session_management(getActivity());
        get_id = sessionManagement.getUserDetails().get(BaseURL.KEY_NAME);
        delivery_boy_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
        rv_myorder = (RecyclerView) view.findViewById(R.id.rv_myorder);
        rv_myorder.setLayoutManager(new LinearLayoutManager(getActivity()));
        status = view.findViewById(R.id.status);
        sharedPreferences = getActivity().getSharedPreferences("check", 0);
        editor = sharedPreferences.edit();
        SwitchCompat sw = (SwitchCompat) view.findViewById(R.id.switch_but);
        refresh_layout = view.findViewById(R.id.refresh_layout);


        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.clear();
                    editor.putString("status", "1");
                    status("1");
                    editor.commit();
                    // The toggle is enabled
                } else {
                    editor.clear();
                    editor.putString("status", "0");
                    editor.commit();

                    status("0");

                    // The toggle is disabled
                }
            }
        });

        String check_onoff = sharedPreferences.getString("status", "");


        status(check_onoff);

        if (sessionManagement.isLoggedIn()) {
            if (check_onoff.contains("1")) {
                sw.setChecked(true);

            } else if (check_onoff.contains("0")) {

                sw.setChecked(false);
            }

        } else {
            editor.clear();
        }
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(handler!=null && timeCounter!=null){
                    handler.removeCallbacks(timeCounter);
                }
                makeGetOrderRequest();
                refresh_layout.setRefreshing(false);

            }
        });

        makeGetOrderRequest();

        return view;
    }

    private void makeGetOrderRequest() {

        String tag_json_obj = "json_socity_req";

        Map<String, String> params = new HashMap<String, String>();

        params.put("d_id", get_id);

        params.put("filter", "0");

        CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.POST,
                BaseURL.GET_ORDER_URL, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                try {

                    for (int i = 0; i < response.length(); i++) {

                        JSONObject obj = response.getJSONObject(i);
                        String saleid = obj.getString("sale_id");
                        String placedon = obj.getString("on_date");
                        String timefrom = obj.getString("delivery_time_from");
                        String timeto = obj.getString("delivery_time_to");
                        String item = obj.getString("total_items");
                        String ammount = obj.getString("total_amount");
                        String status = obj.getString("status");

                        String lat = obj.getString("lat");
                        String lng = obj.getString("lng");
//                        String society = obj.getString("socity_name");
                        String house = obj.getString("house_no");
                        String rename = obj.getString("receiver_name");
                        String renumber = obj.getString("receiver_mobile");
                        String storename=obj.getString("store_name");
                        My_order_model my_order_model = new My_order_model();
//                        my_order_model.setSocityname(society);

                        my_order_model.setLat(lat);
                        my_order_model.setLng(lng);
                        my_order_model.setHouse(house);
                        my_order_model.setStore_name(storename);
                        my_order_model.setRecivername(rename);
                        my_order_model.setRecivermobile(renumber);
                        my_order_model.setDelivery_time_from(timefrom);
                        my_order_model.setSale_id(saleid);
                        my_order_model.setOn_date(placedon);
                        my_order_model.setDelivery_time_to(timeto);
                        my_order_model.setTotal_amount(ammount);
                        my_order_model.setStatus(status);
                        my_order_model.setTotal_items(item);
                        my_order_modelList.add(my_order_model);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                My_Order_Adapter myPendingOrderAdapter = new My_Order_Adapter(my_order_modelList);
                rv_myorder.setAdapter(myPendingOrderAdapter);
                myPendingOrderAdapter.notifyDataSetChanged();


                if (my_order_modelList.isEmpty()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }


    private void status(String check) {

        String tag_json_obj = "json_socity_req";

        Map<String, String> params = new HashMap<String, String>();

        params.put("delivery_boy_id", delivery_boy_id);

        params.put("delivery_boy_status", check);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.check_Status, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {

                    boolean value = response.getBoolean("status");

                    if (value) {
                        status.setText("Active");
                    } else {
                        status.setText("Inactive");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    public class My_Order_Adapter extends RecyclerView.Adapter<My_Order_Adapter.MyViewHolder> {

        private List<My_order_model> modelList;
        private LayoutInflater inflater;
        private android.support.v4.app.Fragment currentFragment;
       ProgressDialog progressDialog;
        private Context context;

        public My_Order_Adapter(Context context, List<My_order_model> modemodelList, final android.support.v4.app.Fragment currentFragment) {

            this.context = context;
            this.modelList = modemodelList;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.currentFragment = currentFragment;

        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_orderno, tv_status, tv_date, tv_time, tv_price, tv_item, relativetextstatus, tv_tracking_date, tv_socity,
                    tv_recivername, tv_house,tv_storename;

            Button get_dirc, pickorder;
            ImageView call;
            CardView cardView;

            public MyViewHolder(View view) {
                super(view);

                tv_storename=view.findViewById(R.id.store_name);
                get_dirc = view.findViewById(R.id.get_dirc);
                tv_orderno = (TextView) view.findViewById(R.id.tv_order_no);
                tv_status = (TextView) view.findViewById(R.id.tv_order_status);
                relativetextstatus = (TextView) view.findViewById(R.id.status);
                tv_tracking_date = (TextView) view.findViewById(R.id.tracking_date);
                tv_date = (TextView) view.findViewById(R.id.tv_order_date);
                tv_time = (TextView) view.findViewById(R.id.tv_order_time);
                tv_price = (TextView) view.findViewById(R.id.tv_order_price);
                tv_item = (TextView) view.findViewById(R.id.tv_order_item);
                tv_socity = view.findViewById(R.id.tv_societyname);
                tv_house = view.findViewById(R.id.tv_house);
                tv_recivername = view.findViewById(R.id.tv_recivername);
                call = view.findViewById(R.id.call);
                pickorder = view.findViewById(R.id.order_picked);
                cardView = view.findViewById(R.id.card_view);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//                        String saleid = modelList.get(position).getSale_id();
//                        String placedon = modelList.get(position).getOn_date();
//                        String time = modelList.get(position).getDelivery_time_from() + "-" + modelList.get(position).getDelivery_time_to();
//                        String item = modelList.get(position).getTotal_items();
//                        String ammount = modelList.get(position).getTotal_amount();
//                        String status = modelList.get(position).getStatus();
//                        String society = modelList.get(position).getSocityname();
//                        String house = modelList.get(position).getHouse();
//                        String recivername = modelList.get(position).getRecivername();
//                        String recivermobile = modelList.get(position).getRecivermobile();
//                        Intent intent = new Intent(context, OrderDetail.class);
//                        intent.putExtra("sale_id", saleid);
//                        intent.putExtra("placedon", placedon);
//                        intent.putExtra("time", time);
//                        intent.putExtra("item", item);
//                        intent.putExtra("ammount", ammount);
//                        intent.putExtra("status", status);
//                        intent.putExtra("socity_name", society);
//                        intent.putExtra("house_no", house);
//                        intent.putExtra("receiver_name", recivername);
//                        intent.putExtra("receiver_mobile", recivermobile);
//                        context.startActivity(intent);
////
//                    }
//                }
//            });


            }
        }

        public My_Order_Adapter(List<My_order_model> modelList) {
            this.modelList = modelList;
        }

        @Override
        public My_Order_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_rv, parent, false);


            context = parent.getContext();

            return new My_Order_Adapter.MyViewHolder(itemView);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(My_Order_Adapter.MyViewHolder holder, final int position) {
            final My_order_model mList = modelList.get(position);


            progressDialog=new ProgressDialog(context);
            progressDialog.setMessage("Loading");

            try {


                holder.tv_orderno.setText(mList.getSale_id());

                if (mList.getStatus().equals("0")) {
                    holder.pickorder.setVisibility(View.GONE);
                    holder.relativetextstatus.setEnabled(false);

                    holder.tv_status.setText(context.getResources().getString(R.string.pending));
                    holder.relativetextstatus.setText(context.getResources().getString(R.string.pending));
                } else if (mList.getStatus().equals("1")) {
                    holder.tv_status.setText(context.getResources().getString(R.string.confirm));
                    holder.pickorder.setVisibility(View.GONE);
                    holder.relativetextstatus.setEnabled(false);
                    holder.relativetextstatus.setText(context.getResources().getString(R.string.confirm));
                    holder.tv_status.setTextColor(context.getResources().getColor(R.color.green));
                } else if (mList.getStatus().equals("2")) {
                    holder.get_dirc.setVisibility(View.VISIBLE);
                    holder.pickorder.setVisibility(View.GONE);
                    holder.relativetextstatus.setEnabled(true);
                    holder.tv_status.setText(context.getResources().getString(R.string.outfordeliverd));
                    holder.relativetextstatus.setText(context.getResources().getString(R.string.outfordeliverd));
                    holder.tv_status.setTextColor(context.getResources().getColor(R.color.green));
                } else if (mList.getStatus().equals("4")) {
                    holder.tv_status.setText(context.getResources().getString(R.string.delivered));
                    holder.pickorder.setVisibility(View.GONE);
                    holder.relativetextstatus.setEnabled(false);

                    holder.relativetextstatus.setText(context.getResources().getString(R.string.delivered));
                    holder.tv_status.setTextColor(context.getResources().getColor(R.color.green));
                } else if (mList.getStatus().equals("3")) {
                    holder.tv_status.setText("Cancel");
                    holder.relativetextstatus.setEnabled(false);
                    holder.pickorder.setVisibility(View.GONE);
                    holder.relativetextstatus.setText("Cancel");
                    holder.tv_status.setTextColor(context.getResources().getColor(R.color.green));
                }
                else if (mList.getStatus().equals("5")) {

                    holder.tv_status.setText("Ready to Pickup");
                    holder.relativetextstatus.setEnabled(false);
                    holder.pickorder.setVisibility(View.VISIBLE);
                    holder.relativetextstatus.setText("Ready to Pickup");
                    holder.tv_status.setTextColor(context.getResources().getColor(R.color.green));

                }


                holder.relativetextstatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String saleid = modelList.get(position).getSale_id();
                        String placedon = modelList.get(position).getOn_date();
                        String time = modelList.get(position).getDelivery_time_from() + "-" + modelList.get(position).getDelivery_time_to();
                        String item = modelList.get(position).getTotal_items();
                        String ammount = modelList.get(position).getTotal_amount();
                        String status = modelList.get(position).getStatus();
                        String society = modelList.get(position).getSocityname();
                        String house = modelList.get(position).getHouse();
                        String recivername = modelList.get(position).getRecivername();
                        String recivermobile = modelList.get(position).getRecivermobile();
                        Intent intent = new Intent(context, OrderDetail.class);
                        intent.putExtra("sale_id", saleid);
                        intent.putExtra("placedon", placedon);
                        intent.putExtra("time", time);
                        intent.putExtra("item", item);
                        intent.putExtra("ammount", ammount);
                        intent.putExtra("status", status);
                        intent.putExtra("socity_name", society);
                        intent.putExtra("house_no", house);
                        intent.putExtra("receiver_name", recivername);
                        intent.putExtra("receiver_mobile", recivermobile);
                        context.startActivity(intent);
//
                    }
                });
                holder.get_dirc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + mList.getLat() + "," + mList.getLng()));
                        context.startActivity(intent);
                    }
                });


            } catch (Exception e) {

            }

            holder.tv_date.setText(mList.getOn_date());
            holder.tv_tracking_date.setText(mList.getOn_date());
            holder.tv_time.setText(mList.getDelivery_time_from() + "-" + mList.getDelivery_time_to());
            holder.tv_price.setText(context.getResources().getString(R.string.currency) + mList.getTotal_amount());
            holder.tv_item.setText(context.getResources().getString(R.string.tv_cart_item) + mList.getTotal_items());
            holder.tv_socity.setText(mList.getSocityname());
            holder.tv_house.setText(mList.getHouse());
            holder.tv_storename.setText(mList.getStore_name());
            holder.tv_recivername.setText(modelList.get(position).getRecivername());
            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPermissionGranted()) {
                        call_action(mList.getRecivermobile());
                    }

                }
            });

            holder.pickorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
            progressDialog.show();
                    order_picked(mList.getSale_id());

                }
            });
        }


        @Override
        public int getItemCount() {
            return modelList.size();

        }

        public boolean isPermissionGranted() {
            if (Build.VERSION.SDK_INT >= 23) {
                if (context.checkSelfPermission(Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v("TAG", "Permission is granted");
                    return true;
                } else {

                    Log.v("TAG", "Permission is revoked");
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 1);
                    return false;
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                Log.v("TAG", "Permission is granted");
                return true;
            }
        }

        public void call_action(String number) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));

            context.startActivity(callIntent);
        }

        private void order_picked(String saleid) {

            String tag_json_obj = "json_socity_req";

            Map<String, String> params = new HashMap<String, String>();


            params.put("sale_id", saleid);

            CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                    BaseURL.pickup, params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {


                    try {

                        boolean value = response.getBoolean("status");
                           progressDialog.dismiss();
                        Fragment fm = new Home();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.contentPanel, fm, "Home_fragment")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(context, context.getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        }


    }
}