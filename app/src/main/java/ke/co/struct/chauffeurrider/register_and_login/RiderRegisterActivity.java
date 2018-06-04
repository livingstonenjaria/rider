package ke.co.struct.chauffeurrider.register_and_login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

import ke.co.struct.chauffeurrider.MainActivity;
import ke.co.struct.chauffeurrider.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RiderRegisterActivity extends AppCompatActivity {
    private Button register;
    private TextView login,mTitle,emailError,passwordError;
    private EditText surname,name,phone,email,password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;
    private DatabaseReference registerRef;
    private FirebaseDatabase driverReg;
    private ProgressDialog mProgress;
    private String TAG ="register";
    private String countrycode,fullphone,currency;
    private CountryCodePicker ccp;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_register);
        checkNetworkStatus();
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();

                if (user!=null){
                    Intent intent = new Intent(RiderRegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    return;
                }
            }
        };
        mProgress = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        mProgress.setCancelable(false);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        surname = findViewById(R.id.surname);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mTitle = findViewById(R.id.mTitle);
        emailError = findViewById(R.id.emailError);
        passwordError = findViewById(R.id.passwordError);
        ccp = findViewById(R.id.ccp);
        emailError.setTextColor(Color.RED);
        passwordError.setTextColor(Color.RED);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/BadScript-Regular.ttf");
        mTitle.setTypeface(custom_font);

        Drawable drawable = getResources().getDrawable(R.drawable.mail);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.parseColor("#7F8C8D"));

        Drawable mdrawable = getResources().getDrawable(R.drawable.lock);
        mdrawable = DrawableCompat.wrap(mdrawable);
        DrawableCompat.setTint(mdrawable, Color.parseColor("#7F8C8D"));

        Drawable tdrawable = getResources().getDrawable(R.drawable.person);
        tdrawable = DrawableCompat.wrap(tdrawable);
        DrawableCompat.setTint(tdrawable, Color.parseColor("#7F8C8D"));

        Drawable kdrawable = getResources().getDrawable(R.drawable.phone);
        kdrawable = DrawableCompat.wrap(kdrawable);
        DrawableCompat.setTint(kdrawable, Color.parseColor("#7F8C8D"));

        email.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        password.setCompoundDrawablesWithIntrinsicBounds(mdrawable, null, null, null);
        surname.setCompoundDrawablesWithIntrinsicBounds(tdrawable, null, null, null);
        name.setCompoundDrawablesWithIntrinsicBounds(tdrawable, null, null, null);
        phone.setCompoundDrawablesWithIntrinsicBounds(kdrawable, null, null, null);
        ccp.registerCarrierNumberEditText(phone);
        ccp.setNumberAutoFormattingEnabled(true);
        ccp.isValidFullNumber();
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                Toast.makeText(RiderRegisterActivity.this, "Updated " + ccp.getSelectedCountryCode()+ "  " +ccp.getFullNumberWithPlus(), Toast.LENGTH_SHORT).show();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RiderRegisterActivity.this, RiderLoginActivity.class );
                startActivity(intent);
                return;
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (isNetworkAvailable()) {
                final String mSurname = surname.getText().toString();
                final String mName = name.getText().toString();
                final String mPhone = phone.getText().toString();
                final String mEmail = email.getText().toString();
                final String mPassword = password.getText().toString();
                if (!mSurname.isEmpty() && !mName.isEmpty() && !mPhone.isEmpty() && !mEmail.isEmpty() && !mPassword.isEmpty()) {
                    mProgress.setMessage("Registering...");
                    mProgress.show();
                    mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(RiderRegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                mProgress.dismiss();
                                try {
                                    throw task.getException();
                                }
                                // if user enters wrong email.
                                catch (FirebaseAuthWeakPasswordException weakPassword) {
                                    passwordError.setVisibility(View.VISIBLE);
                                    passwordError.setText(R.string.weak_pass);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            passwordError.setVisibility(View.GONE);
                                        }
                                    }, 5000);

                                }
                                // if user enters wrong password.
                                catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                    emailError.setVisibility(View.VISIBLE);
                                    emailError.setText(R.string.invalid_email);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            emailError.setVisibility(View.GONE);
                                        }
                                    }, 5000);
                                } catch (FirebaseAuthUserCollisionException existEmail) {
                                    emailError.setVisibility(View.VISIBLE);
                                    emailError.setText(R.string.existing_email);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            emailError.setVisibility(View.GONE);
                                        }
                                    }, 5000);
                                } catch (Exception e) {
                                }

                            } else {
                                fullphone = ccp.getFullNumberWithPlus();
                                countrycode = ccp.getSelectedCountryNameCode();
                                if (countrycode.equals("KE")){
                                    currency = getResources().getString(R.string.currency_in_kenya);
                                }
                                if (countrycode.equals("CD")){
                                    currency = getResources().getString(R.string.currency_in_congo);
                                }
                                mProgress.dismiss();
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = driverReg.getInstance().getReference().child("Users").child("Riders").child(user_id);
                                current_user_db.setValue(true);
                                Map<String, Object> riderInfo = new HashMap<>();
                                riderInfo.put("surname", mSurname);
                                riderInfo.put("name", mName);
                                riderInfo.put("email", mEmail);
                                riderInfo.put("phone", fullphone);
                                riderInfo.put("password", mPassword);
                                riderInfo.put("countrycode", countrycode);
                                riderInfo.put("currency", currency);
                                current_user_db.updateChildren(riderInfo);
                                Toast.makeText(RiderRegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(RiderRegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListner);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListner);
    }
    public class Retry implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            checkNetworkStatus();
        }
    }
    private void checkNetworkStatus(){
        if (!isNetworkAvailable()){
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.registerRootLayout), R.string.offline, Snackbar.LENGTH_INDEFINITE);
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

