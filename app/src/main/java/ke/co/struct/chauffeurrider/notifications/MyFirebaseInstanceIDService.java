package ke.co.struct.chauffeurrider.notifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String REG_TOKEN = "REG_TOKEN";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        storeToken(refreshedToken);
    }
    private void storeToken(String refreshedToken) {
        SharedPrefManager.getInstance(getApplicationContext()).storeToken(refreshedToken);
    }
}