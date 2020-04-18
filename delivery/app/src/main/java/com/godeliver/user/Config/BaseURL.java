package com.godeliver.user.Config;

/**
 * Created by Rajesh Dabhi on 22/6/2017.
 */

public class BaseURL {
    static final String APP_NAME = "OneTwoZoneDeliver";
    public static final String PREFS_NAME = "GroceryLoginPrefs";
    public static final String PREFS_NAME2 = "GroceryLoginPrefs2";
    public static final String IS_LOGIN = "isLogin";
    public static final String KEY_NAME = "user_fullname";
    public static final String KEY_ID = "user_id";
    public static final String KEY_ORDER_ID = "ORDER_ID";

    public static String BASE_URL = "http://api.vegmonk.in";
    public static String Loction = "http://api.vegmonk.in/store/index.php/api/update_location";
    public static String GET_ORDER_URL=  BASE_URL + "index.php/api/delivery_boy_order";

    public static String check_Status=  BASE_URL + "index.php/api/change_boy_status";
    public static String pickup=  BASE_URL + "index.php/api/pickup";
    public static String GET_DELIVERD_ORDER_URL = BASE_URL + "index.php/api/delivered_complete";

    public static String LOGIN = BASE_URL+"index.php/api/delivery_boy_login";

    public static String OrderDetail = BASE_URL + "index.php/api/order_details";

    public static String IMG_PRODUCT_URL = BASE_URL + "uploads/products/";
    public static String JSON_RIGISTER_FCM = BASE_URL +"index.php/api/get_deliveryboy_deviceid";

    public static final String urlUpload = "http://api.vegmonk.in/index.php/api/mark_delivered2";
    public static final int REQCODE = 100;
    public static final String image = "signature";
    public static final String imageName = "id";

}
