package ke.co.struct.chauffeurrider.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import ke.co.struct.chauffeurrider.MainActivity;
import ke.co.struct.chauffeurrider.R;

/**
 * Created by STRUCT on 2/5/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getData().size() > 0){
            Map<String,String> payload = remoteMessage.getData();
            // Create and show notification
            sendNotification(payload);
            String content = payload.get("content");
            String refnum = payload.get("refnum");
            String pickup = payload.get("pickup");
            String dropoff = payload.get("dropoff");
            String title = payload.get("title");
            Intent intent = new Intent("ke.co.struct.chauffeur");
            intent.putExtra("content",content);
            intent.putExtra("title",title);
            intent.putExtra("refnum",refnum);
            intent.putExtra("pickup",pickup);
            intent.putExtra("dropoff",dropoff);
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(intent);
        }
    }

    private void sendNotification(Map<String,String> payload) {
        String title = payload.get("title");
        String content = payload.get("content");
        String refnum = payload.get("refnum");
        String pickup = payload.get("pickup");
        String dropoff = payload.get("dropoff");
        Uri sound =Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + getPackageName() + "/raw/chauffeur_notification");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_foreground))
                        .setSmallIcon(R.drawable.logo)
                        .setSound(sound)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentText(content);
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("content",content);
        resultIntent.putExtra("title",title);
        resultIntent.putExtra("refnum",refnum);
        resultIntent.putExtra("dropoff",dropoff);
        resultIntent.putExtra("pickup",pickup);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }

}