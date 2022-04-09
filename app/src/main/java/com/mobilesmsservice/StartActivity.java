package com.mobilesmsservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import com.mobilesmsservice.AuthPage.AuthActivity;
import com.mobilesmsservice.helper.MessagingService;

public class StartActivity extends AppCompatActivity {


    private static final int REQUEST_CODE = 0;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;
    private Button send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ComponentName componentName = new ComponentName(
                getApplicationContext(),
                MessagingService.class);

        getApplicationContext().getPackageManager().setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        SharedPreferences sp=getSharedPreferences("Login", MODE_PRIVATE);
        String email=sp.getString("email", null);
        if (email==null){
            startActivity(new Intent(StartActivity.this, AuthActivity.class));
            StartActivity.this.finish();
        }else{
            startActivity(new Intent(StartActivity.this,MainActivity.class));
            StartActivity.this.finish();
        }

    }

}