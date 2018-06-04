package ke.co.struct.chauffeurrider.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import ke.co.struct.chauffeurrider.R;

public class NameUpdateActivity extends AppCompatActivity {
    private EditText surname;
    private Button save;
    private String str_name,userId;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ProgressDialog mProgress;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_update);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.name_update);
        ab.setDisplayHomeAsUpEnabled(true);
        checkNetworkStatus();
        mProgress = new ProgressDialog(this);
        mProgress.setCancelable(false);
        userId = mAuth.getCurrentUser().getUid();
        surname = findViewById(R.id.name);
        save = findViewById(R.id.save);
        if (isNetworkAvailable()) {
            DatabaseReference dbref = database.getReference().child("Users").child("Riders").child(userId);
            dbref.keepSynced(true);
            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map.get("name") != null) {
                            str_name = map.get("name").toString();
                            surname.setText(str_name);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.setMessage(getString(R.string.updating_surname));
                mProgress.show();
                str_name = surname.getText().toString();
                if (str_name != null) {
                    if (isNetworkAvailable()) {
                        DatabaseReference updateid = database.getReference().child("Users").child("Riders").child(userId);
                        Map<String, Object> update = new HashMap<>();
                        update.put("name", str_name);
                        updateid.updateChildren(update);
                        mProgress.dismiss();
                        Toast.makeText(NameUpdateActivity.this, R.string.name_updated,Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mProgress.dismiss();
                        Toast.makeText(NameUpdateActivity.this, R.string.offline, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public class Retry implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            checkNetworkStatus();
        }
    }
    private void checkNetworkStatus(){
        if (!isNetworkAvailable()){
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.name_layout),
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
