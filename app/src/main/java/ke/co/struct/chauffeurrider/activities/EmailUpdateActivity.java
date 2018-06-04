package ke.co.struct.chauffeurrider.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import ke.co.struct.chauffeurrider.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EmailUpdateActivity extends AppCompatActivity {
    private static  final String TAG ="passwordupdate";
    private EditText email;
    private Button save;
    private  String newEmail;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private  String userId, str_email, str_password;
    ProgressDialog mProgress;
    Toolbar toolbar;
    FirebaseUser user;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_update);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.password_update);
        ab.setDisplayHomeAsUpEnabled(true);
        checkNetworkStatus();
        userId = mAuth.getCurrentUser().getUid();
        user = mAuth.getCurrentUser();
        email = findViewById(R.id.email);
        save = findViewById(R.id.save);
        mProgress = new ProgressDialog(this);
        mProgress.setCancelable(false);
        DatabaseReference riderRef = database.getReference().child("Users").child("Riders").child(userId);
        riderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("email") != null) {
                        str_email = map.get("email").toString();
                        email.setText(str_email);
                    }
                    if (map.get("password") != null) {
                        str_password = map.get("password").toString();
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newEmail = email.getText().toString();
                if (!newEmail.isEmpty()) {
                    if (isNetworkAvailable()) {
                        mProgress.setMessage(getString(R.string.updating_password));
                        mProgress.show();
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(str_email, str_password);

                        // Prompt the user to re-provide their sign-in credentials
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            user.updateEmail(newEmail)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                DatabaseReference updatepassword = database.getReference().child("Users").child("Riders").child(userId);
                                                                Map<String, Object> update = new HashMap<>();
                                                                update.put("email", newEmail);
                                                                updatepassword.updateChildren(update);
                                                                Toast.makeText(EmailUpdateActivity.this, R.string.email_updated, Toast.LENGTH_SHORT).show();
                                                                mProgress.dismiss();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                    else{
                        mProgress.dismiss();
                        Toast.makeText(EmailUpdateActivity.this, R.string.offline, Toast.LENGTH_SHORT).show();
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
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.email_layout),
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