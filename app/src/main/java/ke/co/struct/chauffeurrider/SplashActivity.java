package ke.co.struct.chauffeurrider;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ke.co.struct.chauffeurrider.register_and_login.RiderLoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent intent = new Intent(SplashActivity.this, RiderLoginActivity.class);
        startActivity(intent);
        finish();
    }
}
