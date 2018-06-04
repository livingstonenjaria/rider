package ke.co.struct.chauffeurrider.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Customer;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

import java.util.HashMap;
import java.util.Map;

import ke.co.struct.chauffeurrider.MainActivity;
import ke.co.struct.chauffeurrider.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PaymentActivity extends AppCompatActivity
     {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ValueEventListener carddetails;
    Toolbar toolbar = null;
    ActionBar ab;
    private Button pay, remove;
    private String userId, email, brand,lastdigits;
    private CardView cardoption, corporateoption;
    private Double payment;
    private EditText edt_payment;
    private ImageView cardsuccess,cashsuccess,corporatesuccess,cardimg;
    private TextView cardstatus, corporatestatus;

         @Override
         protected void attachBaseContext(Context newBase) {
             super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
         }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setTitle("Payments");
        ab.setDisplayHomeAsUpEnabled(true);
        userId = mAuth.getCurrentUser().getUid();
        cashsuccess = findViewById(R.id.cashsuccess);
        cardsuccess = findViewById(R.id.cardsuccess);
        corporatesuccess = findViewById(R.id.corporatesuccess);
        cardimg = findViewById(R.id.cardimg);
        cardstatus = findViewById(R.id.cardstatus);
        corporatestatus = findViewById(R.id.corporatestatus);
        remove = findViewById(R.id.remove);
        cashsuccess.setVisibility(View.VISIBLE);
        cardoption = findViewById(R.id.cardoption);
        corporateoption = findViewById(R.id.corporateoption);
        cardoption.setClickable(true);
        corporateoption.setClickable(true);
        corporateoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, CorporateActivity.class);
                startActivity(intent);
            }
        });
        cardoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, CardActivity.class);
                startActivity(intent);
            }
        });
        DatabaseReference getdetails = database.getReference().child("Users").child("Riders").child(userId);
        getdetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("email") != null) {
                        email = map.get("email").toString();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference removecard = database.getReference().child("Users").child("Riders").child(userId).child("customerpaymentid");
                removecard.setValue(null);
                DatabaseReference removetoken= database.getReference().child("Users").child("Riders").child(userId).child("paymenttoken");
                removetoken.setValue(null);
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });

        DatabaseReference getcardDetails = database.getReference().child("Users").child("Riders").child(userId).
                                                                        child("customerpaymentid").child("sources").child("data").child("0");
        carddetails = getcardDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("brand") != null) {
                        brand = map.get("brand").toString();
                        if (brand.equals("Visa")) {
                            cardimg.setImageResource(R.mipmap.visa);
                        }
                    }
                    if (map.get("brand") != null) {
                        brand = map.get("brand").toString();
                        if (brand.equals("MasterCard")) {
                            cardimg.setImageResource(R.mipmap.mastercard);
                        }
                    }
                    if (map.get("last4") != null) {
                        lastdigits = map.get("last4").toString();
                        cardstatus.setText("***" + lastdigits);
                        cardsuccess.setVisibility(View.VISIBLE);
                    }
                    remove.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        DatabaseReference checkrequests = database.getReference().child("Corporate Requests").child(userId);
        checkrequests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                   corporatestatus.setText("Processing Request");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
