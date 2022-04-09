package com.mobilesmsservice.fragments;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mobilesmsservice.MainActivity;
import com.mobilesmsservice.R;
import com.mobilesmsservice.helper.AlertBox;
import com.mobilesmsservice.helper.Constants;
import com.mobilesmsservice.helper.PermissionUtils;


public class SmsSetUpFragment extends Fragment {

    private View view;

    public SmsSetUpFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        initSetupSmsBtn();
        initActivityResult();
    }

    private void initActivityResult() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity!=null){
            activity.getPermissionResult(granted -> {
                if (granted){
                    NavHostFragment navHostFragment =
                            (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_main_fragment);
                    if (navHostFragment!=null){
                        NavController navController = navHostFragment.getNavController();
                        navController.navigate(R.id.mainFragment);
                    }
                }else{
                    new AlertBox(getActivity()).show("Alert","Send sms permission is needed to activate SMS service");
                }
            });
        }
    }

    private void initSetupSmsBtn() {
        Button setUpSmsServiceBtn = view.findViewById(R.id.set_up_sms_btn);
        setUpSmsServiceBtn.setOnClickListener(view -> {
            PermissionUtils permissionUtils = new PermissionUtils();
            if(permissionUtils.hasThisPermission(getContext(), Manifest.permission.SEND_SMS)){
                permissionUtils.requestPermission(getActivity(),Manifest.permission.SEND_SMS,new Constants().getSendSmsRequestCode());
            }
        });
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sms_set_up, container, false);
        return view;
    }
}