package com.mobilesmsservice.helper;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import java.util.List;

public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = "MyAccessibilityService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String packageName = event.getPackageName().toString();
        if (packageName.equals("com.whatsapp")){
            try {
                try {
                    AccessibilityNodeInfoCompat rootNodeInfo = AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());
                    List<AccessibilityNodeInfoCompat> sendMessageNodeList = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send");
                    if (sendMessageNodeList == null || sendMessageNodeList.isEmpty()){
                        return;
                    }
                    AccessibilityNodeInfoCompat sendMessage = sendMessageNodeList.get(0);
                    if (!sendMessage.isVisibleToUser()){
                        return;
                    }
                    sendMessage.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Thread.sleep(500);
                    performGlobalAction(GLOBAL_ACTION_HOME);

                }catch (Exception e){
                    e.printStackTrace();
                }
                Log.e(TAG, "onAccessibilityEvent: Whatsapp" );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "Accessibility service destroyed");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
                AccessibilityEvent.TYPE_VIEW_FOCUSED;
        info.packageNames = new String[]
                {"com.whatsapp"};
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        this.setServiceInfo(info);
        Log.e(TAG, "Accessibility service connected");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Accessibility service destroyed");
    }
}
