package com.example.student.spicamera;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private FirebaseUser user;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView mTextMessage;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_register:
                    goToRegisterPage();
                    return true;
                case R.id.navigation_notifications: //TODO: figure this out
                    goToNotifications();
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
        setContentView(R.layout.activity_home);

        //[GOOGLE SIGN IN OBJECTS NEEDED TO LOGOUT]
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        user = mAuth.getCurrentUser();
        //[END GOOGLE]




        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setUpCameras();

    }

    private void setUpCameras() {
        //Set background colors and onclick for each card
        //1. Query for user object
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefCamera = database.getReference("cameras");
        final DatabaseReference myRefUser = database.getReference("users");

        final String userId = user.getUid();

        myRefUser.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final User userRetrieved = snapshot.getValue(User.class);
                int cardId = 0;
                CardView cv;
                String cameraIDExisting = "";

                //Get the camera id from what the user selected with the radio button list
                for (int i = 1; i <= 4; i++) {
                    switch (i) {
                        case 1:
                            cameraIDExisting = userRetrieved.getCamera1();
                            cardId = R.id.cv_1;
                            break;
                        case 2:
                            cameraIDExisting = userRetrieved.getCamera2();
                            cardId = R.id.cv_2;
                            break;
                        case 3:
                            cameraIDExisting = userRetrieved.getCamera3();
                            cardId = R.id.cv_3;
                            break;
                        case 4:
                            cameraIDExisting = userRetrieved.getCamera4();
                            cardId = R.id.cv_4;
                            break;
                    }

                    final String cameraID = cameraIDExisting;

                    if (!cameraID.equals("")) {
                        cv = (CardView) findViewById(cardId);

                        cv.setBackgroundColor(getResources().getColor(R.color.cardBackground));
                        cv.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                goToCamera(cameraID);
                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError arg0) {
            }
        });
    }

    private void makeToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void goToCamera(String cameraID) {
        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra("CAMERA_ID", cameraID);

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

    private void goToRegisterPage() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void goToNotifications(){
        Intent intent = new Intent(this,NotificationsActivity.class); //TODO: make activity
        startActivity(intent);
    }

    private void goToSignInPage() {
        Intent intent = new Intent(this, GoogleSignInActivity.class);
        startActivity(intent);
    }

}
