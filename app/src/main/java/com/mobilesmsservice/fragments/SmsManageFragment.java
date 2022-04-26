package com.mobilesmsservice.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mobilesmsservice.Adapters.MessagesRecyclerAdapter;
import com.mobilesmsservice.R;
import com.mobilesmsservice.helper.AlertBox;
import com.mobilesmsservice.helper.MessagesDatabaseHelper;
import com.mobilesmsservice.helper.PermissionUtils;
import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SmsManageFragment extends Fragment {


    private View view;
    private RecyclerView messagesRv;
    private LinearLayout smsServiceButton;

    public SmsManageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sms_manage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        initMessagesRv();
        initGraph();
        initButtons();
    }

    private void initButtons() {
        smsServiceButton = view.findViewById(R.id.sms_service_button);
        smsServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertBox(getContext()).showWithButtonCallback("Disable permission", "You have to disable SMS permission in App settings to stop this service", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", "com.mobilesmsservice", null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void initGraph() {
        SparkView sparkView = (SparkView) view.findViewById(R.id.sparkview);
        JSONArray datas = new MessagesDatabaseHelper(getContext()).getAllData();
        float[] graphData = new float[datas.length()];
        //2022-04-09 15:10:13
        for (int i = 0; i < datas.length()-1; i++) {
            try {
                if (datas.getJSONObject(i+1)!=null){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date date1 = dateFormat.parse(datas.getJSONObject(i).getString("date"));
                        Date date2 = dateFormat.parse(datas.getJSONObject(i+1).getString("date"));
                        graphData[i] =date2.getTime()-date1.getTime();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        sparkView.setAdapter(new SmsManageFragment.MyAdapter(graphData));
    }
    public class MyAdapter extends SparkAdapter {
        private float[] yData;

        public MyAdapter(float[] yData) {
            this.yData = yData;
        }

        @Override
        public int getCount() {
            return yData.length;
        }

        @Override
        public Object getItem(int index) {
            return yData[index];
        }

        @Override
        public float getY(int index) {
            return yData[index];
        }
    }

    private void initMessagesRv() {
        messagesRv = view.findViewById(R.id.messages_recycler_view);
        messagesRv.setLayoutManager(new LinearLayoutManager(getContext()));
        messagesRv.setHasFixedSize(true);
        JSONArray messages = new MessagesDatabaseHelper(getContext()).getAllData();
        messagesRv.setAdapter(new MessagesRecyclerAdapter(messages,messages.length()));
        initializeBroadCastReceiver();
    }
    private void initializeBroadCastReceiver() {
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Activity activity = getActivity();
                if (activity!=null){
                    activity.runOnUiThread(() -> {
                        initMessagesRv();
                        initGraph();
                    });
                }
            }
        };
        getActivity().registerReceiver(receiver,new IntentFilter("sms_receiver"));
    }

    @Override
    public void onStart() {
        super.onStart();
        PermissionUtils permissionUtils = new PermissionUtils();
        if(permissionUtils.hasThisPermission(getContext(), Manifest.permission.SEND_SMS)){
            FragmentActivity activity = getActivity();
            if (activity!=null){
                NavHostFragment navHostFragment =
                        (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_main_fragment);
                if (navHostFragment!=null){
                    NavController navController = navHostFragment.getNavController();
                    navController.navigate(R.id.smsSetUpFragment);
                }
            }
        }
    }
}