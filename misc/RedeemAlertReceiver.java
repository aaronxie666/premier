package icn.premierandroid.misc;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import icn.premierandroid.MainActivity;
import icn.premierandroid.R;


public class RedeemAlertReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            createRedeemNotification(context, "You can now redeem your hourly likes.", "Premier Model Style", "You can now redeem your hourly likes.");
        }

        private void createRedeemNotification(Context context, String message, String msgText, String alert) {
            PendingIntent notificIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(msgText)
                    .setTicker(alert)
                    .setContentText(message);
            mBuilder.setContentIntent(notificIntent);
            mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
            mBuilder.setAutoCancel(true);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());
        }


}
