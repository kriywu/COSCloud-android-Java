package com.easylink.cloud.demos;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.easylink.cloud.R;

import androidx.appcompat.app.AppCompatActivity;

public class RemoteViewsActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_views);
        btnNotification = findViewById(R.id.btn_msg);
        btnNotification.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_msg:
                showNotification();
                break;
        }
    }

    public void showNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "1",
                    "Channel1",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setShowBadge(true);
            manager.createNotificationChannel(channel);

            Notification notification = new Notification
                    .Builder(this, "1")
                    .setSmallIcon(R.drawable.icon_apk)
                    .setContentTitle("Hello Android")
                    .setNumber(3).build();
            manager.notify(111, notification);
        } else {
            Notification notification = new Notification();
            notification.icon = R.drawable.icon_apk;
            notification.tickerText = "hello android";
            notification.when = System.currentTimeMillis();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            Intent intent = new Intent(this, RemoteViewsActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.contentIntent = pendingIntent;
            manager.notify(1, notification);
        }


    }
}
