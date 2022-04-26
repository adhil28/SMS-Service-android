package com.mobilesmsservice.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mobilesmsservice.QrScannerActivity;
import com.mobilesmsservice.R;
import com.mobilesmsservice.helper.Fcm;
import com.mobilesmsservice.helper.MessagesDatabaseHelper;
import com.mobilesmsservice.helper.PermissionUtils;
import com.mobilesmsservice.helper.Server;
import com.mobilesmsservice.helper.WhatsappDatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Callback;


public class WebManageFragment extends Fragment {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "WebManageFragment";
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        String token = data.getData().toString();
                        SharedPreferences sp = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
                        sp.edit().putString("web", token).apply();

                        onScanned();

                        //sendNotification(token,smsMessages,whatsAppMessages,sp.getString("token",null));
                    }
                }
            });

    private void onScanned() {
        SharedPreferences sp = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        JSONObject data = new JSONObject();
        try {
            data.put("sender",sp.getString("token", null));
            new Fcm(getContext()).sendFcm(data, sp.getString("web", null), new Fcm.onSend() {
                @Override
                public void success(String uid) {

                }

                @Override
                public void failure(String msg) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    ActivityResultLauncher<String> permissionResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    Intent intent = new Intent(getContext(), QrScannerActivity.class);
                    activityResultLauncher.launch(intent);
                }
            });
    private View view;

    public WebManageFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_manage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        init();
    }

    private void init() {
        initButtons();
    }

    private void initButtons() {
        view.findViewById(R.id.scan_and_connect_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PermissionUtils permissionUtils = new PermissionUtils();
                if (!permissionUtils.hasThisPermission(getContext(), Manifest.permission.CAMERA)) {
                    Intent intent = new Intent(getContext(), QrScannerActivity.class);
                    activityResultLauncher.launch(intent);
                } else {
                    permissionResultLauncher.launch(Manifest.permission.CAMERA);
                }

            }
        });
    }

}
