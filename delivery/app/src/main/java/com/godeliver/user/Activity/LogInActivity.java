package com.godeliver.user.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.godeliver.user.Config.BaseURL;
import com.godeliver.user.MainActivity;
import com.godeliver.user.R;
import com.godeliver.user.util.Session_management;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class LogInActivity extends AppCompatActivity {
    EditText Et_login_email;
    RelativeLayout Btn_Sign_in;
    TextView tv_login_email;
    String getemail;
    ProgressDialog progressDialog;
    SharedPreferences preferences ;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_log_in);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading");

        preferences=getSharedPreferences("person",MODE_PRIVATE);
        editor=preferences.edit();
//        String token1 = FirebaseInstanceId.getInstance().getToken();
//        String token = SharedPref.getString(LogInActivity.this,SharedPrefManager.getInstance(LogInActivity.this).getDeviceToken());
        Et_login_email = (EditText) findViewById(R.id.et_login_email);
        tv_login_email = (TextView) findViewById(R.id.tv_login_email);
        Btn_Sign_in = (RelativeLayout) findViewById(R.id.btn_Sign_in);

        getemail = Et_login_email.getText().toString();

        Btn_Sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Et_login_email.equals("")) {
                    Toast.makeText(LogInActivity.this, "Please Put Your Currect Password", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    makeLoginRequest();

                }
            }
        });


    }


    private void makeLoginRequest() {
        final String UserName = Et_login_email.getText().toString().trim();
        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest postReq = new StringRequest(Request.Method.POST, BaseURL.LOGIN,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("eclipse", "Response=" + response);
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            if (obj.getString("responce").equals("true")) {
                                JSONArray jsonArray = obj.getJSONArray("product");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String user_id = jsonObject.getString("id");
                                    String user_fullname = jsonObject.getString("user_name");
                                    editor.putString( "userid",user_id);
                                    editor.commit();

                                    Session_management sessionManagement = new Session_management(LogInActivity.this);
                                    sessionManagement.createLoginSession(user_id, user_fullname);
                                    MyFirebaseRegister myFirebaseRegister=new MyFirebaseRegister(LogInActivity.this);
                                    myFirebaseRegister.RegisterUser(user_id);
                                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    Btn_Sign_in.setEnabled(false);
progressDialog.dismiss();
                                    finish();
                                }
                            } else {
                                progressDialog.dismiss();
                                Btn_Sign_in.setEnabled(true);
                                Toast.makeText(LogInActivity.this, "Please Put Your Currect Email-Id", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error [" + error + "]");

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_password", UserName);
                return params;
            }
        };
        rq.add(postReq);


    }




}
