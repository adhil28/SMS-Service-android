package com.mobilesmsservice.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobilesmsservice.StartActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "FCM";
    private static final String TAG = "MessagingService";
    whatsappMessageService messageService;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        JSONObject obj = new JSONObject();
        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        String api = sp.getString("api", null);
        sp.edit().putString("token", s).apply();
        try {
            obj.put("token", s);
            obj.put("api", api);
            new Server().post(new Server().baseUrl + "update-token", obj.toString(), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                }
            });
        } catch (Exception e) {
            Log.e("MessagingService", "onNewToken: ", e);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e(TAG, "onMessageReceived: recived" );
        Map<String, String> data = remoteMessage.getData();
        if (data.get("web") == null) {
            String message = data.get("message"), to = data.get("to"), plat = data.get("plat");
            if (plat != null) {
                if (plat.equals("w")) {
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    boolean isScreenOn = pm.isScreenOn();
                    boolean isScreenOnApi21 = pm.isInteractive();
                    if (!isScreenOn | !isScreenOnApi21) {
                        SharedPreferences sp = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
                        if (sp.getString("wake_phone", null) != null) {
                            sendSMS(sp.getString("wake_phone", null), "Wake up");
                            new MessagesDatabaseHelper(this).insertMessage(sp.getString("wake_phone", null), "Wake up");
                            try {
                                Thread.sleep(3500);
                                isScreenOn = pm.isScreenOn();
                                isScreenOnApi21 = pm.isInteractive();
                                if (!isScreenOn | !isScreenOnApi21) {
                                    Thread.sleep(3500);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            return;
                        }
                    }

                    if (messageService == null) {
                        messageService = new whatsappMessageService(getApplicationContext());
                        messageService.start();
                    }
                    messageService.addMessage(to, message);
                    new WhatsappDatabaseHelper(this).insertMessage(to, message);
                } else {
                    new MessagesDatabaseHelper(this).insertMessage(to, message);
                    sendSMS(to, message);
                }

                Intent intents = new Intent();
                intents.setAction("sms_receiver");
                getBaseContext().sendBroadcast(intents);
            }
        } else {
            Log.e(TAG, "onMessageReceived: " + "web");
            new webManager().onMessage(data);
        }

    }

    private void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private class whatsappMessageService extends Thread {

        JSONArray messages;
        boolean isServiceRunning = false;
        Context context;


        public whatsappMessageService(Context context) {
            this.context = context;
            messages = new JSONArray();
        }

        private void startService() throws JSONException {
            if (messages != null) {
                if (messages.length() != 0) {
                    isServiceRunning = true;
                    JSONObject message = messages.getJSONObject(0);
                    String to = message.getString("phone"), messageText = message.getString("message");
                    work(to, messageText);
                    messages.remove(0);
                    try {
                        Thread.sleep(2000);
                        startService();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    isServiceRunning = false;
                }
            } else {
                Log.e(TAG, "startService: " + "Null array " + messages.length());
            }
        }

        public void addMessage(String phone, String messageText) {
            Log.e(TAG, "addMessage: " + "Adding message");
            try {
                JSONObject message = new JSONObject();
                message.put("phone", phone);
                message.put("message", messageText);
                messages.put(message);
                if (!isServiceRunning) {
                    Log.e(TAG, "startService: " + "Service running " + messages.length());
                    startService();
                } else {
                    Log.e(TAG, "startService: " + "Service running " + messages.length());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void work(String to, String message) {
            startActivity(
                    new Intent(Intent.ACTION_VIEW,
                            Uri.parse(
                                    String.format("https://api.whatsapp.com/send?phone=%s&text=%s", to, message)
                            )
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            );
        }
    }

    private class webManager {

        public void onMessage(Map<String, String> data) {
            if (data.get("req").equals("w-m")) {
                SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                JSONObject sendData = new JSONObject();
                try {
                    sendData.put("w-m", new WhatsappDatabaseHelper(getApplicationContext()).getAllData());
                    new FireStoreHelper(getApplicationContext()).addData(sendData, sp.getString("token", null), new FireStoreHelper.onSetListener() {
                        @Override
                        public void onSend() {
                            try {
                                new Fcm(getApplicationContext()).sendFcm(new JSONObject().put("req", data.get("req")), sp.getString("web", null), new Fcm.onSend() {
                                    @Override
                                    public void success(String uid) {

                                    }

                                    @Override
                                    public void failure(String msg) {

                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFail() {

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else if (data.get("req").equals("s-m")) {
                SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                JSONObject sendData = new JSONObject();
                try {
                    sendData.put("s-m", new MessagesDatabaseHelper(getApplicationContext()).getAllData());
                    new FireStoreHelper(getApplicationContext()).addData(sendData, sp.getString("token", null), new FireStoreHelper.onSetListener() {
                        @Override
                        public void onSend() {
                            try {
                                new Fcm(getApplicationContext()).sendFcm(new JSONObject().put("req", data.get("req")), sp.getString("web", null), new Fcm.onSend() {
                                    @Override
                                    public void success(String uid) {

                                    }

                                    @Override
                                    public void failure(String msg) {

                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFail() {

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (data.get("req").equals("rvk-api")) {
                new AppFunctions.Api().revokeApi(getApplicationContext(), new AppFunctions.onComplete() {
                    @Override
                    public void onSuccess(Response response) {
                        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                        try {
                            JSONObject res = new JSONObject(response.body().string());
                            sp.edit().putString("api", res.getString("apiKey")).apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONObject data = new JSONObject()
                                    .put("req", "rvk-api")
                                    .put("status", "done");
                            new Fcm(getApplicationContext()).sendFcm(data, sp.getString("web", null), new Fcm.onSend() {
                                @Override
                                public void success(String uid) {

                                }

                                @Override
                                public void failure(String msg) {

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            }
            else if (data.get("req").equals("tgl-api")) {
                Log.e(TAG, "onMessage: tgl-api" );
                new AppFunctions.Api().toggleApi(getApplicationContext(), new AppFunctions.onComplete() {
                    @Override
                    public void onSuccess(Response response) {
                        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                        try {
                            JSONObject res = new JSONObject(response.body().string());
                            sp.edit().putString("active",res.getString("active")).apply();

                            JSONObject data = new JSONObject()
                                    .put("req", "tgl-api ")
                                    .put("status", "done");
                            new Fcm(getApplicationContext()).sendFcm(data, sp.getString("web", null), new Fcm.onSend() {
                                @Override
                                public void success(String uid) {

                                }

                                @Override
                                public void failure(String msg) {

                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            }
            else if (data.get("req").equals("ath-api")) {

                Log.e(TAG, "onMessage: auth api" );

                String userName = data.get("userName");
                String password = data.get("password");
                SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                if (userName != null && password != null) {
                    if (userName.length() > 5 && password.length() > 6) {
                        try {
                            JSONObject sendData = new JSONObject().put("api", sp.getString("api", null))
                                    .put("userName", userName)
                                    .put("password", password)
                                    .put("req", "ath-api");
                            new AppFunctions.Api().enableAuth(getApplicationContext(), sendData, new AppFunctions.onComplete() {
                                @Override
                                public void onSuccess(Response response) {
                                    new Fcm(getApplicationContext()).sendFcm(sendData, sp.getString("web", null), new Fcm.onSend() {
                                        @Override
                                        public void success(String uid) {

                                        }

                                        @Override
                                        public void failure(String msg) {

                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
            //else if ()
        }
    }
}
