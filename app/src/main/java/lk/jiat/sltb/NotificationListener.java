package lk.jiat.sltb;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NotificationListener extends NotificationListenerService {

    public static List<Notification> notifications = new ArrayList<>();

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String title = sbn.getNotification().extras.getString("android.title");
        String text = sbn.getNotification().extras.getString("android.text");

        Notification notification = new Notification(title, text, false);
        notifications.add(notification);

        Log.d(TAG, "Notification added: " + title + " - " + text);
    }
}