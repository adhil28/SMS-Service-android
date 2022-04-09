package com.mobilesmsservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;

import com.mobilesmsservice.helper.Constants;

public class MainActivity extends AppCompatActivity {

    ActivityResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public interface ActivityResult {
        public void onPermissionResult(Boolean granted);
    }
    public void getPermissionResult(ActivityResult result){
        this.result = result;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (result!=null && requestCode == new Constants().getSendSmsRequestCode()){
            result.onPermissionResult(grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
    }
}