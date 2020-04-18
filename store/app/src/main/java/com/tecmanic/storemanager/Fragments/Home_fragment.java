package com.tecmanic.storemanager.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.tecmanic.storemanager.Adapter.SelectDeliveryBoyListViewAdapter;
import com.tecmanic.storemanager.AppController;
import com.tecmanic.storemanager.Config.BaseURL;
import com.tecmanic.storemanager.Config.SharedPref;
import com.tecmanic.storemanager.Dashboard.AssignSuccess;
import com.tecmanic.storemanager.Dashboard.MyOrderDeatil;
import com.tecmanic.storemanager.MainActivity;
import com.tecmanic.storemanager.Model.NextdayOrderModel;
import com.tecmanic.storemanager.Model.TodayOrderModel;
import com.tecmanic.storemanager.NetworkConnectivity.NetworkConnection;
import com.tecmanic.storemanager.R;
import com.tecmanic.storemanager.util.CustomVolleyJsonRequest;
import com.tecmanic.storemanager.util.Session_management;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home_fragment extends Fragment {

    private static String TAG = Home_fragment.class.getSimpleName();
    private RecyclerView rv_today_orders, rv_next_day_orders;
    private My_Today_Order_Adapter my_today_order_adapter;
    private My_Nextday_Order_Adapter my_nextday_order_adapter;
    private List<TodayOrderModel> movieList = new ArrayList<>();
    private List<NextdayOrderModel> nextdayOrderModels = new ArrayList<>();
    ProgressDialog pd;
    private LinearLayout linearLayout;
    String getuserid;
    private Session_management sessionManagement;


    public Home_fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        sessionManagement = new Session_management(getActivity());

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.app_name));
        rv_today_orders = (RecyclerView) view.findViewById(R.id.rv_today_order);

        getuserid = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

        rv_next_day_orders = (RecyclerView) view.findViewById(R.id.rv_next_order);
        rv_today_orders.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_next_day_orders.setLayoutManager(new LinearLayoutManager(getActivity()));
        getTodayOrders();
        getNextDayOrders();

        return view;


    }


    private void getTodayOrders() {
        RequestQueue rq = Volley.newRequestQueue(getActivity());

        String tag_json_obj = "json_login_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", getuserid);

        CustomVolleyJsonRequest jsonRequest = new CustomVolleyJsonRequest(Request.Method.POST, BaseURL.DAshborad_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final List<TodayOrderModel> data = new ArrayList<>();

                Log.i("dfs", response.toString());
                try {
//                    JSONObject object = new JSONObject(response.toString());
                    JSONArray Jarray = response.getJSONArray("today_orders");
                    for (int i = 0; i < Jarray.length(); i++) {
                        JSONObject json_data = Jarray.getJSONObject(i);
                        TodayOrderModel brandModel = new TodayOrderModel();
                        brandModel.sale_id = json_data.getString("sale_id");
                        final String sal = brandModel.sale_id;
                        SharedPref.putString(getActivity(), BaseURL.KEY_ORDER_ID, sal);
                        brandModel.user_id = json_data.getString("user_id");
                        brandModel.on_date = json_data.getString("on_date");
                        brandModel.delivery_time_from = json_data.getString("delivery_time_from");
                        brandModel.delivery_time_to = json_data.getString("delivery_time_to");
                        brandModel.status = json_data.getString("status");
                        brandModel.note = json_data.getString("note");
                        brandModel.is_paid = json_data.getString("is_paid");
                        brandModel.total_amount = json_data.getString("total_amount");
                        brandModel.total_rewards = json_data.getString("total_rewards");
                        brandModel.total_kg = json_data.getString("total_kg");
                        brandModel.total_items = json_data.getString("total_items");
                        brandModel.socity_id = json_data.getString("socity_id");
                        brandModel.delivery_address = json_data.getString("delivery_address");
                        brandModel.location_id = json_data.getString("location_id");
                        brandModel.delivery_charge = json_data.getString("delivery_charge");
                        brandModel.new_store_id = json_data.getString("new_store_id");
                        brandModel.assign_to = json_data.getString("assign_to");
                        brandModel.payment_method = json_data.getString("payment_method");
                        brandModel.user_fullname = json_data.getString("user_fullname");
                        brandModel.user_phone = json_data.getString("user_phone");
                        brandModel.pincode = json_data.getString("pincode");
                        brandModel.house_no = json_data.getString("house_no");
                        brandModel.socity_name = json_data.getString("socity_name");
                        brandModel.receiver_name = json_data.getString("receiver_name");
                        brandModel.receiver_mobile = json_data.getString("receiver_mobile");
                        data.add(brandModel);
                    }
                    my_today_order_adapter = new My_Today_Order_Adapter(getActivity(), data);
                    rv_today_orders.setAdapter(my_today_order_adapter);
                       my_today_order_adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
        rq.add(jsonRequest);
    }

    private void getNextDayOrders() {

        RequestQueue rq = Volley.newRequestQueue(getActivity());

        String tag_json_obj = "json_login_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", getuserid);

        CustomVolleyJsonRequest jsonRequest = new CustomVolleyJsonRequest(Request.Method.POST, BaseURL.DAshborad_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final List<NextdayOrderModel> data = new ArrayList<>();

                Log.i("dfs", response.toString());
                try {
//                    JSONObject object = new JSONObject(response.toString());
                    JSONArray Jarray = response.getJSONArray("nextday_orders");
                    for (int i = 0; i < Jarray.length(); i++) {
                        JSONObject json_data = Jarray.getJSONObject(i);
                        NextdayOrderModel brandModel = new NextdayOrderModel();
                        brandModel.sale_id = json_data.getString("sale_id");
                        final String sal = brandModel.sale_id;
                        SharedPref.putString(getActivity(), BaseURL.KEY_ORDER_ID, sal);
                        brandModel.user_id = json_data.getString("user_id");
                        brandModel.on_date = json_data.getString("on_date");
                        brandModel.delivery_time_from = json_data.getString("delivery_time_from");
                        brandModel.delivery_time_to = json_data.getString("delivery_time_to");
                        brandModel.status = json_data.getString("status");
                        brandModel.note = json_data.getString("note");
                        brandModel.is_paid = json_data.getString("is_paid");
                        brandModel.total_amount = json_data.getString("total_amount");
                        brandModel.total_rewards = json_data.getString("total_rewards");
                        brandModel.total_kg = json_data.getString("total_kg");
                        brandModel.total_items = json_data.getString("total_items");
                        brandModel.socity_id = json_data.getString("socity_id");
                        brandModel.delivery_address = json_data.getString("delivery_address");
                        brandModel.location_id = json_data.getString("location_id");
                        brandModel.delivery_charge = json_data.getString("delivery_charge");
                        brandModel.new_store_id = json_data.getString("new_store_id");
                        brandModel.assign_to = json_data.getString("assign_to");
                        brandModel.payment_method = json_data.getString("payment_method");
                        brandModel.user_fullname = json_data.getString("user_fullname");
                        brandModel.user_phone = json_data.getString("user_phone");
                        brandModel.pincode = json_data.getString("pincode");
                        brandModel.house_no = json_data.getString("house_no");
                        brandModel.socity_name = json_data.getString("socity_name");
                        brandModel.receiver_name = json_data.getString("receiver_name");
                        brandModel.receiver_mobile = json_data.getString("receiver_mobile");
                        data.add(brandModel);
                    }

                    my_nextday_order_adapter = new My_Nextday_Order_Adapter(getActivity(), data);
                    rv_next_day_orders.setAdapter(my_nextday_order_adapter);
                    my_nextday_order_adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
        rq.add(jsonRequest);
    }
    public class My_Nextday_Order_Adapter extends RecyclerView.Adapter<My_Nextday_Order_Adapter.MyViewHolder> {

        private List<NextdayOrderModel> modelList;
        private LayoutInflater inflater;
        private android.support.v4.app.Fragment currentFragment;
        ProgressDialog progressDialog;
        private Context context;

        public My_Nextday_Order_Adapter(Context context, List<NextdayOrderModel> modemodelList, final android.support.v4.app.Fragment currentFragment) {

            this.context = context;
            this.modelList = modelList;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.currentFragment = currentFragment;
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_ammount, tv_assign_to, tv_status, tv_order_id, tv_customer_name,
                    tv_customer_socity, tv_customer_phone, tv_order_date, tv_order_time, Assign_Boy_name, payment_mode;
            public RelativeLayout Assign_Order_to_layout, Assign_Order_button;
            public CardView card_view;
            int Selected_Boy = 0;
            String SelectBoy = "";
            Button confirm_order,pick_uporder,cancel_order;

            ArrayList<String> Boy_List = new ArrayList<>();
            String Boy_Id;
            ArrayList<String> BOY_LIST_ID = new ArrayList<>();
            private JsonObject Json;
            private LinearLayout assign_layout;

            public MyViewHolder(View view) {
                super(view);
                tv_ammount = (TextView) view.findViewById(R.id.ammount);
                payment_mode = (TextView) view.findViewById(R.id.payment_mode);
                tv_assign_to = (TextView) view.findViewById(R.id.assign_to);
                tv_status = (TextView) view.findViewById(R.id.status);
                tv_order_id = (TextView) view.findViewById(R.id.order_id);
                confirm_order=view.findViewById(R.id.confirm);
                pick_uporder=view.findViewById(R.id.pickup);
                cancel_order=view.findViewById(R.id.cancle);
                tv_customer_name = (TextView) view.findViewById(R.id.customer_name);
                tv_customer_socity = (TextView) view.findViewById(R.id.order_socity);
                tv_customer_phone = (TextView) view.findViewById(R.id.customer_phone);
                tv_order_date = (TextView) view.findViewById(R.id.order_date);
                tv_order_time = (TextView) view.findViewById(R.id.order_time);
                Assign_Order_to_layout = (RelativeLayout) view.findViewById(R.id.assign_order_to);
                Assign_Boy_name = (TextView) view.findViewById(R.id.order_assign_list);
                Assign_Order_button = (RelativeLayout) view.findViewById(R.id.assign_order);
                assign_layout = (LinearLayout) view.findViewById(R.id.assign_layout);
                Assign_Order_to_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Get_Boys();

                    }
                });
                Assign_Order_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        final String id = modelList.get(pos).getSale_id();
                        final String getname = SharedPref.getString(context, BaseURL.KEY_DELIVERY_BOY_NAME);
                        if (NetworkConnection.connectionChecking(context)) {
                            RequestQueue rq = Volley.newRequestQueue(context);
                            StringRequest postReq = new StringRequest(Request.Method.POST, BaseURL.ASSIGN_ORDER,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.i("eclipse", "Response=" + response);
                                            try {
                                                JSONObject object = new JSONObject(response);
                                                JSONArray Jarray = object.getJSONArray("assign");
                                                for (int i = 0; i < Jarray.length(); i++) {
                                                    JSONObject json_data = Jarray.getJSONObject(i);
                                                    String msg = json_data.getString("msg");
                                                    Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(context, AssignSuccess.class);
                                                    context.startActivity(intent);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    System.out.println("Error [" + error + "]");
                                }
                            }) {

                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("order_id", id);
                                    params.put("boy_name", getname);
                                    return params;
                                }
                            };
                            rq.add(postReq);
                        } else {
                            Intent intent = new Intent(context, NetworkError.class);
                            context.startActivity(intent);
                        }
                    }
                });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            String saleid = modelList.get(position).getSale_id();
                            String userfullname = modelList.get(position).getUser_fullname();
                            String socity = modelList.get(position).getSocity_name();
                            String customerphone = modelList.get(position).getUser_phone();
                            String date = modelList.get(position).getOn_date();
                            String time = modelList.get(position).getDelivery_time_from() + "-" + modelList.get(position).getDelivery_time_to();
                            String ammount = modelList.get(position).getTotal_amount();
                            String status = modelList.get(position).getStatus();
                            Intent intent = new Intent(context, MyOrderDeatil.class);
                            intent.putExtra("sale_id", saleid);
                            intent.putExtra("user_fullname", userfullname);
                            intent.putExtra("socity", socity);
                            intent.putExtra("customer_phone", customerphone);
                            intent.putExtra("date", date);
                            intent.putExtra("time", time);
                            intent.putExtra("ammount", ammount);
                            intent.putExtra("status", status);
                            context.startActivity(intent);

                        }
                    }
                });


            }

            private void SelectBoyDialog() {
                final Dialog dialog;
                dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_assign_order_dialog);
                final ListView listView = (ListView) dialog.findViewById(R.id.list_order);
                SelectDeliveryBoyListViewAdapter sec = new SelectDeliveryBoyListViewAdapter(context, Boy_List);
                listView.setAdapter(sec);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        SelectBoy = (String) adapterView.getItemAtPosition(position);
                        Assign_Boy_name.setText(StringUtils.capitalize(Boy_List.get(position).toLowerCase().trim()));
                        SelectBoy = Assign_Boy_name.getText().toString();
                        SharedPref.putString(context, BaseURL.KEY_DELIVERY_BOY_NAME, SelectBoy);
                        Selected_Boy = position + 1;
                        Boy_Id = ("" + BOY_LIST_ID.get(position));
                        SharedPref.putString(context, BaseURL.KEY_DELIVERY_BOY_ID, Boy_Id);
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().getDecorView().setTop(100);
                dialog.getWindow().getDecorView().setLeft(100);
                dialog.show();

            }

            private void Get_Boys() {
                if (NetworkConnection.connectionChecking(context)) {
                    Json = new JsonObject();
                    Ion.with(context).load(BaseURL.DELIVERY_BOY)
                            .setTimeout(15000).setJsonObjectBody(Json).asString().setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if (e == null) {
                                Log.e("result", result);
                                try {
                                    JSONObject js = new JSONObject(result);
                                    {
                                        JSONArray obj = js.getJSONArray("delivery_boy");
                                        Boy_List.clear();
                                        BOY_LIST_ID.clear();
                                        for (int i = 0; i < obj.length(); i++) {
                                            Boy_List.add("" + obj.getJSONObject(i).optString("user_name"));
                                            BOY_LIST_ID.add("" + obj.getJSONObject(i).optString("id"));

                                        }
                                        Log.e("Size", "" + Boy_List.size());
                                        Log.e("result", js.toString() + "\n" + js.getJSONArray("delivery_boy") + "\n"
                                                + obj.getJSONObject(0).optString("user_name"));
                                    }
                                    SelectBoyDialog();
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });

                } else {
                    Toast.makeText(context, "No Internet Connnection", Toast.LENGTH_SHORT).show();
                }
            }
        }

        public My_Nextday_Order_Adapter(Activity activity, List<NextdayOrderModel> modelList) {
            this.modelList = modelList;
        }

        @Override
        public My_Nextday_Order_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_next_day_order_rv
                    , parent, false);
            context = parent.getContext();
            return new My_Nextday_Order_Adapter.MyViewHolder(itemView);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final My_Nextday_Order_Adapter.MyViewHolder holder, int position) {
            final NextdayOrderModel mList = modelList.get(position);
            progressDialog=new ProgressDialog(context);
            progressDialog.setMessage("Loading");
            holder.tv_ammount.setText(context.getResources().getString(R.string.currency) + mList.getTotal_amount());
            if (mList.getStatus().equals("0")) {

                holder.tv_status.setText(context.getResources().getString(R.string.pending));

               holder.pick_uporder.setVisibility(View.GONE);

            } else if (mList.getStatus().equals("1")) {
                holder.tv_status.setText(context.getResources().getString(R.string.confirm));

                holder.confirm_order.setVisibility(View.GONE);

                holder.pick_uporder.setVisibility(View.VISIBLE);

            } else if (mList.getStatus().equals("2")) {


                holder.tv_status.setText("Picked Up");


            }
            else if (mList.getStatus().equals("3")) {


                holder.tv_status.setText("Cancel");
                holder.pick_uporder.setVisibility(View.GONE);
                holder.confirm_order.setVisibility(View.GONE);

                holder.cancel_order.setVisibility(View.GONE);
            }
            else if (mList.getStatus().equals("4")) {
                holder.tv_status.setText(context.getResources().getString(R.string.delivered));

            }
            else if (mList.getStatus().equals("5")) {

                holder.confirm_order.setVisibility(View.GONE);

                holder.tv_status.setText(context.getResources().getString(R.string.outfordeliverd));

            }

            if (mList.getAssign_to().equals("0")) {

            } else if (mList.getAssign_to() != "0") {

                holder.tv_assign_to.setText("Assign To " + mList.getAssign_to());
            }
            holder.tv_order_id.setText(mList.getSale_id());
            holder.payment_mode.setText(mList.getPayment_method());
            holder.tv_customer_name.setText(mList.getUser_fullname());
            holder.tv_customer_socity.setText(mList.getSocity_name());
            holder.tv_customer_phone.setText(mList.getUser_phone());
            holder.tv_order_date.setText(mList.getOn_date());
            holder.tv_order_time.setText(mList.getDelivery_time_from() + "-" + mList.getDelivery_time_to());
            holder.confirm_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();
                    confirm(mList.getSale_id(), holder.pick_uporder, holder.confirm_order);
                }
            });

            holder.pick_uporder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();
                    pickup(mList.getSale_id());
                }
            });
            holder.cancel_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();
                    cancel(mList.getSale_id());
                }
            });
        }


        @Override
        public int getItemCount()
        {
            return modelList.size();
        }
        private void confirm(String saleid, final Button pickup, final Button confim) {
            String tag_json_obj = "json_login_req";
            Map<String, String> params = new HashMap<String, String>();
            params.put("sale_id", saleid);

            CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                    BaseURL.Confimed_url, params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());

                    try {
                        Boolean status = response.getBoolean("status");
                        if (status) {
                            progressDialog.dismiss();
                            getNextDayOrders();
                            confim.setVisibility(View.GONE);
                            pickup.setVisibility(View.VISIBLE);
                        } else {


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
                        Toast.makeText(context, context.getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }

        private void pickup(String saleid) {
            String tag_json_obj = "json_login_req";
            Map<String, String> params = new HashMap<String, String>();
            params.put("sale_id", saleid);

            CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                    BaseURL.pickup_url, params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());

                    try {
                        Boolean status = response.getBoolean("status");
                        if (status) {
                            progressDialog.dismiss();
                            getNextDayOrders();
                        } else {
                            progressDialog.dismiss();
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
                        Toast.makeText(context, context.getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }
        private void cancel(String saleid) {
            String tag_json_obj = "json_login_req";
            Map<String, String> params = new HashMap<String, String>();
            params.put("sale_id", saleid);

            CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                    BaseURL.cancel_url, params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());

                    try {
                        Boolean status = response.getBoolean("status");
                        if (status) {
                            progressDialog.dismiss();
                            getNextDayOrders();
                        } else {
                            progressDialog.dismiss();
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
                        Toast.makeText(context, context.getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }

    }
    public class My_Today_Order_Adapter extends RecyclerView.Adapter<My_Today_Order_Adapter.MyViewHolder> {

        private List<TodayOrderModel> modelList;
        private LayoutInflater inflater;
        private android.support.v4.app.Fragment currentFragment;
        private Context context;

        ProgressDialog progressDialog;
        public My_Today_Order_Adapter(Context context, List<TodayOrderModel> modemodelList, final android.support.v4.app.Fragment currentFragment) {

            this.context = context;
            this.modelList = modelList;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.currentFragment = currentFragment;
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_ammount, tv_assign_to, tv_status, tv_order_id, tv_customer_name, tv_customer_socity, tv_customer_phone,
                    tv_order_date, tv_order_time, Assign_Boy_name, payment_mode;
            public RelativeLayout Assign_Order_to_layout, Assign_Order_button;
            public CardView card_view;
            int Selected_Boy = 0;
            String SelectBoy = "";
            Button confirm_order,pick_uporder,cancel_order;
            ArrayList<String> Boy_List = new ArrayList<>();
            String Boy_Id;
            ArrayList<String> BOY_LIST_ID = new ArrayList<>();
            private JsonObject Json;
            private LinearLayout assign_layout;


            public MyViewHolder(View view) {
                super(view);
                tv_ammount = (TextView) view.findViewById(R.id.ammount);
                payment_mode = (TextView) view.findViewById(R.id.payment_mode);
                tv_assign_to = (TextView) view.findViewById(R.id.assign_to);
                tv_status = (TextView) view.findViewById(R.id.status);
                confirm_order=view.findViewById(R.id.confirm);
                pick_uporder=view.findViewById(R.id.pickup);
                cancel_order=view.findViewById(R.id.cancle);
                tv_order_id = (TextView) view.findViewById(R.id.order_id);
                tv_customer_name = (TextView) view.findViewById(R.id.customer_name);
                tv_customer_socity = (TextView) view.findViewById(R.id.order_socity);
                tv_customer_phone = (TextView) view.findViewById(R.id.customer_phone);
                tv_order_date = (TextView) view.findViewById(R.id.order_date);
                tv_order_time = (TextView) view.findViewById(R.id.order_time);
                Assign_Order_to_layout = (RelativeLayout) view.findViewById(R.id.assign_order_to);
                Assign_Boy_name = (TextView) view.findViewById(R.id.order_assign_list);
                Assign_Order_button = (RelativeLayout) view.findViewById(R.id.assign_order);
                assign_layout = (LinearLayout) view.findViewById(R.id.assign_layout);
                Assign_Order_to_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Get_Boys();

                    }
                });
                Assign_Order_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        final String id = modelList.get(pos).getSale_id();
                        final String getname = SharedPref.getString(context, BaseURL.KEY_DELIVERY_BOY_NAME);
                        if (NetworkConnection.connectionChecking(context)) {
                            RequestQueue rq = Volley.newRequestQueue(context);
                            StringRequest postReq = new StringRequest(Request.Method.POST, BaseURL.ASSIGN_ORDER,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.i("eclipse", "Response=" + response);
                                            try {
                                                JSONObject object = new JSONObject(response);
                                                JSONArray Jarray = object.getJSONArray("assign");
                                                for (int i = 0; i < Jarray.length(); i++) {
                                                    JSONObject json_data = Jarray.getJSONObject(i);
                                                    String msg = json_data.getString("msg");
                                                    Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(context, AssignSuccess.class);
                                                    context.startActivity(intent);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    System.out.println("Error [" + error + "]");
                                }
                            }) {

                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("order_id", id);
                                    params.put("boy_name", getname);

                                    return params;
                                }
                            };
                            rq.add(postReq);
                        } else {
                            Intent intent = new Intent(context, NetworkError.class);
                            context.startActivity(intent);
                        }
                    }
                });
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            String saleid = modelList.get(position).getSale_id();
                            String userfullname = modelList.get(position).getUser_fullname();
                            String socity = modelList.get(position).getSocity_name();
                            String customerphone = modelList.get(position).getUser_phone();
                            String date = modelList.get(position).getOn_date();
                            String time = modelList.get(position).getDelivery_time_from() + "-" + modelList.get(position).getDelivery_time_to();
                            String ammount = modelList.get(position).getTotal_amount();
                            String status = modelList.get(position).getStatus();
                            Intent intent = new Intent(context, MyOrderDeatil.class);
                            intent.putExtra("sale_id", saleid);
                            intent.putExtra("user_fullname", userfullname);
                            intent.putExtra("socity", socity);
                            intent.putExtra("customer_phone", customerphone);
                            intent.putExtra("date", date);
                            intent.putExtra("time", time);
                            intent.putExtra("ammount", ammount);
                            intent.putExtra("status", status);
                            context.startActivity(intent);
                        }
                    }
                });


            }

            private void SelectBoyDialog() {
                final Dialog dialog;
                dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_assign_order_dialog);
                final ListView listView = (ListView) dialog.findViewById(R.id.list_order);
                SelectDeliveryBoyListViewAdapter sec = new SelectDeliveryBoyListViewAdapter(context, Boy_List);
                listView.setAdapter(sec);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        SelectBoy = (String) adapterView.getItemAtPosition(position);
                        Assign_Boy_name.setText(StringUtils.capitalize(Boy_List.get(position).toLowerCase().trim()));
                        SelectBoy = Assign_Boy_name.getText().toString();
                        SharedPref.putString(context, BaseURL.KEY_DELIVERY_BOY_NAME, SelectBoy);
                        Selected_Boy = position + 1;
                        Boy_Id = ("" + BOY_LIST_ID.get(position));
                        SharedPref.putString(context, BaseURL.KEY_DELIVERY_BOY_ID, Boy_Id);
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().getDecorView().setTop(100);
                dialog.getWindow().getDecorView().setLeft(100);
                dialog.show();

            }

            private void Get_Boys() {
                if (NetworkConnection.connectionChecking(context)) {
                    Json = new JsonObject();
                    Ion.with(context).load(BaseURL.DELIVERY_BOY)
                            .setTimeout(15000).setJsonObjectBody(Json).asString().setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if (e == null) {
                                Log.e("result", result);
                                try {
                                    JSONObject js = new JSONObject(result);
                                    {
                                        JSONArray obj = js.getJSONArray("delivery_boy");
                                        Boy_List.clear();
                                        BOY_LIST_ID.clear();
                                        for (int i = 0; i < obj.length(); i++) {
                                            Boy_List.add("" + obj.getJSONObject(i).optString("user_name"));
                                            BOY_LIST_ID.add("" + obj.getJSONObject(i).optString("id"));
                                        }
                                        Log.e("Size", "" + Boy_List.size());
                                        Log.e("result", js.toString() + "\n" + js.getJSONArray("delivery_boy") + "\n"
                                                + obj.getJSONObject(0).optString("user_name"));
                                    }
                                    SelectBoyDialog();
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });

                } else {
                    Toast.makeText(context, "No Internet Connnection", Toast.LENGTH_SHORT).show();
                }
            }


        }

        public My_Today_Order_Adapter(Activity activity, List<TodayOrderModel> modelList) {
            this.modelList = modelList;
        }

        @Override
        public My_Today_Order_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_today_order_rv, parent, false);
            context = parent.getContext();
            return new My_Today_Order_Adapter.MyViewHolder(itemView);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final My_Today_Order_Adapter.MyViewHolder holder, int position) {
            final TodayOrderModel mList = modelList.get(position);
            holder.tv_ammount.setText(context.getResources().getString(R.string.currency) + mList.getTotal_amount());
            progressDialog=new ProgressDialog(context);
            progressDialog.setMessage("Loading");

            if (mList.getStatus().equals("0")) {

                holder.tv_status.setText(context.getResources().getString(R.string.pending));

                holder.pick_uporder.setVisibility(View.GONE);

            } else if (mList.getStatus().equals("1")) {
                holder.tv_status.setText(context.getResources().getString(R.string.confirm));

                holder.confirm_order.setVisibility(View.GONE);

                holder.pick_uporder.setVisibility(View.VISIBLE);

            } else if (mList.getStatus().equals("2")) {


                holder.tv_status.setText("Picked Up");


            }
            else if (mList.getStatus().equals("3")) {


                holder.tv_status.setText("Cancel");
                holder.pick_uporder.setVisibility(View.GONE);
                holder.confirm_order.setVisibility(View.GONE);

                holder.cancel_order.setVisibility(View.GONE);
            }
            else if (mList.getStatus().equals("4")) {
                holder.tv_status.setText(context.getResources().getString(R.string.delivered));

            }
            else if (mList.getStatus().equals("5")) {

                holder.confirm_order.setVisibility(View.GONE);

                holder.tv_status.setText(context.getResources().getString(R.string.outfordeliverd));

            }


            if (mList.getAssign_to().equals("0")) {

            } else if (mList.getAssign_to() != "0") {

                holder.tv_assign_to.setText("Assign To " + mList.getAssign_to());
                holder.assign_layout.setVisibility(View.GONE);
            }
            holder.payment_mode.setText(mList.getPayment_method());
            holder.tv_order_id.setText(mList.getSale_id());
            holder.tv_customer_name.setText(mList.getUser_fullname());
            holder.tv_customer_socity.setText(mList.getSocity_name());
            holder.tv_customer_phone.setText(mList.getUser_phone());
            holder.tv_order_date.setText(mList.getOn_date());
            holder.tv_order_time.setText(mList.getDelivery_time_from() + "-" + mList.getDelivery_time_to());

            holder.confirm_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressDialog.show();

                    confirm(mList.getSale_id(),holder.pick_uporder,holder.confirm_order);

                }
            });

            holder.pick_uporder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressDialog.show();

                    pickup(mList.getSale_id(),holder.pick_uporder,holder.confirm_order);
                }
            });
            holder.cancel_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressDialog.show();

                    cancel(mList.getSale_id());
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return modelList.size();
        }
        private void confirm(String saleid, final Button pickup, final Button confim) {
            String tag_json_obj = "json_login_req";
            Map<String, String> params = new HashMap<String, String>();
            params.put("sale_id", saleid);

            CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                    BaseURL.Confimed_url, params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());

                    try {
                        Boolean status = response.getBoolean("status");
                        if (status) {

                            progressDialog.dismiss();

                            confim.setVisibility(View.GONE);
                            pickup.setVisibility(View.VISIBLE);

                           getTodayOrders();
                        } else {

                            progressDialog.dismiss();

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
                        Toast.makeText(context, context.getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }

        private void pickup(String saleid, final Button pickup, final Button confirmm) {
            String tag_json_obj = "json_login_req";
            Map<String, String> params = new HashMap<String, String>();
            params.put("sale_id", saleid);

            CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                    BaseURL.pickup_url, params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());

                    try {
                        Boolean status = response.getBoolean("status");

                        if (status) {

                            progressDialog.dismiss();
                            pickup.setVisibility(View.GONE);
                            confirmm.setVisibility(View.GONE);
                            getTodayOrders();
                        } else {
                            progressDialog.dismiss();
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
                        Toast.makeText(context, context.getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                    }
                }

            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }
        private void cancel(String saleid) {
            String tag_json_obj = "json_login_req";
            Map<String, String> params = new HashMap<String, String>();
            params.put("sale_id", saleid);

            CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                    BaseURL.cancel_url, params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());

                    try {
                        Boolean status = response.getBoolean("status");
                        if (status) {
                            progressDialog.dismiss();
                            getTodayOrders();
                        } else {
                            progressDialog.dismiss();
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
                        Toast.makeText(context, context.getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }


    }


}


