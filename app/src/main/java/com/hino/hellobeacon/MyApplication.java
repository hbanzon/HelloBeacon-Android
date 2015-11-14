package com.hino.hellobeacon;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;
import java.util.UUID;

/**
 * Created by Hino Banzon on 11/12/15.
 *
 * This class monitors beacons at all times, no matter which Activity is in use.  Basically, this is a Background App
 *
 * In Android, the Application base class is 'for those who need to maintain global application state.'
 *   -- which is what we need for beacon monitoring at all times
 *
 */
public class MyApplication extends Application {

    private BeaconManager beaconManager;

    private static final String NOTIFICATION_TITLE = "Hello Beacon";
    private static final String ENTER_NOTIFICATION_TEMPLATE = "You just entered %s";
    private static final String EXIT_NOTIFICATION_TEMPLATE = "You just left %s";

    @Override
    public void onCreate() {
        super.onCreate();

        // instantiate the BeaconManager
        beaconManager = new BeaconManager(getApplicationContext());

        // set monitoring listeners
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                String message = String.format(ENTER_NOTIFICATION_TEMPLATE, region.getIdentifier());
                showNotification(NOTIFICATION_TITLE, message);
            }

            @Override
            public void onExitedRegion(Region region) {
                String message = String.format(EXIT_NOTIFICATION_TEMPLATE, region.getIdentifier());
                showNotification(NOTIFICATION_TITLE, message);
            }
        });

        // monitor beacons
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new Region(
                        "Mint",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        63029, 44225
                ));

                beaconManager.startMonitoring(new Region(
                        "Ice",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        31194, 58554
                ));

                beaconManager.startMonitoring(new Region(
                        "Blueberry",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        52066, 51215
                ));
            }
        });

    }

    /**
     * Helper method to show Notifications.
     *
     * @param title
     * @param message
     */
    protected void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags((Intent.FLAG_ACTIVITY_SINGLE_TOP));
        PendingIntent pendingIntent = PendingIntent.getActivities(
                this, 0, new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

}
