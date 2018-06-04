package ke.co.struct.chauffeurrider.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import ke.co.struct.chauffeurrider.MainActivity;
import ke.co.struct.chauffeurrider.R;
import ke.co.struct.chauffeurrider.adapters.RideHistoryAdapter;
import ke.co.struct.chauffeurrider.objects.HistoryObject;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HistoryActivity extends AppCompatActivity
//        implements NavigationView.OnNavigationItemSelectedListener
{
    ActionBarDrawerToggle toggle;
    Toolbar toolbar = null;
    DrawerLayout drawer;
    NavigationView navigationView;
    private RecyclerView mHistoryRecylerView;
    private RecyclerView.Adapter mHistoryAdapter;
    private RecyclerView.LayoutManager mHistoryLayoutManager;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String userId,rating,driverId,from,to,drivername,riderdate,ridertime,status,rideid;
    Long timestamp = 0L;
    ImageView empty;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//       drawer =  findViewById(R.id.history);
//       toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        navigationView =  findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setTitle("History");
        ab.setDisplayHomeAsUpEnabled(true);
        empty = findViewById(R.id.empty);
        mHistoryRecylerView = findViewById(R.id.historyRecyclerView);
        mHistoryRecylerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(HistoryActivity.this);
        mHistoryRecylerView.setLayoutManager(mLayoutManager);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mHistoryRecylerView.getContext(),
//                mHistoryLayoutManager.getOrientation());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(HistoryActivity.this,mLayoutManager.getOrientation());
        mHistoryRecylerView.addItemDecoration(dividerItemDecoration);
        mHistoryAdapter = new RideHistoryAdapter(getDataSetHistory(),HistoryActivity.this);
        mHistoryRecylerView.setAdapter(mHistoryAdapter);
        userId = mAuth.getCurrentUser().getUid();
        getRideHistory();
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.history);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        switch (id){
//            case  R.id.nav_profile:
//                Intent profile = new Intent(HistoryActivity.this, ProfileActivity.class);
//                startActivity(profile);
//                break;
//            case R.id.nav_history:
////                Intent history = new Intent(HistoryActivity.this, HistoryActivity.class);
////                startActivity(history);
//                break;
//            case R.id.nav_payment:
//                Intent payment = new Intent(HistoryActivity.this, PaymentActivity.class);
//                startActivity(payment);
//                break;
//            case R.id.nav_about:
//
//                break;
//            case R.id.nav_logout:
//
//                break;
//
//        }
//
//        DrawerLayout drawer =  findViewById(R.id.history);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
    /*------------------End of Navigation----------------------------*/
    /*--------------------------Get History-------------------------------*/
    private void getRideHistory() {
        DatabaseReference rideHistoryRef = database.getReference().child("Users").child("Riders").child(userId).child("rideHistory");
        rideHistoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot history : dataSnapshot.getChildren()){
                        FetchRideInformation(history.getKey());
                    }
                }else{
                    empty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void FetchRideInformation(final String ridekey) {
        DatabaseReference historyDB = database.getReference().child("rideHistory").child(ridekey);
        historyDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("driver") != null){
                        driverId = map.get("driver").toString();
                    }
                    if(map.get("from") != null){
                        from = map.get("from").toString();
                    }
                    if(map.get("to") != null){
                        to = map.get("to").toString();
                    }
                    if(map.get("timestamp") != null){
                        timestamp = Long.valueOf(map.get("timestamp").toString());
                    }
                    if(map.get("status") != null){
                        status = map.get("status").toString();
                    }
                    HistoryObject obj = new HistoryObject(from,to,getRideDate(timestamp),getRideTime(timestamp),ridekey,status);
                    resultsHistory.add(obj);
                    mHistoryAdapter.notifyDataSetChanged();
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
            String date = android.text.format.DateFormat.format("HH:mm", cal).toString();
            return date;
        }
        return null;
    }

    private String getRideDate(Long timestamp) {
        if (timestamp != 0.0){
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(timestamp*1000L);
            String date = android.text.format.DateFormat.format("dd-MM-yyyy", cal).toString();
            return date;
        }
        return null;
    }



    private ArrayList<HistoryObject> resultsHistory = new ArrayList<HistoryObject>();
    private ArrayList<HistoryObject> getDataSetHistory() {
        return resultsHistory;
    }
    /*--------------------------End of History----------------------------------*/

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        toggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        toggle.onConfigurationChanged(newConfig);
//    }
}
