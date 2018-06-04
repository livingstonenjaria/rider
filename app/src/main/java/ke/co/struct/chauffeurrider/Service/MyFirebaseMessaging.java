package ke.co.struct.chauffeurrider.Service;

import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import ke.co.struct.chauffeurrider.MainActivity;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        LatLng rider_location = new Gson().fromJson(remoteMessage.getNotification().getBody(), LatLng.class);
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("lat", rider_location.latitude);
        intent.putExtra("lng", rider_location.longitude);
        startActivity(intent);
    }
}
