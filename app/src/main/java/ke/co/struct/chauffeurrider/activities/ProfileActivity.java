package ke.co.struct.chauffeurrider.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import ke.co.struct.chauffeurrider.MainActivity;
import ke.co.struct.chauffeurrider.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity
//        implements NavigationView.OnNavigationItemSelectedListener
{
    ActionBarDrawerToggle toggle;
    Toolbar toolbar = null;
    DrawerLayout drawer;
    NavigationView navigationView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ProgressDialog mProgress;
    private EditText password;
    private TextView full_name,surname,phone,email,national_id,countrycode;
    private ImageView profile_image;
    private String str_password,str_surname,str_phone,str_name,str_email,str_id,str_profile_imgurl,userId,code, selectedcode, currency;
    private RelativeLayout name_layout, surname_layout, phone_layout, id_layout, email_layout, password_layout;
    private CountryCodePicker ccp;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Profile");
        ab.setDisplayHomeAsUpEnabled(true);
        userId = mAuth.getCurrentUser().getUid();
        password = findViewById(R.id.password);
        password.setFocusable(false);
        full_name = findViewById(R.id.name);
        surname =findViewById(R.id.surname);
        phone =findViewById(R.id.phone);
        national_id =findViewById(R.id.national_id);
        email =findViewById(R.id.email);
        profile_image =findViewById(R.id.profile);
        phone_layout = findViewById(R.id.phone_layout);
        id_layout = findViewById(R.id.id_layout);
        email_layout = findViewById(R.id.email_layout);
        name_layout = findViewById(R.id.name_layout);
        surname_layout = findViewById(R.id.surname_layout);
        password_layout = findViewById(R.id.password_layout);
        countrycode = findViewById(R.id.country_code);
        ccp = findViewById(R.id.ccp1);
        mProgress = new ProgressDialog(this);
        mProgress.setCancelable(false);
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent image = new Intent(ProfileActivity.this, ImageUpdateActivity.class);
                startActivity(image);
                finish();
            }
        });
        phone_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phone = new Intent(ProfileActivity.this, PhoneUpdateActivity.class);
                startActivity(phone);
                finish();

            }
        });
        name_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent name = new Intent(ProfileActivity.this, NameUpdateActivity.class);
                startActivity(name);
                finish();

            }
        });
        surname_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent surname = new Intent(ProfileActivity.this, SurnameUpdateActivity.class);
                startActivity(surname);
                finish();

            }
        });
        id_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent id = new Intent(ProfileActivity.this, IDUpdateActivity.class);
                startActivity(id);
                finish();

            }
        });
        email_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(ProfileActivity.this, EmailUpdateActivity.class);
                startActivity(email);
                finish();

            }
        });
        password_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent password = new Intent(ProfileActivity.this, PasswordUpdateActivity.class);
                startActivity(password);
                finish();
            }
        });
        getUserInfo();
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                mProgress.setMessage(getString(R.string.updating_code));
                mProgress.show();
                selectedcode = ccp.getSelectedCountryNameCode();
                if (selectedcode != null) {
                    if (selectedcode.equals("KE")){
                        currency = getResources().getString(R.string.currency_in_kenya);
                    }
                    if (countrycode.equals("CD")){
                        currency = getResources().getString(R.string.currency_in_congo);
                    }
                    if (isNetworkAvailable()) {
                        DatabaseReference updateid = database.getReference().child("Users").child("Riders").child(userId);
                        Map<String, Object> update = new HashMap<>();
                        update.put("countrycode", selectedcode);
                        update.put("currency", currency);
                        updateid.updateChildren(update);
                        mProgress.dismiss();
                        Toast.makeText(ProfileActivity.this, R.string.code_updated, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mProgress.dismiss();
                        Toast.makeText(ProfileActivity.this, R.string.offline, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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
    /*----------------------------------Get profile info--------------------------------------*/
    private void getUserInfo() {
        DatabaseReference dbref = database.getReference().child("Users").child("Riders").child(userId);
        dbref.keepSynced(true);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        str_name = map.get("name").toString();
                        full_name.setText(str_name);
                    }
                    if (map.get("phone") != null) {
                        str_phone = map.get("phone").toString();
                        phone.setText(str_phone);
                    }
                    if (map.get("surname") != null) {
                        str_surname = map.get("surname").toString();
                        surname.setText(str_surname);
                    }
                    if (map.get("id") != null) {
                        str_id = map.get("id").toString();
                        national_id.setText(str_id);
                    }
                    if (map.get("email") != null) {
                        str_email = map.get("email").toString();
                        email.setText(str_email);
                    }
                    if (map.get("password") != null) {
                        str_password = map.get("password").toString();
                        password.setText(str_password);
                    }
                    if (map.get("countrycode") != null) {
                        code = map.get("countrycode").toString();
                        countrycode.setText(code);
                        ccp.setDefaultCountryUsingNameCode(code);
                    }
                    if (map.get("ProfileImageUrl") != null) {
                        str_profile_imgurl = map.get("ProfileImageUrl").toString();
                        Picasso.with(ProfileActivity.this).load(str_profile_imgurl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(profile_image, new Callback() {
                            @Override
                            public void onSuccess() {
                            }
                            @Override
                            public void onError() {
                                Picasso.with(ProfileActivity.this).load(str_profile_imgurl).placeholder(R.drawable.profile).into(profile_image);
                            }
                        });
                    }
                }
                ccp.setDefaultCountryUsingNameCode(code);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    /*-----------------------------------------End-----------------------------------------*/
    public class Retry implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            checkNetworkStatus();
        }
    }
    private void checkNetworkStatus(){
        if (!isNetworkAvailable()){
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.profile_layout),
                    R.string.offline, Snackbar.LENGTH_INDEFINITE);
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
}
