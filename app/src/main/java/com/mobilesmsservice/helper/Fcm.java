package com.mobilesmsservice.helper;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Fcm {
    Context context;
    private static final String Legacy_SERVER_KEY = "AAAAJ1kq0HA:APA91bGEZA_Sh1Rrix4sNPVyG7JKv2TLtlHTPa45cuEcedoxABfRUSOoDYr0e5Pytz6d6UtEl_D8pSNUw7QD_Ubn1ire-gnFklpWri-G9cortMWg40NQhIdwcPVSHsHLZbK7xNjKZuFn";

    public Fcm(Context context) {
        this.context = context;
    }
    public interface onSend {
        void success(String uid);
        void failure(String msg);
    }

    public void sendFcm(JSONObject data, String token, onSend onSend){
        JSONObject options = new JSONObject();
        try {
            options.put("to",token);
            options.put("data",data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", options,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        onSend.success(response.toString());
                        Log.e("!_@@_SUCESS", response + "");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onSend.failure(error.getMessage());
                        Log.e("!_@@_Errors--", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }
}
