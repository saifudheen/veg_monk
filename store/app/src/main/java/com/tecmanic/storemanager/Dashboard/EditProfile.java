package com.tecmanic.storemanager.Dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.bumptech.glide.Glide;
import com.tecmanic.storemanager.AppController;
import com.tecmanic.storemanager.Config.BaseURL;
import com.tecmanic.storemanager.MainActivity;
import com.tecmanic.storemanager.Model.edit_model;
import com.tecmanic.storemanager.R;
import com.tecmanic.storemanager.util.CustomVolleyJsonRequest;
import com.tecmanic.storemanager.util.Session_management;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfile extends AppCompatActivity  {
    private static String TAG = EditProfile.class.getSimpleName();

    private EditText et_phone, et_name, et_email, et_house;
    private RelativeLayout btn_update;
    private TextView tv_phone, tv_name, tv_email, tv_house, tv_socity, btn_socity;
    private ImageView iv_profile;
    SharedPreferences myPrefrence;
    private String getsocity = "";
    List<edit_model> edit_models =new ArrayList<>();

    private String filePath = "";
    private static final int GALLERY_REQUEST_CODE1 = 201;
    private Bitmap bitmap;
    private Uri imageuri;

    public String getuserid;

    String user_id;
    String email, user_fullname, user_mobile;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor1;

    String email1 , phone, name ,userid;
    String image;
    private Session_management sessionManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        sharedPreferences = getSharedPreferences("personinfo", MODE_PRIVATE);
        editor1 = sharedPreferences.edit();

        user_id = sharedPreferences.getString("user_id", "");

        sessionManagement = new Session_management(getApplicationContext());
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("Edit Profile");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfile.this, MainActivity.class);
                startActivity(intent);
            }
        });

        et_phone = (EditText) findViewById(R.id.et_pro_phone);
        et_name = (EditText) findViewById(R.id.et_pro_name);
//        tv_phone = (TextView) findViewById(R.id.tv_pro_phone);
//        tv_name = (TextView) findViewById(R.id.tv_pro_name);
     //   tv_email = (TextView) findViewById(R.id.tv_pro_email);
        et_email = (EditText) findViewById(R.id.et_pro_email);
        iv_profile = (ImageView) findViewById(R.id.iv_pro_img);
        btn_update = (RelativeLayout) findViewById(R.id.btn_pro_edit);

        getuserid =sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
        String getemail = sessionManagement.getUserDetails().get(BaseURL.KEY_EMAIL);
        String getimage = sessionManagement.getUserDetails().get(BaseURL.KEY_IMAGE);
        String getname = sessionManagement.getUserDetails().get(BaseURL.KEY_NAME);
        String getphone = sessionManagement.getUserDetails().get(BaseURL.KEY_MOBILE);
        String getpin = sessionManagement.getUserDetails().get(BaseURL.KEY_PINCODE);
        String gethouse = sessionManagement.getUserDetails().get(BaseURL.KEY_HOUSE);
        getsocity = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_ID);
        String getsocity_name = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_NAME);


        et_name.setText(getname);
        et_phone.setText(getphone);
        et_email.setText(getemail);





        if (!TextUtils.isEmpty(getimage)) {
            Glide.with(this)
                    .load(BaseURL.IMG_PROFILE_URL + getimage)
                    .centerCrop()
                    .placeholder(R.drawable.icons)
                    .crossFade()
                    .into(iv_profile);
        }
        if (!TextUtils.isEmpty(getemail)) {
            et_email.setText(getemail);
        }
//        btn_update.setOnClickListener(this);
//        iv_profile.setOnClickListener(this);


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                email1 = et_email.getText().toString();
                phone = et_phone.getText().toString();
                name = et_name.getText().toString();


                edit(email1,phone,name);

                startActivity(new Intent(getApplicationContext(),MainActivity.class));

            }
        });
    }

//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        if (id == R.id.btn_pro_edit) {
//            //  attemptEditProfile();
//            // storeImage(bitmap);
//        } else if (id == R.id.iv_pro_img) {
//            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            // Start the Intent
//            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE1);
//        }
//    }

    private void edit(String email1 , String phone ,String name) {

        String tag_json_obj = "json_login_req";

        Map<String, String> params = new HashMap<String, String>();

        params.put("user_email", email1);

        params.put("user_id", getuserid);

        params.put("user_fullname", name);

        params.put("user_mobile", phone);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.edit, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    Boolean status = response.getBoolean("responce");

                    if (status) {

                        JSONObject obj = response.getJSONObject("data");

                        String user_id = obj.getString("user_id");

                        String user_fullname = obj.getString("user_fullname");

                        String user_email = obj.getString("user_email");

                        String user_phone = obj.getString("user_phone");

                        sessionManagement.updateData(user_fullname,user_phone,"",user_email,"","","","","");

                        Toast.makeText(EditProfile.this, "Your Profile is Change", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(EditProfile.this, "profile not chnaged", Toast.LENGTH_SHORT).show();

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

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}