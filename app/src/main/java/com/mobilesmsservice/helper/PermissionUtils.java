package com.mobilesmsservice.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class PermissionUtils {

    public  final int REQUEST_GROUP_STORAGE = 1508;

    public PermissionUtils() {
    }

    public  boolean hasThisPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           return ActivityCompat.checkSelfPermission(context,permission)
                   != PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    public  boolean requestPermission(Activity activity, String permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity,new String[]{permission},
                    requestCode);
        }
        return false;
    }
}