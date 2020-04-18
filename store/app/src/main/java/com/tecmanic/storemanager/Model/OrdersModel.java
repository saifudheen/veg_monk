package com.tecmanic.storemanager.Model;

public class OrdersModel {

    public String sale_id;
    public String user_id;
    public String on_date;
    public String delivery_time_from;
    public String delivery_time_to;
    public String status;
    public String note;
    public String is_paid;
    public String total_amount;
    public String total_rewards;
    public String total_kg;
    public String total_items;
    public String socity_id;
    public String delivery_address;
    public String location_id;
    public String delivery_charge;
    public String new_store_id;
    public String assign_to;
    public String payment_method;

    public OrdersModel() {

    }



    public OrdersModel( String sale_id, String user_id, String on_date, String delivery_time_from, String delivery_time_to, String status, String note, String is_paid,
             String total_amount,
             String total_kg,
             String total_items,
             String socity_id, String delivery_address, String location_id,
             String delivery_charge, String new_store_id,
             String assign_to,
             String user_fullname,
             String user_phone,
             String pincode,
             String house_no,
             String socity_name,
             String receiver_name,
             String receiver_mobile) {
        this.user_id = user_id;
        this.on_date = on_date;
        this.delivery_time_from = delivery_time_from;
        this.delivery_time_to = delivery_time_to;
        this.status = status;
        this.note = note;
        this.is_paid=is_paid;
        this.total_amount = total_amount;
        this.total_kg = total_kg;
        this.total_items = total_items;
        this.socity_id = socity_id;
        this.delivery_address = delivery_address;
        this.location_id = location_id;
        this.delivery_charge = delivery_charge;
        this.new_store_id = new_store_id;
        this.assign_to = assign_to;
        this.new_store_id = new_store_id;

    }


    public String getSale_id() {
        return sale_id;
    }

    public void setSale_id(String sale_id) {
        this.sale_id = sale_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOn_date() {
        return on_date;
    }

    public void setOn_date(String on_date) {
        this.on_date = on_date;
    }

    public String getDelivery_time_from() {
        return delivery_time_from;
    }

    public void setDelivery_time_from(String delivery_time_from) {
        this.delivery_time_from = delivery_time_from;
    }

    public String getDelivery_time_to() {
        return delivery_time_to;
    }

    public void setDelivery_time_to(String delivery_time_to) {
        this.delivery_time_to = delivery_time_to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getIs_paid() {
        return is_paid;
    }

    public void setIs_paid(String is_paid) {
        this.is_paid = is_paid;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getTotal_kg() {
        return total_kg;
    }

    public void setTotal_kg(String total_kg) {
        this.total_kg = total_kg;
    }

    public String getTotal_items() {
        return total_items;
    }

    public void setTotal_items(String total_items) {
        this.total_items = total_items;
    }

    public String getSocity_id() {
        return socity_id;
    }

    public void setSocity_id(String socity_id) {
        this.socity_id = socity_id;
    }

    public String getDelivery_address() {
        return delivery_address;
    }

    public void setDelivery_address(String delivery_address) {
        this.delivery_address = delivery_address;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getDelivery_charge() {
        return delivery_charge;
    }

    public void setDelivery_charge(String delivery_charge) {
        this.delivery_charge = delivery_charge;
    }

    public String getNew_store_id() {
        return new_store_id;
    }

    public void setNew_store_id(String new_store_id) {
        this.new_store_id = new_store_id;
    }

    public String getAssign_to() {
        return assign_to;
    }

    public void setAssign_to(String assign_to) {
        this.assign_to = assign_to;
    }


    public String getTotal_rewards() {
        return total_rewards;
    }

    public void setTotal_rewards(String total_rewards) {
        this.total_rewards = total_rewards;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

}
