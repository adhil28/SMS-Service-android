package com.mobilesmsservice.helper;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Server {
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public Fcm retrofitInterface;
    OkHttpClient client = new OkHttpClient();
    String baseUrl = "https://rogue-omniscient-plot.glitch.me/";

    public Server() {
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Call post(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }




}
