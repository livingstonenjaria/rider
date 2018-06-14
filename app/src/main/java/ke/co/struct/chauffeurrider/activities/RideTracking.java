package ke.co.struct.chauffeurrider.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import ke.co.struct.chauffeurrider.R;
import ke.co.struct.chauffeurrider.remote.Common;

public class RideTracking extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    View driverBottomSheet;
    BottomSheetBehavior sheetBehavior;
    private TextView name, plate;
    private CircleImageView pic,car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_tracking);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        driverBottomSheet = findViewById(R.id.driver_bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(driverBottomSheet);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        name = driverBottomSheet.findViewById(R.id.driver);
        plate = driverBottomSheet.findViewById(R.id.plate);
        pic = driverBottomSheet.findViewById(R.id.driverImage);
        car = driverBottomSheet.findViewById(R.id.carImage);
        if(Common.driver !=null) {
            if (!TextUtils.isEmpty(Common.driver.getName())) {
                name.setText(Common.driver.getName());
            }
            if (!TextUtils.isEmpty(Common.driver.getPlate())) {
                plate.setText(Common.driver.getPlate());
            }
            if (!TextUtils.isEmpty(Common.driver.getPic())) {
                Picasso.with(RideTracking.this).load(Common.driver.getPic()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(pic, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Picasso.with(RideTracking.this).load(Common.driver.getPic()).placeholder(R.drawable.profile).into(pic);
                    }
                });
            }
            if (!TextUtils.isEmpty(Common.driver.getCar())) {
                Picasso.with(RideTracking.this).load(Common.driver.getCar()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.mipmap.car_pic).into(car, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Picasso.with(RideTracking.this).load(Common.driver.getCar()).placeholder(R.mipmap.car_pic).into(car);
                    }
                });
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
