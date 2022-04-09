package com.mobilesmsservice.helper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class ClipBoard {
    Context context;

    public ClipBoard(Context context) {
        this.context = context;
    }

    public void copy(String text){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("COPY", text);
        clipboard.setPrimaryClip(clip);
    }
}
