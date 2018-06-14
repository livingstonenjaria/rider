package ke.co.struct.chauffeurrider.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import ke.co.struct.chauffeurrider.MainActivity;
import ke.co.struct.chauffeurrider.Model.Driver;
import ke.co.struct.chauffeurrider.R;
import ke.co.struct.chauffeurrider.remote.Common;

public class DriverAlertActivity extends AppCompatActivity {
    private MaterialEditText drivername, driverphone, cartype, licplate;
    private CircleImageView carpic, driverpic;
    private String driver_name, driver_phone, driver_pic, car_type, lic_plate, car_pic;
    private Button ok;
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
        ok = findViewById(R.id.okbtn);
        driverphone.setFocusable(false);
        driverphone.setClickable(false);
        drivername.setFocusable(false);
        drivername.setClickable(false);
        licplate.setFocusable(false);
        licplate.setClickable(false);
        cartype.setFocusable(false);
        cartype.setClickable(false);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDetailsToMain();
            }
        });

        if (getIntent() != null){
            driver_name = getIntent().getStringExtra("name");
            driver_phone = getIntent().getStringExtra("phone");
            driver_pic = getIntent().getStringExtra("pic");
            car_type = getIntent().getStringExtra("model");
            car_pic = getIntent().getStringExtra("car");
            lic_plate = getIntent().getStringExtra("plate");

//            Common.driver.setName(driver_name);
//            Common.driver.setModel(car_type);
//            Common.driver.setPlate(lic_plate);
//            Common.driver.setPhone(driver_phone);
//            Common.driver.setCar(car_pic);
//            Common.driver.setPic(driver_pic);

            drivername.setText(driver_name);
            driverphone.setText(driver_phone);
            licplate.setText(lic_plate);
            cartype.setText(car_type);
                Picasso.with(DriverAlertActivity.this).load(driver_pic).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(driverpic, new Callback() {
                    @Override
                    public void onSuccess() {}
                    @Override
                    public void onError() {
                            Picasso.with(DriverAlertActivity.this).load(driver_pic).placeholder(R.drawable.profile).into(driverpic);
                    }
                });
            Picasso.with(DriverAlertActivity.this).load(car_pic).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.mipmap.car_pic).into(carpic, new Callback() {
                @Override
                public void onSuccess() {}
                @Override
                public void onError() {
                    Picasso.with(DriverAlertActivity.this).load(car_pic).placeholder(R.mipmap.car_pic).into(carpic);
                }
            });

        }
    }

    private void sendDetailsToMain() {
        Intent intent = new Intent(DriverAlertActivity.this, RideTracking.class);
        startActivity(intent);
        finish();
    }
}
