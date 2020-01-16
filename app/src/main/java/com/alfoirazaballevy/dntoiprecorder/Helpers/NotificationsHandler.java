package com.alfoirazaballevy.dntoiprecorder.Helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationsHandler {

    public static final String NOTIF_ID_SERVER_DATA_GETTER = "server_data_getter";
    public static final String NOTIF_ID_SERVER_DATA_COMPLETIONS = "server_data_completions";

    private Context context;

    public NotificationsHandler(Context context) {
        this.context = context;
    }

    public void displayBigNotification(
            String channelId,
            int icon,
            String title,
            String text,
            int notificationId
    ){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), channelId);
        builder.setSmallIcon(icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon));
        builder.setContentTitle(title);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notificationId, builder.build());
    }

    public void removeNotification(int notificationId) {
        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    public void createChannels() {
        createNotificationChannel(
                "Server Data Getter Events",
                "Events such as getting IP of Domain Names, erros, and such.",
                NotificationManager.IMPORTANCE_DEFAULT,
                NOTIF_ID_SERVER_DATA_GETTER
        );
        createNotificationChannel(
                "Server Data Getter Completion Events",
                "Events final results information.",
                NotificationManager.IMPORTANCE_DEFAULT,
                NOTIF_ID_SERVER_DATA_COMPLETIONS
        );
    }

    private void createNotificationChannel(
            CharSequence name, String description, int importance, String notificationChannelId
    ){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel =
                    new NotificationChannel(notificationChannelId, name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

}
