package ke.co.struct.chauffeurrider.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ke.co.struct.chauffeurrider.MainActivity;
import ke.co.struct.chauffeurrider.R;
import ke.co.struct.chauffeurrider.activities.DriverAlertActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String title, body;
    private static final String TAG = "MyFirebaseMessagingServ";

    
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        title = remoteMessage.getNotification().getTitle();
        body =remoteMessage.getNotification().getBody();
        
        if (title.equals("Cancelled")){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MyFirebaseMessagingService.this, ""+remoteMessage.getNotification().getBody(), Toast.LENGTH_SHORT).show();
                }
            });
            String message = "Your driver has cancelled the trip";
            showNotification(message);
        }
        if (title.equals("Arrived")){
            showNotification(body);
        }
        if (title.equals("Accepted")){
            try {
                JSONObject data = new JSONObject(body);
                String name = data.getString("name");
                String phone = data.getString("phone");
                String model = data.getString("carType");
                String car = data.getString("carimg");
                String plate = data.getString("licPlate");
                String pic = data.getString("ProfileImageUrl");
                Intent intent = new Intent(getBaseContext(), DriverAlertActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("phone",phone);
                intent.putExtra("pic",pic);
                intent.putExtra("model",model);
                intent.putExtra("car",car);
                intent.putExtra("plate",plate);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void showNotification(String body) {
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + getPackageName() + "/raw/chauffeur_notification");
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,new Intent(),PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_foreground))
                .setSmallIcon(R.drawable.logo)
                .setSound(sound)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());
    }
}
