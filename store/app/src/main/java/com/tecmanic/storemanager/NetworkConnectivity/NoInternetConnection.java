package com.tecmanic.storemanager.NetworkConnectivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tecmanic.storemanager.MainActivity;
import com.tecmanic.storemanager.R;

/**
 * Created by Krishna Sharma on 27-Nov-17.
 */

public class NoInternetConnection extends AppCompatActivity {
    ImageView mCheckConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_no_internet_connection);
        mCheckConnection = (ImageView) findViewById(R.id.no_internet_connection);
        mCheckConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkConnection.connectionChecking(getApplicationContext())) {
                    Intent intent = new Intent(NoInternetConnection.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),"Check your connection.Try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
