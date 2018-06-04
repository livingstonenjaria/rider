package ke.co.struct.chauffeurrider.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

import java.util.HashMap;
import java.util.Map;

import ke.co.struct.chauffeurrider.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CardActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ValueEventListener statusevent,carddetails;
    Toolbar toolbar = null;
    ActionBar ab;
    private Button save;
    private CardMultilineWidget cardMultilineWidget;
    private String TAG = "Stripe";
    private String userId, cvv, card_num,email,token_id,msg,status,brand,lastdigits;
    private Integer exp, exp_year;
    private Card card,cardtosave;
    private ProgressDialog mProgress;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setTitle("Card Option");
        ab.setDisplayHomeAsUpEnabled(true);
        mProgress = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        mProgress.setCancelable(false);
        userId = mAuth.getCurrentUser().getUid();
        cardMultilineWidget = findViewById(R.id.card_input_widget);
        save = findViewById(R.id.card_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setMessage("Verifying Card ....");
                mProgress.show();
                cardtosave = cardMultilineWidget.getCard();
                if (cardtosave == null) {
                    Toast.makeText(CardActivity.this, "Please fill all required details", Toast.LENGTH_SHORT).show();
                }
                else {
                cvv = cardMultilineWidget.getCard().getCVC();
                exp = cardMultilineWidget.getCard().getExpMonth();
                exp_year = cardMultilineWidget.getCard().getExpYear();
                card_num = cardMultilineWidget.getCard().getNumber();
                if (!exp.equals(0) && !exp_year.equals(0) && cvv != null && card_num != null) {
                    card = new Card(card_num, exp, exp_year, cvv);
                    boolean validation = card.validateCard();
                    if (validation) {
                        new Stripe(getApplicationContext(), "pk_test_B1F4czz8kDJ7VsXGN8hFnhuR")
                                .createToken(card, new TokenCallback() {
                                    @Override
                                    public void onError(Exception error) {
                                        Toast.makeText(getApplicationContext(),
                                                error.getLocalizedMessage(),
                                                Toast.LENGTH_LONG).show();
                                        mProgress.dismiss();
                                    }

                                    @Override
                                    public void onSuccess(Token token) {
                                        Toast.makeText(CardActivity.this, "Token is: " + token.getId(), Toast.LENGTH_SHORT).show();
                                        token_id = token.getId();
                                        if (!token_id.isEmpty()) {
                                            DatabaseReference paymentref = database.getReference().child("Users").child("Riders").child(userId);
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("paymenttoken", token.getId());
                                            paymentref.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    final DatabaseReference paymentrefstatus = database.getReference().child("Users").child("Riders").child(userId).child("customerpaymentidstatus");
                                                    statusevent = paymentrefstatus.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                                                if (map.get("cardstatus") != null) {
                                                                    status = map.get("cardstatus").toString();
                                                                    if (status.equals("error")) {
                                                                        mProgress.dismiss();
                                                                        if (map.get("msg") != null) {
                                                                            msg = map.get("msg").toString();
                                                                            AlertDialog.Builder builder = new AlertDialog.Builder(CardActivity.this);
                                                                            builder.setMessage(msg);
                                                                            builder.setCancelable(false);
                                                                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                                    Intent intent = getIntent();
                                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                                                    DatabaseReference paymentstatus = database.getReference().child("Users").child("Riders").child(userId).child("customerpaymentidstatus");
                                                                                    paymentstatus.setValue(null);
                                                                                    paymentrefstatus.removeEventListener(statusevent);
                                                                                    startActivity(intent);
                                                                                    finish();
                                                                                    dialogInterface.dismiss();
                                                                                }
                                                                            });
                                                                            AlertDialog dialog = builder.create();
                                                                            dialog.show();
                                                                        }
                                                                    }
                                                                    if (status.equals("success")) {
                                                                        mProgress.dismiss();
                                                                        if (map.get("msg") != null) {
                                                                            msg = map.get("msg").toString();
                                                                            AlertDialog.Builder builder = new AlertDialog.Builder(CardActivity.this);
                                                                            builder.setMessage(msg);
                                                                            builder.setCancelable(false);
                                                                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                                    Intent intent = getIntent();
                                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                                                    DatabaseReference paymentstatus = database.getReference().child("Users").child("Riders").child(userId).child("customerpaymentidstatus");
                                                                                    paymentstatus.setValue(null);
                                                                                    paymentrefstatus.removeEventListener(statusevent);
                                                                                    startActivity(intent);
                                                                                    finish();
                                                                                    dialogInterface.dismiss();
                                                                                }
                                                                            });
                                                                            AlertDialog dialog = builder.create();
                                                                            dialog.show();
                                                                        }
                                                                    }
                                                                }
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
                                });
                    } else if (!card.validateNumber()) {
                        Toast.makeText(CardActivity.this, "The card number that you entered is invalid", Toast.LENGTH_SHORT).show();
                    } else if (!card.validateExpiryDate()) {
                        Toast.makeText(CardActivity.this, "The expiration date that you entered is invalid", Toast.LENGTH_SHORT).show();
                    } else if (!card.validateCVC()) {
                        Toast.makeText(CardActivity.this, "The CVC code that you entered is invalid", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CardActivity.this, "The card details that you entered are invalid", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mProgress.dismiss();
                    Toast.makeText(CardActivity.this, "Card is null", Toast.LENGTH_SHORT).show();
                }
            }
            }
        });
    }
}
