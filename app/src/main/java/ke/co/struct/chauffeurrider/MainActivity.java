package ke.co.struct.chauffeurrider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import ke.co.struct.chauffeurrider.Model.DataMessage;
import ke.co.struct.chauffeurrider.Model.FCMResponse;
import ke.co.struct.chauffeurrider.Model.Notification;
import ke.co.struct.chauffeurrider.Model.Sender;
import ke.co.struct.chauffeurrider.Model.Token;
import ke.co.struct.chauffeurrider.Model.User;
import ke.co.struct.chauffeurrider.notifications.MyFirebaseMessagingService;
import ke.co.struct.chauffeurrider.remote.Common;
import ke.co.struct.chauffeurrider.remote.IFCMService;
import ke.co.struct.chauffeurrider.activities.CardActivity;
import ke.co.struct.chauffeurrider.activities.HistoryActivity;
import ke.co.struct.chauffeurrider.activities.PaymentActivity;
import ke.co.struct.chauffeurrider.activities.ProfileActivity;
import ke.co.struct.chauffeurrider.activities.TermsActivity;
import ke.co.struct.chauffeurrider.adapters.CustomInfoWindowAdapter;
import ke.co.struct.chauffeurrider.dialog.PaymentDialog;
import ke.co.struct.chauffeurrider.notifications.SharedPrefManager;
import ke.co.struct.chauffeurrider.register_and_login.RiderLoginActivity;
import ke.co.struct.chauffeurrider.service.MyFirebaseInstanceIDService;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        RoutingListener,
        AdapterView.OnItemSelectedListener,
        PaymentDialog.NoticeDialogListener{
    ActionBarDrawerToggle toggle;
    Button btnBottomSheet;
    View bottomSheet;
    BottomSheetBehavior sheetBehavior;
    Toolbar toolbar = null;
    DrawerLayout drawer;
    IFCMService mService;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference riderRef, driversAvailable;
    private  String mName,mEmail,mProfileUrl, ridername,rideremail,riderimage,riderphone,ridercountrycode,ridercurrency;
    private TextView txtEmail,txtName;
    private ImageView profileImage;
    private NavigationView navigationView;
    private View navHeaderView;
    GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Location mLastKnownLocation;
    private LatLng mDefaultLocation = new LatLng(-1.2921, 36.8219);
    private LatLng riderLatLng, driverLatLng, directionsLatLng, pickUpLatLng,start,end,otherLatLng;
    private double destinationLat = 0.0, destinationLng = 0.0;
    private double currentLat = 0.0, currentLng = 0.0, otherLat, otherLng;
    private double initialLat = 0.0, initialLng = 0.0;
    private double directionLat = 0.0, directionLng = 0.0;
    private double pickupLat = 0.0, pickupLng = 0.0;
    private static final int DEFAULT_ZOOM = 13;
    private FusedLocationProviderClient mFusedLocationClient;
    private GeoQuery geoQuery;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    public static final int REQUEST_LOCATION_CODE = 99;
    private final String TAG = "riderMap";
    private String userID, paymentMethod = "Cash",brand,lastdigits;
    private String driverFoundId, placeId, place_name, driver_ratings,cancel_reason,countrycode = "KE",defaultcurrency,faretable = "fares";
    private  String driver_name, car_type, driver_plate, driverImage, driver_phone;
    private Boolean mLocationPermissionGranted = false;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_SEARCH = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKUP = 2;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION = 3;
    private ImageView imgPayment, driver_image,driver_profile;
    private Button searchBtn, requestBtn, confirmBtn, cancelRideBtn, callDriver,makepayment,rateBtn,cancelBtn, ongoingBtn;
    private MaterialEditText mSource, mDestination, mrideSource, mrideDestination;
    private TextView mPayment, driverETA, driverName, carType, licPlate, ratings,estimate,pricetag;
    private TextView txtDropOff, txtPickUp,txtRating,txtDriver;
    private ConstraintLayout  driverLayout,paymentDialog,ratingDialog;
    private LinearLayout mPaymentLayout;
    private RatingBar ratingBar;
    private CameraPosition mCameraPosition;
    SupportMapFragment mapFragment;
    CharSequence placeName = null;
    View v, mapView;
    float bearing = 0;
    float rating = 0;
    private List<Polyline> polylines;
    private int duration,distance;
    private JSONObject ret;
    private JSONObject location;
    private String location_string, address,driverId,completerideref,riderpickup,riderdropoff,otheraddress;
    private int radius = 1;
    private int newRadius = 1;
    private int maxRadius = 15;
    private double min_fare = 0 ,base_fare = 0,per_km = 0, total_estimate = 0, range = 0;
    private Boolean driverFound = false;
    private Marker driverMarker, pickUpMarker;
    Spinner spinner;
    float mDistance;
    FloatingActionButton fabrequest;
     ValueEventListener rejectedlistner;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Chauffeur");
        mService = Common.getFCMService();
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        fabrequest = findViewById(R.id.fabrequest);
        bottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottomSheet);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
           /*----------------------------------Notifications---------------------------------*/
        String recent_token = SharedPrefManager.getInstance(this).getToken();
        if (recent_token != null) {
            DatabaseReference notificationsRef = database.getReference("Notifications").child(userID);
            Map<String, Object> notificationUpdate = new HashMap<>();
            notificationUpdate.put("token", recent_token);
            notificationsRef.updateChildren(notificationUpdate);
        }
        /*--------------------------------------End---------------------------------------*/
        buildGoogleApiClient();
        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler, new IntentFilter("ke.co.struct.chauffeur"));


        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }


        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        navHeaderView = navigationView.getHeaderView(0);
        txtEmail = navHeaderView.findViewById(R.id.email);
        txtName = navHeaderView.findViewById(R.id.name);
        profileImage = navHeaderView.findViewById(R.id.profile_image);
        riderRef = database.getReference().child("Users").child("Riders").child(userID);
        riderRef.keepSynced(true);
        riderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        mName = map.get("name").toString();
                        txtName.setText(mName);
                    }
                    if (map.get("email") != null) {
                        mEmail = map.get("email").toString();
                        txtEmail.setText(mEmail);
                    }
                    if (map.get("countrycode") != null) {
                        countrycode = map.get("countrycode").toString();
                    }
                    if (map.get("ProfileImageUrl") != null) {
                        mProfileUrl = map.get("ProfileImageUrl").toString();
                        Picasso.with(MainActivity.this).load(mProfileUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(profileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Picasso.with(MainActivity.this).load(mProfileUrl).placeholder(R.drawable.profile).into(profileImage);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        polylines = new ArrayList<>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mainmap);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        mPaymentLayout = bottomSheet.findViewById(R.id.payment);
        confirmBtn = bottomSheet.findViewById(R.id.confirmBtn);
        cancelBtn = findViewById(R.id.cancel);
        ongoingBtn = findViewById(R.id.ride_started);
        callDriver = findViewById(R.id.callDriver);
        driverLayout = findViewById(R.id.mDriverInfo);
        paymentDialog = findViewById(R.id.payment_dialog);
        if (checkLocationPermission()) {
            if (mFusedLocationClient != null) {
                getLastLocation();
            }
        }

        spinner = findViewById(R.id.spinner);
        driver_image = findViewById(R.id.driver_image);
        carType = findViewById(R.id.carType);
        licPlate = findViewById(R.id.licPlate);
        driverName = findViewById(R.id.driverName);
        ratings = findViewById(R.id.ratings);
        driverETA = findViewById(R.id.driverEta);
        mPayment = bottomSheet.findViewById(R.id.txtPayment);
        mSource = bottomSheet.findViewById(R.id.source);
        estimate = bottomSheet.findViewById(R.id.txtEstimate);
        mDestination = bottomSheet.findViewById(R.id.destination);
//        mrideSource = findViewById(R.id.ridesource);
//        mrideDestination = findViewById(R.id.ridedestination);
        imgPayment = bottomSheet.findViewById(R.id.imgPayment);
        pricetag = findViewById(R.id.pricetag);
        makepayment = findViewById(R.id.makepayment);
        /*------------------Rating-------------------------*/
        ratingDialog = findViewById(R.id.rating_dialog);
        rateBtn = findViewById(R.id.btnRate);
        ratingBar = findViewById(R.id.ratingbar);
        txtDriver = findViewById(R.id.driver_name);
        txtRating = findViewById(R.id.ratingtxt);
        driver_profile = findViewById(R.id.driver_profile);

        /*-----------------Payment----------------------*/
        txtDropOff = findViewById(R.id.dropoff);
        txtPickUp = findViewById(R.id.pickup);
        mSource.setFocusable(false);
        mSource.setClickable(true);
        mDestination.setFocusable(false);
        mDestination.setClickable(true);
//        mrideSource.setFocusable(false);
//        mrideSource.setClickable(false);
//        mrideDestination.setFocusable(false);
//        mrideDestination.setClickable(false);
        mPaymentLayout.setClickable(true);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choice, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        mPaymentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPaymentOptions();
            }
        });
        /*---------------------Get notification info---------------------------*/
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String content = null, refnum = null, pickup = null, dropoff = null;
                if (key.equals("content")) {
                    content = getIntent().getExtras().getString(key);
                }
                if (key.equals("refnum")) {
                    refnum = getIntent().getExtras().getString(key);
                }
                if (key.equals("pickup")) {
                    pickup = getIntent().getExtras().getString(key);
                }
                if (key.equals("dropoff")) {
                    dropoff = getIntent().getExtras().getString(key);
                }
                if (key.equals("title")) {
                    String title = getIntent().getExtras().getString(key);
                    if (title.equals("Ride started")) {
                        ongoingBtn.setVisibility(View.VISIBLE);
                    }
                    if (title.equals("Ride ended")) {
                        ongoingBtn.setVisibility(View.GONE);
                    }
                    if (title.equals("Driver Cancelled")) {
                        showDriverCanceled();
                    }
                    if (title.equals("Driver has arrived")) {
                        showDriverArrived();
                    }
                    if (title.equals("Total Ride Cost")) {
                        //dataFromActivityToFragment.sendCompleteRideInfo(title,content,ref,pickup,dropoff);
                        completerideref = refnum;
                        riderpickup = pickup;
                        riderdropoff = dropoff;
                        txtPickUp.setText(pickup);
                        txtDropOff.setText(dropoff);
                        showPaymentDialog(content);
                        ongoingBtn.setVisibility(View.GONE);
                    }
                    if (title.equals("No Driver Found")) {
                        //Toast.makeText(this, "No driver found", Toast.LENGTH_SHORT).show();

                        displayNoDriversAvailable();
                    }
                    if (title.equals("Driver Found")) {
                        if (refnum != null) {
                            getDriverDetails(refnum);
                            getDriverLocation();
                            driverId = refnum;
                        }

                    }

                }
            }

        }
        /*------------------End---------------------------*/

        /*-----------------Source place picker-----------*/
        mSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(Place.TYPE_COUNTRY)
                            .setCountry(countrycode)
                            .build();
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .setFilter(autocompleteFilter)
                                    .build(MainActivity.this);


                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKUP);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });
         /*-----------------Destination place picker-----------*/
        mDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(Place.TYPE_COUNTRY)
                            .setCountry(countrycode)
                            .build();
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                  .setFilter(autocompleteFilter)
                                    .build(MainActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });
         /*-----------------Search place -----------*/
        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(Place.TYPE_COUNTRY)
                            .setCountry(countrycode)
                            .build();
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                   .setFilter(autocompleteFilter)
                                    .build(MainActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_SEARCH);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

            }
        });
//         /*-----------------Request a Chauffeur-----------*/
//        requestBtn = findViewById(R.id.requestBtn);
//        requestBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (address == null) {
//                    getLocationInfo(currentLat, currentLng);
//                }
//                if (address != null) {
//                    mSource.setText(address);
//                }
//                confirmBtn.setVisibility(View.VISIBLE);
//                requestBtn.setVisibility(View.GONE);
//            }
//        });
         /*-----------------Confirm request details-----------*/
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (address != null) {
//                    mrideSource.setText(address);
                }
                if (placeName != null) {
//                    mrideDestination.setText(placeName);
                    riderRequest();
                    bottomSheet.setVisibility(View.GONE);
                    fabrequest.setVisibility(View.GONE);
                    cancelRideBtn.setVisibility(View.VISIBLE);
                    driverLayout.setVisibility(View.GONE);
                }else {
                    Toast.makeText(MainActivity.this, "Please enter a destination", Toast.LENGTH_SHORT).show();
                }

            }
        });
         /*-----------------Call the chauffeur-----------*/
        cancelRideBtn = findViewById(R.id.cancel_request);
        cancelRideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCancelDialog();
            }
        });
         /*-----------------Make a phone call to the driver-----------*/
        callDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (driver_phone != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", driver_phone, null));
                    startActivity(intent);
                }
            }
        });
        /*------------------Cancel Button ---------------------------*/
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRide();
            }
        });
        /*------------------Make payment---------------------------*/
        makepayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePayment();
                paymentDialog.setVisibility(View.GONE);
            }
        });
        /*-----------------Rating Bar-----------------------------*/
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating = v;
            }
        });
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingDialog.setVisibility(View.GONE);
                requestBtn.setVisibility(View.VISIBLE);
                submitRating();
                mMap.clear();
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });
        getLastLocation();
        defaultcurrency = getResources().getString(R.string.currency_in_kenya);
        DatabaseReference getRiderInfo = database.getReference().child("Users").child("Riders").child(userID);
        getRiderInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Common.userrider = dataSnapshot.getValue(User.class);
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        ridername = map.get("name").toString();
                    }
                    if (map.get("phone") != null) {
                        riderphone = map.get("phone").toString();
                    }
                    if (map.get("ProfileImageUrl") != null) {
                        riderimage = map.get("ProfileImageUrl").toString();
                    }
                    if (map.get("email") != null) {
                        rideremail = map.get("email").toString();
                    }
                    if (map.get("countrycode") != null) {
                        ridercountrycode = map.get("countrycode").toString();
                    }
                    if (map.get("currency") != null) {
                        ridercurrency = map.get("currency").toString();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        if (address!= null){
                            mSource.setText(address);
                        }

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
        fabrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBottomSheet();
            }
        });
        updateFirebaseToken();
    }

    private void toggleBottomSheet() {
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
    }

    private void updateFirebaseToken() {
        MyFirebaseInstanceIDService myFirebaseService = new MyFirebaseInstanceIDService();
        myFirebaseService.updateTokenToServer(FirebaseInstanceId.getInstance().getToken());
    }
    private void showPaymentOptions() {
        DialogFragment dialog = new PaymentDialog();
        dialog.show(getSupportFragmentManager(), "PaymentDialog");

    }

    private void cancelRide() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle(R.string.cancel_reason);
        builder.setSingleChoiceItems(R.array.cancel_choices, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        cancel_reason = ((AlertDialog)dialogInterface).getListView().getItemAtPosition(i).toString();
                        break;
                    case 1:
                        cancel_reason = ((AlertDialog)dialogInterface).getListView().getItemAtPosition(i).toString();
                        break;
                    case 2:
                        cancel_reason = ((AlertDialog)dialogInterface).getListView().getItemAtPosition(i).toString();
                        break;
                }
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseReference cancelrequest =  database.getReference().child("cancelride");
                String request = cancelrequest.push().getKey();
                DatabaseReference cancel = database.getReference().child("cancelride").child(request).child(userID).child(driverId);
                HashMap<String, Object> map = new HashMap<>();
                map.put("reason", cancel_reason);
                cancel.updateChildren(map);
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showDriverCanceled() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.driver_cancel);
        builder.setMessage(R.string.driver_cancel_msg);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                erasepolylines();
                mMap.clear();
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle(R.string.cancel_reason);
        builder.setSingleChoiceItems(R.array.cancel_choices, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        cancel_reason = ((AlertDialog)dialogInterface).getListView().getItemAtPosition(i).toString();
                        break;
                    case 1:
                        cancel_reason = ((AlertDialog)dialogInterface).getListView().getItemAtPosition(i).toString();
                        break;
                    case 2:
                        cancel_reason = ((AlertDialog)dialogInterface).getListView().getItemAtPosition(i).toString();
                        break;
                }
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseReference cancelrequest =  database.getReference().child("cancelledrequest");
                String request = cancelrequest.push().getKey();
                DatabaseReference cancel = database.getReference().child("cancelledrequest").child(request).child(userID);
                HashMap<String, Object> map = new HashMap<>();
                map.put("reason", cancel_reason);
                cancel.updateChildren(map);
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();

            }
        });
        builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private BroadcastReceiver mHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String title = intent.getStringExtra("title");
                String content = intent.getStringExtra("content");
                String refnum = intent.getStringExtra("refnum");
                String pickup = intent.getStringExtra("pickup");
                String dropoff = intent.getStringExtra("dropoff");
                if (title != null) {
                    if (title.equals("Ride started")) {
                        ongoingBtn.setVisibility(View.VISIBLE);
                    }
                    if (title.equals("Ride ended")) {
                        ongoingBtn.setVisibility(View.GONE);
                    }

                    if (title.equals("Driver has arrived")) {
                        showDriverArrived();
                    }
                    if (title.equals("Driver Cancelled")) {
                        showDriverCanceled();
                    }
                    if (title.equals("Total Ride Cost")) {
                        //dataFromActivityToFragment.sendCompleteRideInfo(title,content,ref,pickup,dropoff);
                        completerideref = refnum;
                        riderpickup = pickup;
                        riderdropoff = dropoff;
                        txtPickUp.setText(pickup);
                        txtDropOff.setText(dropoff);
                        showPaymentDialog(content);
                        ongoingBtn.setVisibility(View.GONE);
                    }
                    if (title.equals(getString(R.string.no_driver))) {
                        displayNoDriversAvailable();
                    }
                    if (title.equals("Driver Found")) {
                        if (refnum != null) {
                            getDriverDetails(refnum);
                            getDriverLocation();
                            driverId = refnum;
                        }
                    }
                }
            }
        }
    };

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.


        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_payment) {
            Intent payment = new Intent(MainActivity.this, PaymentActivity.class);
            startActivity(payment);

        }  else if (id == R.id.nav_about) {
            Intent payment = new Intent(MainActivity.this, TermsActivity.class);
            startActivity(payment);

        } else if (id == R.id.nav_logout) {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, RiderLoginActivity.class);
            startActivity(intent);
            finish();
        }
        drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String option = adapterView.getItemAtPosition(i).toString();

        if(option.equals(getString(R.string.other))) {
            MarkerOptions options = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .position(riderLatLng)
                    .draggable(true)
                    .zIndex(1.0f)
                    .title(getString(R.string.drag_marker));
            pickUpMarker = mMap.addMarker(options);
            Toast.makeText(this, R.string.drag_marker_to_location, Toast.LENGTH_LONG).show();
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    otherLatLng = pickUpMarker.getPosition();
                    pickUpLatLng = otherLatLng;
                    otherLat = otherLatLng.latitude;
                    otherLng = otherLatLng.longitude;
                    start = otherLatLng;
                    if (end != null){
                        getRouteToDestination(end);
                    }
                    getLocationInfo(otherLat,otherLng);
                    if (address != null){
//                        mrideSource.setText(address);
                        mSource.setText(address);
                    }

                }
            });
        }
        if (option.equals(getString(R.string.me))){
            if (pickUpMarker != null){
                pickUpMarker.remove();
                erasepolylines();
                mMap.clear();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onListItemClick(DialogFragment dialog, String itemName) {
        if (itemName.equals(getResources().getString(R.string.visa))) {
            paymentMethod = getResources().getString(R.string.visa);
            DatabaseReference getcardDetails = database.getReference().child("Users").child("Riders").child(userID).
                    child("customerpaymentid").child("sources").child("data").child("0");
            getcardDetails.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map.get("brand") != null) {
                            brand = map.get("brand").toString();
                            if (brand.equals("Visa")) {
                                imgPayment.setBackgroundResource(R.mipmap.visa);
                            }
                        }
                        if (map.get("brand") != null) {
                            brand = map.get("brand").toString();
                            if (brand.equals("MasterCard")) {
                                imgPayment.setBackgroundResource(R.mipmap.mastercard);
                            }
                        }
                        if (map.get("last4") != null) {
                            lastdigits = map.get("last4").toString();
                            mPayment.setText(brand + " ***" + lastdigits);
                        }
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(R.string.carderror)
                                .setTitle(R.string.selectionerror);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Intent intent = new Intent(MainActivity.this, CardActivity.class);
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }  else if (itemName.equals(getResources().getString(R.string.cash))) {
            imgPayment.setBackgroundResource(R.mipmap.ico1);
            paymentMethod = getResources().getString(R.string.cash);
            mPayment.setText(R.string.cash);
        } else if (itemName.equals(getResources().getString(R.string.corporate))) {
            imgPayment.setBackgroundResource(R.mipmap.ico0);
            paymentMethod = getResources().getString(R.string.corporate);
            mPayment.setText(R.string.corporate);
        }
        dialog.dismiss();
    }

    //check for network connectivity
    public class Retry implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            checkNetworkStatus();
        }
    }
    private void checkNetworkStatus(){
        if (isNetworkAvailable()==false){
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.mainRootLayout), R.string.offline, Snackbar.LENGTH_INDEFINITE);
            mySnackbar.setAction(R.string.retry, new Retry());
            mySnackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
            mySnackbar.show();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            String refnum = intent.getStringExtra("refnum");
            String pickup = intent.getStringExtra("pickup");
            String dropoff = intent.getStringExtra("dropoff");
            if(title != null) {
                if (title.equals("Driver Cancelled")) {
                    showDriverCanceled();
                }
                if (title.equals("Driver has arrived")) {
                   showDriverArrived();
                }
                if (title.equals("Ride started")) {
                    ongoingBtn.setVisibility(View.VISIBLE);
                }
                if (title.equals("Ride ended")) {
                    ongoingBtn.setVisibility(View.GONE);
                }
                if (title.equals("Total Ride Cost")) {
                    //dataFromActivityToFragment.sendCompleteRideInfo(title,content,ref,pickup,dropoff);
                    completerideref = refnum;
                    riderpickup = pickup;
                    riderdropoff = dropoff;
                    txtPickUp.setText(pickup);
                    txtDropOff.setText(dropoff);
                    showPaymentDialog(content);
                    ongoingBtn.setVisibility(View.GONE);
                }
                if (title.equals("No Driver Found")){
                    //Toast.makeText(this, "No driver found", Toast.LENGTH_SHORT).show();

                    displayNoDriversAvailable();
                }
                if (title.equals("Driver Found")){
                    if(refnum!=null){
                        getDriverDetails(refnum);
                        getDriverLocation();
                        driverId = refnum;
                    }

                }

            } else {

            }
        } else {

        }
    }

    private void showDriverArrived() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Driver Has Arrived");
        builder.setMessage("Hi, your driver has arrived");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public interface DataFromActivityToFragment {
        void sendData(String title, String content);
        void sendCompleteRideInfo(String title, String content,String ref, String pickup, String dropoff);
    }
    private void displayNoDriversAvailable() {
        mMap.clear();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.no_driver);
        builder.setMessage(R.string.no_driver_message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showPaymentDialog(String content) {
        if (countrycode.equals("KE")){
            defaultcurrency = getResources().getString(R.string.currency_in_kenya);
            faretable = "fares";
        }
        else{
            defaultcurrency = getResources().getString(R.string.currency_in_congo);
            faretable = "faresindollars";
        }
        pricetag.setText(defaultcurrency + content);
        driverLayout.setVisibility(View.GONE);
        paymentDialog.setVisibility(View.VISIBLE);
    }

    private void submitRating() {
        DatabaseReference driverRating = database.getReference().child("driverRating").child(driverId).child(completerideref).child("rating");
        driverRating.setValue(rating);
    }

    private void makePayment() {
        ratingDialog.setVisibility(View.VISIBLE);
    }

    //Get Last Location
    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    mLastKnownLocation = location;

                    if (mLastKnownLocation != null) {
                        try {
                            // Set the map's camera position to the current location of the device.
                            initialLat = mLastKnownLocation.getLatitude();
                            initialLng = mLastKnownLocation.getLongitude();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            if (mLastKnownLocation != null) {
                                getLocationInfo(initialLat,initialLng);
                            }
                        } catch (SecurityException e) {

                        }
                    } else {
                        if (mMap != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 10));
                        }
                    }
                }
            });
        }
    }


    /*----------------------------------Place autocomplete----------------------------*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
         /*-----------------Results for place search-----------*/
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_SEARCH) {
            if (resultCode == RESULT_OK) {
                if (directionLat == 0.0 && directionLng == 0.0) {
                    if (currentLat != 0.0 && currentLng != 0.0) {
                        directionLat = currentLat;
                        directionLng = currentLng;
                    } else {
                        directionLat = initialLat;
                        directionLng = initialLng;
                    }
                }
                Place place = PlaceAutocomplete.getPlace(MainActivity.this, data);

                LatLng placeLatLng = place.getLatLng();
                destinationLat = placeLatLng.latitude;
                destinationLng = placeLatLng.longitude;
                placeId = place.getId();
                placeName = place.getName();
                if (placeName != null) {
                    searchBtn.setText(placeName);
                    mDestination.setText(placeName);
                }
                if (placeId != null && destinationLng != 0.0 && destinationLat != 0.0 && directionLng != 0.0 && directionLat != 0.0) {
                    directionsLatLng = new LatLng(directionLat, directionLng);
                    if (start == null){
                        start = riderLatLng;
                    }
                    end = new LatLng(destinationLat,destinationLng);
                    //getRouteDirections(directionsLatLng);
                    getRouteToDestination(end);
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(MainActivity.this, data);
                // TODO: Handle the error.

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        /*-----------------Results for pickup place search-----------*/
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKUP) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(MainActivity.this, data);

                LatLng placeLatLng = place.getLatLng();
                pickupLat = placeLatLng.latitude;
                pickupLng = placeLatLng.longitude;
                //placeId = place.getId();
                placeName = place.getName();
                if (placeName != null) {
                    address = placeName.toString();
                    mSource.setText(address);
                }
                directionLng = pickupLng;
                directionLat = pickupLat;
                directionsLatLng = new LatLng(directionLat, directionLng);
                pickUpLatLng = directionsLatLng;
                start = directionsLatLng;
                if (placeId != null && destinationLng != 0.0 && destinationLat != 0.0 && directionLng != 0.0 && directionLat != 0.0) {
                    if (end != null){
                        getRouteToDestination(end);
                    }
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(MainActivity.this, data);
                // TODO: Handle the error.


            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        /*-----------------Results for destination place search-----------*/
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION) {
            if (resultCode == RESULT_OK) {
                if (directionLat == 0.0 && directionLng == 0.0) {
                    if (currentLat != 0.0 && currentLng != 0.0) {
                        directionLat = currentLat;
                        directionLng = currentLng;
                    } else {
                        directionLat = initialLat;
                        directionLng = initialLng;
                    }
                }
                Place place = PlaceAutocomplete.getPlace(MainActivity.this, data);

                LatLng placeLatLng = place.getLatLng();
                destinationLat = placeLatLng.latitude;
                destinationLng = placeLatLng.longitude;
                placeId = place.getId();
                placeName = place.getName();
                if (placeName != null) {
                    searchBtn.setText(placeName);
                    mDestination.setText(placeName);
                }
                if (placeId != null && destinationLng != 0.0 && destinationLat != 0.0 && directionLng != 0.0 && directionLat != 0.0) {
                    directionsLatLng = new LatLng(directionLat, directionLng);
                    if(start == null) {
                        start = riderLatLng;
                    }
                    end = new LatLng(destinationLat,destinationLng);
                    //getRouteDirections(directionsLatLng);
                    getRouteToDestination(end);
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(MainActivity.this, data);
                // TODO: Handle the error.


            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
    /*--------------------------------------End-----------------------------------------*/
   /*-----------------Permissions-----------------------------------*/
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(false);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
        }
        updateLocationUI();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;
        } else {
            return true;
        }
    }
    /*------------------- end of permissions ------------*/

    /*-----------------Make a ride request-----------*/

    private void riderRequest() {
        if (pickUpLatLng == null) {
            pickUpLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
        DatabaseReference riderRequest = database.getReference("riderRequest");
        String request = riderRequest.push().getKey();
        GeoFire geoFire = new GeoFire(riderRequest);
        geoFire.setLocation(request, new GeoLocation(pickUpLatLng.latitude, pickUpLatLng.longitude));
        DatabaseReference ref = database.getReference().child("riderRequest").child(request);
        HashMap<String, Object> map = new HashMap<>();
        map.put("riderDestination", placeName);
        map.put("riderid", userID);
        map.put("ridername", ridername);
        map.put("riderphone", riderphone);
        map.put("riderLocation", address);
        map.put("paymentMethod", paymentMethod);
        map.put("destinationLat", destinationLat);
        map.put("destinationLng", destinationLng);
        map.put("timestamp", getCurrentTimestamp());
        ref.updateChildren(map);

        if (mMap != null) {
            pickUpMarker = mMap.addMarker(new MarkerOptions().position(pickUpLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.destination)).title(getString(R.string.pick_up_here)));
        }
        //getClosestDriver(request);
        findDriver(request);
    }
    GeoFire geoFire;
    private int searchRadius = 1;
    private int max = 15;
    GeoQuery searchGeoquery;
    Boolean newDriverFound = false;
    private String foundDriverId;

    private void getClosestDriver(final String requestId){
        final DatabaseReference driverLocation = database.getReference().child("driversavailable");
        geoFire = new GeoFire(driverLocation);
        searchGeoquery = geoFire.queryAtLocation(new GeoLocation(pickUpLatLng.latitude, pickUpLatLng.longitude), searchRadius);
        searchGeoquery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!newDriverFound){
                    newDriverFound = true;
                    foundDriverId = key;
                    DatabaseReference matched = database.getReference().child("matched").child(requestId);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("rider",userID);
                    map.put("driver",foundDriverId);
                    matched.updateChildren(map).addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            final DatabaseReference rejected =  database.getReference().child("rejected").child(requestId).child(foundDriverId).child(userID);
                             rejected.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        displayNoDriversAvailable();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!newDriverFound){
                    if (searchRadius < max){
                        searchRadius++;
                        getClosestDriver(requestId);
                    }else{
                        mMap.clear();
                        displayNoDriversAvailable();
                    }
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (checkLocationPermission()) {
                mMap.setMyLocationEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 15));
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {

        }
    }
    /*-----------------------------------Map Activity-----------------------------------*/
    private void getRouteToDestination(LatLng destinationLatLng) {
        if (start != null){
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(start, destinationLatLng)
                    .build();
            routing.execute();
        }
        else{
            Toast.makeText(MainActivity.this, R.string.unable_to_get_location, Toast.LENGTH_SHORT).show();
        }

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

            //Toast.makeText(mContext,"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
            int seconds =  route.get(i).getDurationValue();
            duration = seconds / 60;
            int totalDistance =  route.get(i).getDistanceValue();
            distance = totalDistance / 1000;
            getEstimate(distance);
        }
        Marker marker_one,marker_two;
        // End marker
        MarkerOptions options = new MarkerOptions();
        options.snippet(Integer.toString(distance) + "  km");
        options.position(end);
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.destination));
        CustomInfoWindowAdapter adapter_two = new CustomInfoWindowAdapter(MainActivity.this);
        mMap.setInfoWindowAdapter(adapter_two);
        marker_one = mMap.addMarker(options);
        marker_one.showInfoWindow();

        // Start marker
        options.position(start);
        options.snippet(Integer.toString(duration) + "  min");
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.source));
        CustomInfoWindowAdapter adapter_one = new CustomInfoWindowAdapter(MainActivity.this);
        mMap.setInfoWindowAdapter(adapter_one);
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

    private void getEstimate(final int total_distance) {
        if (countrycode.equals("KE")){
            defaultcurrency = getResources().getString(R.string.currency_in_kenya);
            faretable = "fares";
        }
        else{
            defaultcurrency = getResources().getString(R.string.currency_in_congo);
            faretable = "faresindollars";
        }
        DatabaseReference fares = database.getReference().child(faretable);
        fares.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("base") != null) {
                        base_fare = Double.parseDouble(map.get("base").toString());
                    }
                    if (map.get("min") != null) {
                        min_fare = Double.parseDouble(map.get("min").toString());
                    }
                    if (map.get("perkm") != null) {
                        per_km = Double.parseDouble(map.get("perkm").toString());
                    }
                    if (map.get("estrange") != null) {
                        range = Double.parseDouble(map.get("estrange").toString());
                    }
                    if (base_fare != 0 && per_km != 0){
                        total_estimate = (total_distance * per_km) + base_fare + range;
                        estimate.setText(defaultcurrency + "  " + String.format( "%.2f", total_estimate) );
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        connectRider();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        currentLat = location.getLatitude();
        currentLng = location.getLongitude();
        riderLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (riderLatLng != null) {
            if (mLastLocation != location) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(riderLatLng));
            }
            getLocationInfo(currentLat,currentLng);
            driversAvailable = database.getReference(Common.drivers_available);
            driversAvailable.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    loadAllAvailableDrivers();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 150);
        }
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(MainActivity.this, R.raw.mapstyle));

            if (!success) {

            }
        } catch (Resources.NotFoundException e) {

        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }
    private void connectRider() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }
    private void erasepolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }
    /*-------------------- Location Updates ----------------


     -----------------Current Location information-----------*/
    public void getLocationInfo(Double newLat, Double newLng) {
        String placeUrl;
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/geocode/json?");
        googlePlaceUrl.append("latlng=" + newLat + "," + newLng);
        //googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=" + "AIzaSyBvfzFgDQZQjThxa3MgCUczuKTreOWsAkk");
        //googlePlaceUrl.append("&key=" + "AIzaSyAJIa-FpquMtKsHGpDJ9uOtHlXK9wa9XVI");
        placeUrl = googlePlaceUrl.toString();
        //String to place our result in
        String result;

        //Instantiate new instance of our class
        GetPlace getRequest = new GetPlace();

        //Perform the doInBackground method, passing in our url
        try {
            result = getRequest.execute(placeUrl).get();
            getAddress(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public  void getAddress(String jsonResponse){
        try {
            if (jsonResponse != null) {
                ret = new JSONObject(jsonResponse);
                location = ret.getJSONArray("results").getJSONObject(0);
                location_string = location.getString("formatted_address");
                address = location_string;
                if (address != null){
                    mSource.setText(address);
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }
    /*--------------------------End--------------------------------*/
        /*-----------------Retrieve driver details-----------*/
    private void getDriverDetails(String driverid) {
        DatabaseReference driverDetails = database.getReference().child("Users").child("Drivers").child(driverid);
        driverDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        driver_name = map.get("name").toString();
                        driverName.setText(driver_name);
                        txtDriver.setText(driver_name);
                    }
                    if (map.get("phone") != null) {
                        driver_phone = map.get("phone").toString();
                    }
                    if (map.get("ratings") != null) {
                        driver_ratings = map.get("ratings").toString();
                        ratings.setText(driver_ratings);
                        txtRating.setText(driver_ratings);
                    }
                    if (map.get("carType") != null) {
                        car_type = map.get("carType").toString();
                        carType.setText(car_type);
                    }
                    if (map.get("licPlate") != null) {
                        driver_plate = map.get("licPlate").toString();
                        licPlate.setText(driver_plate);
                    }
                    if (map.get("ProfileImageUrl") != null) {
                        driverImage = map.get("ProfileImageUrl").toString();
                        Picasso.with(MainActivity.this).load(driverImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(driver_image, new Callback() {
                            @Override
                            public void onSuccess() {}
                            @Override
                            public void onError() {
                                Picasso.with(MainActivity.this).load(driverImage).placeholder(R.drawable.profile).into(driver_image);
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    /*-----------------Find the drivers location-----------*/
    private void getDriverLocation() {
        DatabaseReference driverLocationRef = database.getReference().child("driverEnroute").child(foundDriverId).child("l");
        driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;

                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    if (driverMarker != null) {
                        driverMarker.remove();
                    }
                    DatabaseReference driverBearing = database.getReference().child("driverAvailable").child(foundDriverId).child("bearing");
                    driverBearing.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                bearing = Float.parseFloat(dataSnapshot.getValue().toString());
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                    driverLatLng = new LatLng(locationLat, locationLng);
                    if (driverLatLng != null && riderLatLng != null) {
                        Location loc1 = new Location("");
                        loc1.setLatitude(driverLatLng.latitude);
                        loc1.setLongitude(driverLatLng.longitude);
                        Location loc2 = new Location("");
                        loc2.setLatitude(riderLatLng.latitude);
                        loc2.setLongitude(riderLatLng.longitude);
                        mDistance = loc1.distanceTo(loc2) / 1000;
                        if (mDistance >= 1) {
                            String dist = String.format("%.1f", mDistance);
                            driverETA.setText(getString(R.string.your_driver) + " " + dist + getString(R.string.away));
                        }
                        if (mDistance < 1) {
                            float driverDistance = mDistance * 1000;
                            String driverdist = String.format("%.1f", driverDistance);
                            driverETA.setText(getString(R.string.your_driver) + " "  + driverdist + getString(R.string.away));
                        }
                        if (mDistance < 0.5) {
                            driverETA.setText(R.string.arrived);
                            new CountDownTimer(20000, 1000) {

                                public void onTick(long millisUntilFinished) {

                                }
                                public void onFinish() {
                                    driverLayout.setVisibility(View.GONE);
                                }
                            }.start();

                        }
                    }
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(driverLatLng);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.car));
                    markerOptions.rotation(bearing);
                    markerOptions.flat(true);
                    driverMarker = mMap.addMarker(markerOptions);
                    driverMarker.setRotation(bearing);
                    cancelRideBtn.setVisibility(View.GONE);
                    requestBtn.setVisibility(View.GONE);
                    driverLayout.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void loadAllAvailableDrivers(){
        DatabaseReference driversavailable = database.getReference().child(Common.drivers_available);
        GeoFire geoFireDriver = new GeoFire(driversavailable);
        GeoQuery geoQuery = geoFireDriver.queryAtLocation(new GeoLocation(riderLatLng.latitude, riderLatLng.longitude), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                    driverFoundId = key;
                DatabaseReference driverLocated = database.getReference().child(Common.drivers_available).child(driverFoundId).child("l");
                driverLocated.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            List<Object> map = (List<Object>) dataSnapshot.getValue();
                            double locationLat = 0;
                            double locationLng = 0;

                            if (map.get(0) != null) {
                                locationLat = Double.parseDouble(map.get(0).toString());
                            }
                            if (map.get(1) != null) {
                                locationLng = Double.parseDouble(map.get(1).toString());
                            }
                            if (driverMarker != null) {
                                driverMarker.remove();
                            }
                            DatabaseReference driverBearing = database.getReference().child("driverBearing").child(driverFoundId).child("bearing");
                            driverBearing.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        bearing = Float.parseFloat(dataSnapshot.getValue().toString());
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                            driverLatLng = new LatLng(locationLat, locationLng);
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(driverLatLng);
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.car));
                            markerOptions.rotation(bearing);
                            markerOptions.flat(true);
                            if (driverMarker != null){
                                driverMarker.remove();
                            }
                            driverMarker = mMap.addMarker(markerOptions);
                            driverMarker.setRotation(bearing);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });


            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                    if (radius <= maxRadius ){
                        radius ++;
                        loadAllAvailableDrivers();
                    }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
    private void findDriver(final String requestid){
        if (pickUpLatLng == null) {
            pickUpLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
        DatabaseReference driversavailable = database.getReference().child("driversavailable");
        GeoFire geoFireDriver = new GeoFire(driversavailable);
        GeoQuery geoQuery = geoFireDriver.queryAtLocation(new GeoLocation(pickUpLatLng.latitude, pickUpLatLng.longitude), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound){
                    driverFound = true;
                    driverFoundId = key;
                    sendDriverRequest(driverFoundId, requestid);
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                    if(!driverFound){
                        if (radius <= maxRadius ){
                            radius ++;
                            findDriver(requestid);
                        }else{
                            mMap.clear();
                            displayNoDriversAvailable();
                        }
                    }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void sendDriverRequest(String driverFoundId, final String requestid) {
        if (pickUpLatLng == null) {
            pickUpLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
        DatabaseReference tokens = database.getReference(Common.notifications);
        tokens.orderByKey().equalTo(driverFoundId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                            Token token = postSnapshot.getValue(Token.class);
                            Log.d(TAG, "onDataChange: "+token.getToken());
                            Log.d(TAG, "onDataChange: "+Common.userrider.getName());
                            Log.d(TAG, "onDataChange: "+Common.userrider.getPhone());
                            String json_lat_lng = new Gson().toJson(new LatLng(pickUpLatLng.latitude, pickUpLatLng.longitude));
                            String to_lat_lng = new Gson().toJson(new LatLng(destinationLat, destinationLng));
                            Log.d(TAG, "onDataChange: "+to_lat_lng);
                            Log.d(TAG, "onDataChange: "+json_lat_lng);
                            String ridertoken = FirebaseInstanceId.getInstance().getToken();
                            Map<String,String> data = new HashMap<>();
                            data.put("title","Ride Request");
                            data.put("message",String.format("%s has sent you a request", Common.userrider.getName()));
                            data.put("phone",Common.userrider.getPhone());
                            data.put("name",Common.userrider.getName());
                            data.put("location", json_lat_lng);
                            data.put("destination",to_lat_lng);
                            data.put("ridertoken",ridertoken);
                            data.put("pushid",requestid);
                            data.put("riderid",userID);
                            DataMessage content = new DataMessage(token.getToken(), data);
                            Log.d(TAG, "onDataChange: "+content);
                            mService.sendMessage(content)
                                    .enqueue(new retrofit2.Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                            if (response.body().success == 1){
                                                Toast.makeText(MainActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                                            }else{
                                                try {
                                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                                    Toast.makeText(MainActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                                                    Log.e(TAG, jObjError.getString("message") );
                                                } catch (Exception e) {
                                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                                            Log.e(TAG, "onFailure:  "+t.getMessage() );
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //    //Show Drivers Available
//    public void driverAvailable() {
//        DatabaseReference driverLocation = database.getReference().child("driversavailable");
//        GeoFire geoFire = new GeoFire(driverLocation);
//        geoQuery = geoFire.queryAtLocation(new GeoLocation(riderLatLng.latitude, riderLatLng.longitude), radius);
//        geoQuery.removeAllListeners();
//        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
//            @Override
//            public void onKeyEntered(String key, GeoLocation location) {
//                driverFoundId = key;
//                DatabaseReference driverLocated = database.getReference().child("driversavailable").child(driverFoundId).child("l");
//                driverLocated.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            List<Object> map = (List<Object>) dataSnapshot.getValue();
//                            double locationLat = 0;
//                            double locationLng = 0;
//
//                            if (map.get(0) != null) {
//                                locationLat = Double.parseDouble(map.get(0).toString());
//                            }
//                            if (map.get(1) != null) {
//                                locationLng = Double.parseDouble(map.get(1).toString());
//                            }
//                            if (driverMarker != null) {
//                                driverMarker.remove();
//                            }
//                            DatabaseReference driverBearing = database.getReference().child("driverBearing").child(driverFoundId).child("bearing");
//                            driverBearing.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.exists()) {
//                                        bearing = Float.parseFloat(dataSnapshot.getValue().toString());
//                                    }
//                                }
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {}
//                            });
//                            driverLatLng = new LatLng(locationLat, locationLng);
//                            MarkerOptions markerOptions = new MarkerOptions();
//                            markerOptions.position(driverLatLng);
//                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.car));
//                            markerOptions.rotation(bearing);
//                            markerOptions.flat(true);
//                            driverMarker = mMap.addMarker(markerOptions);
//                            driverMarker.setRotation(bearing);
//                        }
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {}
//                });
//
//            }
//            @Override
//            public void onKeyExited(String key) {}
//            @Override
//            public void onKeyMoved(String key, GeoLocation location) {}
//            @Override
//            public void onGeoQueryReady() {
//                if (radius < maxRadius) {
//                    radius++;
//                } else {
//                    return;
//                }
//            }
//            @Override
//            public void onGeoQueryError(DatabaseError error) {
//            }
//        });
//    }
    private Long getCurrentTimestamp() {
        Long timestamp = System.currentTimeMillis()/1000;
        return timestamp;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient==null) {
            if (!mGoogleApiClient.isConnected()) {
                buildGoogleApiClient();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager .getInstance(this).registerReceiver(mHandler,new IntentFilter("ke.co.struct.chauffeur"));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LocalBroadcastManager .getInstance(this).registerReceiver(mHandler,new IntentFilter("ke.co.struct.chauffeur"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }
}