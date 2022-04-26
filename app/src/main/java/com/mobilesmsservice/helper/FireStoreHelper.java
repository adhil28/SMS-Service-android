package com.mobilesmsservice.helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class FireStoreHelper {
    Context context;
    private static final String TAG = "FireStoreHelper";

    public FireStoreHelper(Context context) {
        this.context = context;
    }
    public interface onSetListener{
        void onSend();
        void onFail();
    }

    public void addData(JSONObject data, String token,onSetListener onSetListener){
        CollectionReference db = FirebaseFirestore.getInstance().collection("web");
        HashMap<String, Object> addData = new Gson().fromJson(data.toString(), HashMap.class);
        db.document(token).set(addData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                onSetListener.onSend();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onSetListener.onFail();
            }
        });
    }
}
