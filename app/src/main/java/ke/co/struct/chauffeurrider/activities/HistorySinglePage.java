package ke.co.struct.chauffeurrider.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ke.co.struct.chauffeurrider.MainActivity;
import ke.co.struct.chauffeurrider.R;
import ke.co.struct.chauffeurrider.adapters.CustomInfoWindowAdapter;
import ke.co.struct.chauffeurrider.objects.HistoryObject;

public class HistorySinglePage extends AppCompatActivity implements OnMapReadyCallback,
        RoutingListener{
    GoogleMap mMap;
    private SupportMapFragment supportMapFragment;
    TextView from,to,date,time,fromHeader,toHeader,driver, txtstatus;
    String source, destination, ridedate, ridetime,status;
    String historyid,riderid,pickupLatLng,dropoffLatLng,drivername;
    Double picklat,picklng,droplat,droplng;
    Long timestamp = 0L;
    LatLng pickup, dropoff;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private List<Polyline> polylines;
    private static  final String TAG = "history";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_single_page);
        mAuth.getCurrentUser().getUid();
        if(getIntent().getExtras() != null){
            historyid = getIntent().getExtras().getString("historyid");
            historyInfo();
        }
        polylines = new ArrayList<>();
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.historymap);
        supportMapFragment.getMapAsync(this);
        from = findViewById(R.id.from);
        date = findViewById(R.id.rideDate);
        time = findViewById(R.id.rideTime);
        to = findViewById(R.id.to);
        toHeader = findViewById(R.id.toHeader);
        fromHeader = findViewById(R.id.fromHeader);
        driver = findViewById(R.id.txtdriver);
        txtstatus = findViewById(R.id.status);
        toHeader.setText(R.string.to);
        fromHeader.setText(R.string.from);
    }

    private void historyInfo() {
        DatabaseReference historyDB = database.getReference().child("rideHistory").child(historyid);
        historyDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("from") != null){
                        source = map.get("from").toString();
                        from.setText(source);
                    }
                    if(map.get("to") != null){
                        destination = map.get("to").toString();
                        to.setText(destination);
                    }
                    if(map.get("timestamp") != null){
                        timestamp = Long.valueOf(map.get("timestamp").toString());
                        getRideDate(timestamp);
                        getRideTime(timestamp);
                    }
                    if(map.get("pickupLat") != null){
                        picklat = Double.parseDouble(map.get("pickupLat").toString());
                    }
                    if(map.get("pickupLng") != null){
                        picklng = Double.parseDouble(map.get("pickupLng").toString());
                    }
                    if(map.get("dropoffLat") != null){
                        droplat = Double.parseDouble(map.get("dropoffLat").toString());
                    }
                    if(map.get("dropoffLng") != null){
                        droplng = Double.parseDouble(map.get("dropoffLng").toString());
                    }
                    if(map.get("drivername") != null){
                        drivername = map.get("drivername").toString();
                        driver.setText(drivername);
                    } if(map.get("status") != null){
                        status = map.get("status").toString();
                        txtstatus.setText(status);
                    }
                    if (picklng!=null && picklat!=null && droplat!=null && droplng!=null){
                        pickup = new LatLng(picklat,picklng);
                        dropoff = new LatLng(droplat,droplng);
                        getRouteToDestination(pickup,dropoff);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private String getRideTime(Long timestamp) {
        if (timestamp != 0.0){
            Calendar cal = Calendar.getInstance(Locale.getDefault());
            cal.setTimeInMillis(timestamp*1000);
            String historytime = android.text.format.DateFormat.format("HH:mm", cal).toString();
            time.setText(historytime);
            return historytime;
        }
        return null;
    }

    private String getRideDate(Long timestamp) {
        if (timestamp != 0.0){
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(timestamp*1000L);
            String historydate = android.text.format.DateFormat.format("dd-MM-yyyy", cal).toString();
            date.setText(historydate);
            return historydate;
        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(HistorySinglePage.this, R.raw.mapstyle));

            if (!success) {

            }
        } catch (Resources.NotFoundException e) {

        }
    }

    private void getRouteToDestination(LatLng pickup, LatLng destinationLatLng) {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(pickup, destinationLatLng)
                    .build();
            routing.execute();
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        mMap.clear();

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
            polyOptions.width(7);
            polyOptions.startCap(new RoundCap());
            polyOptions.endCap(new RoundCap());
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
        }
        Marker marker_one,marker_two;
        // End marker
        MarkerOptions options = new MarkerOptions();
        options.position(dropoff);
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.destination));
       marker_one=  mMap.addMarker(options);


        // Start marker
        options.position(pickup);
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.source));
        marker_two = mMap.addMarker(options);
        marker_two.showInfoWindow();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

//the include method will calculate the min and max bound.
        builder.include(marker_one.getPosition());
        builder.include(marker_two.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.20); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);

    }
    @Override
    public void onRoutingCancelled() {

    }
}
