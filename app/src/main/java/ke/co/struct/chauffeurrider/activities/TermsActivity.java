package ke.co.struct.chauffeurrider.activities;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import ke.co.struct.chauffeurrider.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TermsActivity extends AppCompatActivity {
    Toolbar toolbar = null;;
    ActionBar ab;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setTitle("Terms and Condition");
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
