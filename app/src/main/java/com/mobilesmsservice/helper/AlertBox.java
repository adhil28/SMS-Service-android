package com.mobilesmsservice.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public class AlertBox {
    Context context;

    public AlertBox(Context context) {
        this.context = context;
    }

    public void show(String title, String message){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                    }
                })
                .show();
    }
    public void showWithButtonCallback(String title, String message,DialogInterface.OnClickListener clickListener){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.yes, clickListener)
                .show();
    }
    public AlertDialog showWithCustomView(View view,String title){
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .show();
    }
}
