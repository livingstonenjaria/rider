package ke.co.struct.chauffeurrider.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ke.co.struct.chauffeurrider.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CorporateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ValueEventListener statusevent,carddetails;
        Toolbar toolbar = null;
        ActionBar ab;
        Button addcompany, request, cancelrequest;
        Spinner companylist;
        EditText company, staffid,staffemail;
        ImageView staffpic;
        String name, userId,profile_image,emp_id, emp_email, option;
        FirebaseStorage imgStore;
        StorageReference filepath;
        ProgressDialog mProgress;
        RelativeLayout corporatelayout, requestmessage,requestlayout;
        private Uri resultUri;
        List<String> corporatelist = new ArrayList<String>();
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corporate);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setTitle("Corporate");
        ab.setDisplayHomeAsUpEnabled(true);
        mProgress = new ProgressDialog(this);
        mProgress.setCancelable(false);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        staffid = findViewById(R.id.staffid);
        staffemail = findViewById(R.id.staffemail);
        staffpic = findViewById(R.id.staffpic);
        request = findViewById(R.id.request);
        cancelrequest = findViewById(R.id.cancelrequest);
//        company = findViewById(R.id.company);
        companylist = findViewById(R.id.companylist);
//        addcompany = findViewById(R.id.addcompany);
        corporatelayout = findViewById(R.id.corporatelayout);
        requestmessage = findViewById(R.id.requestmessage);
        requestlayout = findViewById(R.id.requestlayout);
        companylist.setOnItemSelectedListener(this);

//        addcompany.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                name = company.getText().toString();
//                if (!name.isEmpty()){
//                    DatabaseReference savecompany = database.getReference().child("Company");
//                    String unique = savecompany.push().getKey();
//                    savecompany.child(unique).setValue(name);
//                }
//            }
//        });
        DatabaseReference getCompany = database.getReference().child("Company");
        getCompany.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String companyname = areaSnapshot.getValue(String.class);
                    corporatelist.add(companyname);
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(CorporateActivity.this, android.R.layout.simple_spinner_item, corporatelist);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                companylist.setAdapter(dataAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        staffpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });
        DatabaseReference checkrequests = database.getReference().child("Corporate Requests").child(userId);
        checkrequests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    corporatelayout.setVisibility(View.GONE);
                    requestmessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageInfo();
            }
        });
        cancelrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestmessage.setVisibility(View.GONE);
                corporatelayout.setVisibility(View.VISIBLE);
                DatabaseReference cancel = database.getReference().child("Corporate Requests").child(userId);
                cancel.setValue(null);
            }
        });
    }
    private void saveImageInfo() {
        emp_email = staffemail.getText().toString();
        emp_id = staffid.getText().toString();
        if (resultUri != null && emp_id != null && emp_email != null){
            filepath = imgStore.getInstance().getReference().child("staffpics").child(userId);
            staffpic.setDrawingCacheEnabled(true);
            staffpic.buildDrawingCache();
            Bitmap bitmap =null;

//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            bitmap = staffpic.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    mProgress.setMessage("Sending Request...");
                    mProgress.show();
                }
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    DatabaseReference staff = database.getReference().child("Corporate Requests");
                    String pushid = staff.push().getKey();
                    Map newStaff = new HashMap();
                    newStaff.put("company",option);
                    newStaff.put("riderid",userId);
                    newStaff.put("staffid",emp_id);
                    newStaff.put("staffemail",emp_email);
                    newStaff.put("staffimage",downloadUrl.toString());
                    staff.child(userId).updateChildren(newStaff);
                    corporatelayout.setVisibility(View.GONE);
                    requestmessage.setVisibility(View.VISIBLE);
                    mProgress.dismiss();
                    Toast.makeText(CorporateActivity.this, "Request Sent.",Toast.LENGTH_LONG).show();
                }
            });

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            staffpic.setImageURI(resultUri);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         option = parent.getItemAtPosition(position).toString();
         requestlayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
