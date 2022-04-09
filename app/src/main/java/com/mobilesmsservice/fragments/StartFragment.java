package com.mobilesmsservice.fragments;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobilesmsservice.R;
import com.mobilesmsservice.helper.PermissionUtils;


public class StartFragment extends Fragment {

    public StartFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PermissionUtils permissionUtils = new PermissionUtils();
        if(permissionUtils.hasThisPermission(getContext(), Manifest.permission.SEND_SMS)){
            FragmentActivity activity = getActivity();
            if (activity!=null){
                NavHostFragment navHostFragment =
                        (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_main_fragment);
                if (navHostFragment!=null){
                    NavController navController = navHostFragment.getNavController();
                    navController.navigate(R.id.action_startFragment_to_smsSetUpFragment);
                }
            }
        }else {
            FragmentActivity activity = getActivity();
            if (activity!=null){
                NavHostFragment navHostFragment =
                        (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_main_fragment);
                if (navHostFragment!=null){
                    NavController navController = navHostFragment.getNavController();
                    navController.navigate(R.id.action_startFragment_to_mainFragment);
                }
            }
        }
    }
}