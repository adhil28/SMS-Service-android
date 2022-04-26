package com.mobilesmsservice.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilesmsservice.R;
import com.mobilesmsservice.helper.AlertBox;
import com.mobilesmsservice.helper.AppFunctions;
import com.mobilesmsservice.helper.ClipBoard;
import com.mobilesmsservice.helper.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ApiFragment extends Fragment {
    private View view;
    private TextView apiTv,apiActiveTextTv;
    private ImageView apiActiveIconIv;
    private LinearLayout copyApiButton,revokeApiBtn,disableApiBtn,enableAuthBtn;
    private ProgressDialog pd;
    private AppFunctions.Api appFunctionsApi;
    private SharedPreferences sp ;

    public ApiFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_api, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        appFunctionsApi = new AppFunctions.Api();
        sp = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        apiTv = view.findViewById(R.id.api_key);
        initActiveInfo();
        apiTv.setText("API : "+getApi());
        pd = new ProgressDialog(getContext());
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        initButton();
    }

    private void initActiveInfo() {
        apiActiveTextTv = view.findViewById(R.id.api_active_status_info_text);
        apiActiveIconIv = view.findViewById(R.id.api_active_status_info_icon);
        SharedPreferences sp=getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        if (sp.getString("active", null).equals("true")){
            apiActiveIconIv.setImageResource(R.drawable.circle_green_icon_foreground);
            apiActiveTextTv.setText("Active");
        }else{
            apiActiveIconIv.setImageResource(R.drawable.circle_red_icon_foreground);
            apiActiveTextTv.setText("Disabled");
        }
    }

    private void initButton() {
        initCopyApiButton();
        revokeApiBtn = view.findViewById(R.id.revoke_api_btn);
        revokeApiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setMessage("Revoking APi...");
                pd.show();
                appFunctionsApi.revokeApi(getContext(), new AppFunctions.onComplete() {
                    @Override
                    public void onSuccess(Response response) {
                        pd.dismiss();
                        if (response.code() == 200) {
                            try {
                                JSONObject data = new JSONObject(response.body().string());
                                sp.edit().putString("api", data.getString("apiKey")).apply();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            apiTv.setText("API :" + data.getString("apiKey"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertBox(getContext()).show("Error", e.getMessage());
                                    }
                                });
                            }
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertBox(getContext()).show("Error", "Server error please try again");
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        pd.dismiss();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertBox(getContext()).show("Error", e.getMessage());
                            }
                        });
                    }
                });
            }
        });
        disableApiBtn = view.findViewById(R.id.disable_api_btn);
        disableApiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setMessage("Disabling APi...");
                pd.show();
                appFunctionsApi.toggleApi(getContext(), new AppFunctions.onComplete() {
                    @Override
                    public void onSuccess(Response response) {
                        pd.dismiss();
                        if (response.code()==200){
                            try {
                                JSONObject res = new JSONObject(response.body().string());
                                sp.edit().putString("active",res.getString("active")).apply();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        initActiveInfo();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }else{
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertBox(getContext()).show("Error","Server error please try again");
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        pd.dismiss();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertBox(getContext()).show("Error",e.getMessage());
                            }
                        });
                    }
                });
            }
        });
        enableAuthBtn = view.findViewById(R.id.enable_auth_btn);
        enableAuthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertBox(getContext()).showWithCustomView(LayoutInflater.from(getContext()).inflate(R.layout.auth_model_view,null,false),"Enable/Disable credential");
                EditText userNameTv = dialog.findViewById(R.id.username),passwordTv = dialog.findViewById(R.id.password);
                dialog.findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pd.setMessage("Enabling authentication");
                        pd.show();
                        JSONObject data = new JSONObject();
                        try {
                            data.put("api",sp.getString("api",null));
                            data.put("userName",userNameTv.getText().toString());
                            data.put("password",passwordTv.getText().toString());
                            appFunctionsApi.enableAuth(getContext(),data, new AppFunctions.onComplete() {
                                @Override
                                public void onSuccess(Response response) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), "Credential set successfully", Toast.LENGTH_SHORT).show();
                                            pd.dismiss();
                                            dialog.dismiss();
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    pd.dismiss();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new AlertBox(getContext()).show("Error",e.getMessage());
                                        }
                                    });
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void initCopyApiButton() {
        copyApiButton = view.findViewById(R.id.copy_api_btn);
        copyApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ClipBoard(getContext()).copy(getApi());
                Toast.makeText(getContext(), "Api key successfully copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getApi(){
        SharedPreferences sp=getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        return sp.getString("api", null);
    }
}