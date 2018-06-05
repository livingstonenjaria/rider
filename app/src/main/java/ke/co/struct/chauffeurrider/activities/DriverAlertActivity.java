package ke.co.struct.chauffeurrider.activities;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ke.co.struct.chauffeurrider.R;

public class DriverAlertActivity extends AppCompatActivity {
    private TextView drivername, driverphone, driverpic, cartype, licplate, carpic;
    private String driver_name, driver_phone, driver_pic, car_type, lic_plate, car_pic;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_alert);
        mediaPlayer = MediaPlayer.create(this, R.raw.chauffeur_notification);
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
        drivername = findViewById(R.id.drivername);
        driverphone = findViewById(R.id.driverphone);
        driverpic = findViewById(R.id.driverimage);
        licplate = findViewById(R.id.licplate);
        cartype = findViewById(R.id.cartype);
        carpic = findViewById(R.id.carpic);

        if (getIntent() != null){
            driver_name = getIntent().getStringExtra("name");
            driver_phone = getIntent().getStringExtra("phone");
            driver_pic = getIntent().getStringExtra("pic");
            car_type = getIntent().getStringExtra("model");
            car_pic = getIntent().getStringExtra("car");
            lic_plate = getIntent().getStringExtra("plate");

            drivername.setText(driver_name);
            driverphone.setText(driver_phone);
            driverpic.setText(driver_pic);
            licplate.setText(car_type);
            cartype.setText(car_pic);
            carpic.setText(lic_plate);
        }
    }
}
