package com.example.notifs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && "com.example.notifs.ACTION_LIKE".equals(intent.getAction())) {
            Toast.makeText(context, "You liked the notification!", Toast.LENGTH_SHORT).show();
        }
    }
}
