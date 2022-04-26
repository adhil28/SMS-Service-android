package com.mobilesmsservice.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mobilesmsservice.AuthPage.AuthActivity;
import com.mobilesmsservice.R;
import com.mobilesmsservice.helper.AlertBox;
import com.mobilesmsservice.helper.FirebaseLogin;
import com.mobilesmsservice.helper.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AccountManageFragment extends Fragment {

    private View view;
    private LinearLayout logoutBtn,deleteAccountBtn;
    private ProgressDialog pd;

    public AccountManageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_manage, container, false);
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
        pd = new ProgressDialog(getContext());
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        initButtons();
    }

    private void initButtons() {
        logoutBtn = view.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FirebaseLogin(getActivity()).logOut();
                SharedPreferences sp=getActivity().getSharedPreferences("Login", MODE_PRIVATE);
                sp.edit().clear().apply();
                startActivity(new Intent(getContext(), AuthActivity.class));
                getActivity().finish();
            }
        });
        deleteAccountBtn = view.findViewById(R.id.delete_account_btn);
        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertBox(getContext()).showWithButtonCallback("Confirmation", "Are you sure want to delete your account.\nNb:-This is irreversible", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pd.setMessage("Deleting account from server");
                        pd.show();
                        new FirebaseLogin(getActivity()).deleteAccount(new FirebaseLogin.onResult() {
                            @Override
                            public void onSuccess() {
                                Server server = new Server();
                                JSONObject data = new JSONObject();
                                SharedPreferences sp=getActivity().getSharedPreferences("Login", MODE_PRIVATE);
                                String email=sp.getString("email", null);
                                try {
                                    data.put("email",email);
                                    server.post(server.getBaseUrl()+"delete-account", data.toString(), new Callback() {
                                        @Override
                                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                            pd.dismiss();
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new AlertBox(getContext()).show("Error","Failed to delete account\n"+e.getLocalizedMessage());
                                                }
                                            });
                                        }

                                        @Override
                                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                            pd.dismiss();
                                            if (response.code()==200){
                                                sp.edit().clear().apply();
                                                startActivity(new Intent(getContext(),AuthActivity.class));
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                getActivity().finish();
                                            }else{
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        new AlertBox(getContext()).show("Error","Failed to delete account");
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    pd.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                new AlertBox(getContext()).show("Error",e.getMessage());
                            }
                        });


                    }
                });
            }
        });
    }

}