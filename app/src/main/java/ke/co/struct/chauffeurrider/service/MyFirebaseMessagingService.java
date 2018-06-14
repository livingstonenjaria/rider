package ke.co.struct.chauffeurrider.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
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
import ke.co.struct.chauffeurrider.helper.NotificationHelper;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String title, body;
    private static final String TAG = "MyFirebaseMessagingServ";

    
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0){
            title = remoteMessage.getData().get("title");
            Map<String,String> notification = remoteMessage.getData();


            if (title.equals("Accepted")){
                String name = notification.get("name");
                String phone = notification.get("phone");
                String model =notification.get("carType");
                String car = notification.get("carimg");
                String plate = notification.get("licPlate");
                String pic = notification.get("ProfileImageUrl");
                Intent intent = new Intent(getApplicationContext(), DriverAlertActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("phone",phone);
                intent.putExtra("pic",pic);
                intent.putExtra("model",model);
                intent.putExtra("car",car);
                intent.putExtra("plate",plate);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                showNotificationAccepted(name,phone,model,car,plate,pic);

            }
            if (title.equals("Cancelled")){
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MyFirebaseMessagingService.this, ""+remoteMessage.getNotification().getBody(), Toast.LENGTH_SHORT).show();
                    }
                });
                String message = "Your driver has cancelled the trip";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    showNotificationsAPI26(message);
                }
                showNotification(message);
            }
            if (title.equals("Arrived")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    showNotificationsAPI26(body);
                }
                showNotification(body);
            }
        }
    }

    private void openDriverAccept() {
        try {
            JSONObject data = new JSONObject(body);
            String name = data.getString("name");
            String phone = data.getString("phone");
            String model = data.getString("carType");
            String car = data.getString("carimg");
            String plate = data.getString("licPlate");
            String pic = data.getString("ProfileImageUrl");



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private  void showNotificationsAPI26(String message){
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,new Intent(),PendingIntent.FLAG_ONE_SHOT);
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + getPackageName() + "/raw/drivernotification");
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        Notification.Builder builder = notificationHelper.getChauffeurNotification(title,message,pendingIntent,sound);
        notificationHelper.getManager().notify(1,builder.build());

    }
    private void showNotification(String body) {
        Intent intent = new Intent(this, DriverAlertActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + getPackageName() + "/raw/chauffeur_notification");
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_ONE_SHOT);
        Notification.Builder builder = new Notification.Builder(getBaseContext());
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_foreground))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_car)
                .setSound(sound)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());
    }
    private void showNotificationAccepted(String name, String phone, String model, String car, String plate, String pic) {
        Intent intent = new Intent(getApplicationContext(), DriverAlertActivity.class);
            intent.putExtra("name",name);
            intent.putExtra("phone",phone);
            intent.putExtra("pic",pic);
            intent.putExtra("model",model);
            intent.putExtra("car",car);
            intent.putExtra("plate",plate);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + getPackageName() + "/raw/chauffeur_notification");
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0, intent ,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(getBaseContext());
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_foreground))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_car)
                .setSound(sound)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());
    }
}
