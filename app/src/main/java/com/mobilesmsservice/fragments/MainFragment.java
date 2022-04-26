package com.mobilesmsservice.fragments;


import android.app.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.mobilesmsservice.R;
import com.mobilesmsservice.helper.MessagesDatabaseHelper;




public class MainFragment extends Fragment {

    private View view;
    private MessagesDatabaseHelper db;
    private Activity activity;
    private LinearLayout apiSettingsOpenBtn,whatsappSettingsOpenBtn,smsSettingsOpenBtn,accountManageOpenBtn;
    private NavController navController;

    public MainFragment() { }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }
    private void init(){
        initNavigation();
        initButtons();
        activity = getActivity();

    }

    private void initNavigation() {
        FragmentActivity activity = getActivity();
        if (activity!=null){
            NavHostFragment navHostFragment =
                    (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_main_fragment);
            if (navHostFragment!=null){
                navController = navHostFragment.getNavController();
            }
        }
    }

    private void initButtons() {
        apiSettingsOpenBtn = view.findViewById(R.id.open_api_settings_btn);
        apiSettingsOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navController!=null){
                    navController.navigate(R.id.action_mainFragment_to_apiFragment);
                }
            }
        });

        whatsappSettingsOpenBtn = view.findViewById(R.id.whatsapp_settings_open_btn);
        whatsappSettingsOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navController!=null){
                    navController.navigate(R.id.action_mainFragment_to_whatsappManageFragment);
                }
            }
        });
        smsSettingsOpenBtn = view.findViewById(R.id.sms_settings_open_btn);
        smsSettingsOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navController!=null){
                    navController.navigate(R.id.action_mainFragment_to_smsManageFragment);
                }
            }
        });
        accountManageOpenBtn = view.findViewById(R.id.account_manage_open_btn);
        accountManageOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navController!=null){
                    navController.navigate(R.id.action_mainFragment_to_accountManageFragment);
                }
            }
        });
        view.findViewById(R.id.web_manage_open_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navController!=null){
                    navController.navigate(R.id.action_mainFragment_to_webManageFragment);
                }
            }
        });
    }


}