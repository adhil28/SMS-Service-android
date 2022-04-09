package com.mobilesmsservice.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilesmsservice.R;
import com.mobilesmsservice.helper.AccessibilityServiceManager;
import com.mobilesmsservice.helper.AlertBox;
import com.mobilesmsservice.helper.MessagesDatabaseHelper;
import com.mobilesmsservice.helper.MyAccessibilityService;
import com.mobilesmsservice.helper.WhatsappDatabaseHelper;
import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;


public class WhatsappManageFragment extends Fragment {


    private View view;
    private LinearLayout startWhatsappServiceButton;
    private ImageView whatsappServiceStatusIcon;
    private TextView whatsappServiceStatusText;

    public WhatsappManageFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_whatsapp_manage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        this.view = view;
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        initializeBroadCastReceiver();
        initGraph();
        initButtons();
        whatsappServiceStatusIcon = view.findViewById(R.id.whatsapp_service_status_icon);
        whatsappServiceStatusText = view.findViewById(R.id.whatsapp_service_status_text);
    }

    private void initButtons() {
        startWhatsappServiceButton = view.findViewById(R.id.start_whatsapp_service_button);
        startWhatsappServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccessibilityServiceManager serviceManager = new AccessibilityServiceManager(getContext());
                if (!serviceManager.hasAccessibilityServicePermission(MyAccessibilityService.class)){
                    new AlertBox(getContext()).showWithButtonCallback("Permission needed", "You have to enable Mobile SMS Service in accessibility to use this feature", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                            getActivity().startActivity(intent);
                            dialogInterface.dismiss();
                        }
                    });
                }else{
                    new AlertBox(getContext()).showWithButtonCallback("Disable MobileSMSService", "You have to disable MobileSMSService in accessibility settings to disable Whatsapp service", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                            getActivity().startActivity(intent);
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (new AccessibilityServiceManager(getContext()).hasAccessibilityServicePermission(MyAccessibilityService.class)){
            whatsappServiceStatusText.setText("Stop service");
            whatsappServiceStatusIcon.setImageResource(R.drawable.block_icon_foreground);
        }
    }

    private void initializeBroadCastReceiver() {
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Activity activity = getActivity();
                if (activity!=null){
                    activity.runOnUiThread(() -> {
                        //initMessagesRv();
                        initGraph();
                    });
                }
            }
        };
        getActivity().registerReceiver(receiver,new IntentFilter("receiver"));
    }

    private void initGraph() {
        SparkView sparkView = (SparkView) view.findViewById(R.id.sparkview);
        JSONArray datas = new WhatsappDatabaseHelper(getContext()).getAllData();
        float[] graphData = new float[datas.length()];
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
        sparkView.setAdapter(new MyAdapter(graphData));

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
}