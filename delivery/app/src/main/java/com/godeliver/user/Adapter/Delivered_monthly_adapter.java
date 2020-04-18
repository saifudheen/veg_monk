package com.godeliver.user.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.godeliver.user.Activity.OrderDetail;
import com.godeliver.user.Model.Monthly_model;
import com.godeliver.user.R;

import java.util.List;

public class Delivered_monthly_adapter extends RecyclerView.Adapter<Delivered_monthly_adapter.MyViewHolder> {

    private List<Monthly_model> modelList;
    private LayoutInflater inflater;
    private Fragment currentFragment;

    private Context context;

    public Delivered_monthly_adapter(Context context, List<Monthly_model> modemodelList, final Fragment currentFragment) {

        this.context = context;
        this.modelList = modemodelList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.currentFragment = currentFragment;

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_orderno, tv_status, tv_date, tv_time, tv_price, tv_item, relativetextstatus, tv_tracking_date, tv_socity,
                tv_recivername, tv_house,tv_storename;

        ImageView call;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            tv_storename=view.findViewById(R.id.store_name);

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
            cardView = view.findViewById(R.id.card_view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
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
                }
            });


        }
    }

    public Delivered_monthly_adapter(List<Monthly_model> modelList) {
        this.modelList = modelList;
    }

    @Override
    public Delivered_monthly_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_delivered_history, parent, false);


        context = parent.getContext();

        return new Delivered_monthly_adapter.MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(Delivered_monthly_adapter.MyViewHolder holder, int position) {
        final Monthly_model mList = modelList.get(position);


        try {


            holder.tv_orderno.setText(mList.getSale_id());



        } catch (Exception e) {

        }

        holder.tv_date.setText(mList.getOn_date());
        holder.tv_tracking_date.setText(mList.getOn_date());
        holder.tv_time.setText(mList.getDelivery_time_from() + "-" + mList.getDelivery_time_to());
        holder.tv_price.setText(context.getResources().getString(R.string.currency) + mList.getTotal_amount());
        holder.tv_item.setText(context.getResources().getString(R.string.tv_cart_item) + mList.getTotal_items());
        holder.tv_socity.setText(mList.getSocityname());
        holder.tv_house.setText(mList.getHouse());
        holder.tv_recivername.setText(modelList.get(position).getRecivername());
        holder.tv_storename.setText(mList.getStore_name());

    }


    @Override
    public int getItemCount() {
        return modelList.size();

    }



}

