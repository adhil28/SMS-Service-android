package com.mobilesmsservice.helper;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AppFunctions {
    public interface onComplete{
        void onSuccess(Response response);
        void onFailure(Exception e);
    }
    public static class Api {
        public void revokeApi(Context context, onComplete onComplete) {
            try {
                Server server = new Server();
                JSONObject data = new JSONObject();
                SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);
                data.put("email", sp.getString("email", null));
                server.post(server.getBaseUrl() + "revoke-api", data.toString(), new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        onComplete.onFailure(e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        onComplete.onSuccess(response);
                    }
                });
            } catch (JSONException e) {
                onComplete.onFailure(e);
            }
        }
        public void toggleApi(Context context,onComplete onComplete){
            try {

                Server server = new Server();
                JSONObject data = new JSONObject();
                SharedPreferences sp=context.getSharedPreferences("Login", MODE_PRIVATE);
                data.put("api",sp.getString("api", null));
                server.post(server.getBaseUrl() + "disable-api", data.toString(), new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        onComplete.onFailure(e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        onComplete.onSuccess(response);
                    }
                });
            } catch (JSONException e) {
                onComplete.onFailure(e);
            }
        }
        public void enableAuth(Context context,JSONObject data,onComplete onComplete){
            Server server = new Server();
            try {
                SharedPreferences sp=context.getSharedPreferences("Login", MODE_PRIVATE);

                server.post(server.getBaseUrl() + "enable-auth", data.toString(), new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        onComplete.onFailure(e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        onComplete.onSuccess(response);
                    }
                });
            }catch (Exception e){
                onComplete.onFailure(e);
            }
        }
    }
}
