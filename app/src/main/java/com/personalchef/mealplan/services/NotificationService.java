package com.personalchef.mealplan.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.personalchef.mealplan.R;
import com.personalchef.mealplan.StepCounterActivity;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class NotificationService extends IntentService {

    public static final String ACTION_SUMMARY = "Summary";
    //public static final String ACTION_BAZ = "com.personalchef.mealplan.action.BAZ";

    public static final String EXTRA_TITLE = "Title";
    public static final String EXTRA_TEXT = "Text";
    private final int NOTIFICATION_ID = 5453;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String title = intent.getStringExtra(EXTRA_TITLE);
            final String text = intent.getStringExtra(EXTRA_TEXT);
            final String action = intent.getAction();

            if (ACTION_SUMMARY.equals(action)) {
                showNotification(title, text, StepCounterActivity.class);
            } /*else if (ACTION_BAZ.equals(action)) {
                //handleActionBaz(param1, param2);
            }*/
        }
    }

    private void showNotification(String title, String text, Class<?> cls) {

        Log.i("PC", "Main: Into showNotification - Title:  "+ title + " Text: " + text + "  Cls: " + cls.getName());
        // Intent to redirect to an Activity when notification is clicked
        Intent actionIntent = new Intent(this, cls);
        //actionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Notification notif = createNotification(title, text, actionIntent);

        // Issue notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this); //(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notif);

        return;
    }

    private Notification createNotification(String title, String text, Intent actionIntent) {

        // Intent to redirect to an Activity when notification is clicked
        PendingIntent actionPendingIntent = PendingIntent.getActivity(this,
                0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(text);
        bigText.setBigContentTitle(title);
        bigText.setSummaryText("Steps Progress");

        // Create Notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.notifChannelId))
                .setSmallIcon(R.drawable.main_logo)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(actionPendingIntent)
                .setStyle(bigText)
                .setVibrate(new long[] {0, 1000})
                .setAutoCancel(true);

        // Create an action
        return builder.build();
    }

    /**
     * Handle action Summary in the provided background thread with the provided
     * parameters.
     *
    private void handleActionSummary(String title, String text) {

    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     *
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }*/
}