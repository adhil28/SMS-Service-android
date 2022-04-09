package com.mobilesmsservice.helper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "FCM";
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        JSONObject obj = new JSONObject();
        SharedPreferences sp=getSharedPreferences("Login", MODE_PRIVATE);
        String api=sp.getString("api", null);

        try {
            obj.put("token",s);
            obj.put("api",api);
            new Server().post(new Server().baseUrl + "update-token", obj.toString(), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                }
            });
        }catch (Exception e){
            Log.e("MessagingService", "onNewToken: ",e );
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Intent intents=new Intent();
        intents.setAction("receiver");
        getBaseContext().sendBroadcast(intents);

        Handler handler = new Handler(Looper.getMainLooper());
        Map<String, String> data = remoteMessage.getData();
        String message = data.get("message"),to = data.get("to"),plat=data.get("plat");
        if (plat.equals("w")){
            startActivity(
                    new Intent(Intent.ACTION_VIEW,
                            Uri.parse(
                                    String.format("https://api.whatsapp.com/send?phone=%s&text=%s", to, message)
                            )
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            );
            new WhatsappDatabaseHelper(this).insertMessage(to,message);
        }else{
            new MessagesDatabaseHelper(this).insertMessage(to,message);
            sendSMS(to,message);
        }

    }


    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

}
