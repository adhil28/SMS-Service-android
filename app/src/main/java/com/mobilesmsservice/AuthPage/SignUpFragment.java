package com.mobilesmsservice.AuthPage;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mobilesmsservice.MainActivity;
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

public class SignUpFragment extends Fragment {


    private View view;

    private Button submit_btn;
    private TextView email_tv, password_tv, name_tv, openLoginPageBtn;
    private ProgressDialog pd;
    private SharedPreferences sp;

    public SignUpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        init();
    }

    private void run() {
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setMessage("Creating account");
                pd.show();
                String email = email_tv.getText().toString(), password = password_tv.getText().toString(), name = name_tv.getText().toString();
                JSONObject jsonString = new JSONObject();
                try {
                    jsonString.put("email", email);
                    jsonString.put("password", password);
                    jsonString.put("name", name);

                    FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            sp.edit().putString("token", s).apply();

                            try {
                                jsonString.put("token", s);
                                new Server().post(new Server().getBaseUrl() + "sign-up", jsonString.toString(), new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        pd.dismiss();
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        SharedPreferences.Editor Ed = sp.edit();
                                        try {
                                            JSONObject data = new JSONObject(response.body().string());
                                            if (data.getInt("code") == 201) {
                                                pd.dismiss();
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        new AlertBox(getActivity()).show("Account already exist", "The entered email is already used by someone else. pick a new one");
                                                    }
                                                });
                                            } else {

                                                new FirebaseLogin(getActivity()).signUp(email, password, new FirebaseLogin.onLoggedIn() {
                                                    @Override
                                                    public void success(String uid) {
                                                        try {
                                                            Ed.putString("email", data.getString("email"));
                                                            Ed.putString("api", data.getString("apiKey"));
                                                            Ed.putString("active", data.getString("active"));
                                                            Ed.apply();
                                                            pd.dismiss();
                                                            startActivity(new Intent(getActivity(), MainActivity.class));
                                                            getActivity().finish();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    @Override
                                                    public void failure(String msg) {
                                                        new AlertBox(getActivity()).show("Error", msg);
                                                    }
                                                });

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        openLoginPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    NavHostFragment navHostFragment =
                            (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_main_fragment);
                    if (navHostFragment != null) {
                        NavController navController = navHostFragment.getNavController();
                        navController.navigate(R.id.action_signUpFragment_to_loginFragment);
                    }
                }
            }
        });
    }

    private void init() {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        submit_btn = view.findViewById(R.id.submit_btn);
        email_tv = view.findViewById(R.id.email_tv);
        password_tv = view.findViewById(R.id.password_tv);
        name_tv = view.findViewById(R.id.name_tv);
        pd = new ProgressDialog(getContext());
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        sp = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        openLoginPageBtn = view.findViewById(R.id.login_page_open_btn);
        run();
    }
}