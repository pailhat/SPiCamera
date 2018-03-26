package com.example.student.spicamera;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser user;
    private TextView mTextMessage;
    private RadioGroup radioGroup;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    goHome();
                    return true;
                case R.id.navigation_register:
                    return true;
                case R.id.navigation_signout:
                    signOut();
                    return true;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //[GOOGLE SIGN IN OBJECTS NEEDED TO LOGOUT]
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        user = mAuth.getCurrentUser();

        //[END GOOGLE]

        mTextMessage = (TextView) findViewById(R.id.cameraId);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.getMenu().getItem(1).setChecked(true);
        Log.w("BottomNav", navigation.getSelectedItemId() + "");

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        Button submitButton = (Button) findViewById(R.id.submit_register);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                //Get the text from edit text and save it into input
                registerCamera();

            }
        });

        Button deleteButton = (Button) findViewById(R.id.submit_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                //Get the text from edit text and save it into input
                deleteCamera();

            }
        });
    }

    private void registerCamera() {
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(radioButtonID);
        final int idx = radioGroup.indexOfChild(radioButton) + 1;

        final String cameraID = mTextMessage.getText().toString();

        if (cameraID.equals("")) {
            makeToast("Please enter a camera ID first.");
        } else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRefCamera = database.getReference("cameras");
            final DatabaseReference myRefUser = database.getReference("users");

            final String userId = user.getUid();

            myRefCamera.child(cameraID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        //Update the registeredTo field of the cameraID specified
                        Map<String, Object> updatesCamera = new HashMap<>();
                        updatesCamera.put("registeredTo", userId);

                        myRefCamera.child(cameraID).updateChildren(updatesCamera);

                        //Update the user object and put the camera in
                        Map<String, Object> updatesUser = new HashMap<>();
                        updatesUser.put("camera" + idx, cameraID);

                        myRefUser.child(userId).updateChildren(updatesUser);

                        makeToast("Registered camera " + idx + " with ID: " + cameraID);
                    } else {
                        makeToast("CAMERA DOES NOT EXIST");
                    }
                }
                @Override
                public void onCancelled(DatabaseError arg0) {
                }
            });



        }
    }

    private void deleteCamera() {
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(radioButtonID);
        final int idx = radioGroup.indexOfChild(radioButton) + 1;

        final String cameraID = mTextMessage.getText().toString();

        if (cameraID.equals("")) {
            makeToast("Please enter a camera ID first.");
        } else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRefCamera = database.getReference("cameras");
            final DatabaseReference myRefUser = database.getReference("users");

            final String userId = user.getUid();

            myRefCamera.child(cameraID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        //Update the registeredTo field of the cameraID specified
                        Map<String, Object> updatesCamera = new HashMap<>();
                        updatesCamera.put("registeredTo", "None");

                        myRefCamera.child(cameraID).updateChildren(updatesCamera);

                        //Update the user object and put the camera in
                        Map<String, Object> updatesUser = new HashMap<>();
                        updatesUser.put("camera" + idx, "");

                        myRefUser.child(userId).updateChildren(updatesUser);

                        makeToast("Deleted camera " + idx + " with ID: " + cameraID);
                    } else {
                        makeToast("CAMERA DOES NOT EXIST");
                    }
                }
                @Override
                public void onCancelled(DatabaseError arg0) {
                }
            });



        }
    }


    private void makeToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void goHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //OPEN Sign In Page
                        goToSignInPage();
                    }
                });
    }

    private void goToSignInPage() {
        Intent intent = new Intent(this, GoogleSignInActivity.class);
        startActivity(intent);
    }

    private void goToRegisterPage() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}
