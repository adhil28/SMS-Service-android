package com.mobilesmsservice.helper;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseLogin {
    private static final String TAG = "FirebaseLogin";
    private final FirebaseAuth mAuth;
    Activity activity;

    public FirebaseLogin(Activity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
    }

    public void signUp(String email, String password, onLoggedIn callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            callback.success(user.getUid());
                        } else {

                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            callback.failure(task.getException().getMessage());
                        }
                    }
                });

    }

    public void signIn(String email, String password, onLoggedIn callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            callback.success(user.getUid());
                        } else {

                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            callback.failure(task.getException().getMessage());
                        }
                    }
                });

    }

    public FirebaseUser getUser(){
        return mAuth.getCurrentUser();
    }

    public interface onResult{
        void onSuccess();
        void onFailure(Exception e);
    }

    public void deleteAccount(onResult onDeleteAccount){
        mAuth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                onDeleteAccount.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onDeleteAccount.onFailure(e);
            }
        });
    }

    public void logOut(){
        FirebaseAuth.getInstance().signOut();
    }

    public interface onLoggedIn {
        void success(String uid);
        void failure(String msg);
    }
}
