package com.example.student.spicamera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Vibrator;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
    private CardView[] allCards;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRefCamera = database.getReference("cameras");
    private DatabaseReference myRefUser = database.getReference("users");

    private MyBroadcastReceiver receiver;
    private IntentFilter intentFilter;

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
                case R.id.navigation_notifications:
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
        navigation.getMenu().getItem(0).setChecked(true);

        setUpCameras();

        //Change bell icon when notificaiton is received
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.my.app.onMessageReceived");
        receiver = new MyBroadcastReceiver();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.getMenu().getItem(0).setChecked(true);
    }

    private void setUpCameras() {
        //Set background colors and onclick for each card
        //1. Query for user object

        final String userId = user.getUid();
        allCards = new CardView[4];

        myRefUser.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final User userRetrieved = snapshot.getValue(User.class);
                int cardId = 0;
                int cardTextId=0;
                String cameraIDExisting = "";

                //Get the camera id from what the user selected with the radio button list
                for (int i = 1; i <= 4; i++) {
                    switch (i) {
                        case 1:
                            cameraIDExisting = userRetrieved.getCamera1();
                            cardId = R.id.cv_1;
                            cardTextId = R.id.card_text_1;
                            break;
                        case 2:
                            cameraIDExisting = userRetrieved.getCamera2();
                            cardId = R.id.cv_2;
                            cardTextId = R.id.card_text_2;
                            break;
                        case 3:
                            cameraIDExisting = userRetrieved.getCamera3();
                            cardId = R.id.cv_3;
                            cardTextId = R.id.card_text_3;
                            break;
                        case 4:
                            cameraIDExisting = userRetrieved.getCamera4();
                            cardId = R.id.cv_4;
                            cardTextId = R.id.card_text_4;
                            break;
                    }

                    final String cameraID = cameraIDExisting;

                    if (!cameraID.equals("")) {

                        final CardView cv = (CardView) findViewById(cardId);

                        final TextView cvText = (TextView) findViewById(cardTextId);

                        cvText.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);

                        cvText.setText(cvText.getText()+": "+cameraIDExisting);

                        //cv.setCardBackgroundColor(getResources().getColor(R.color.cardBackground));

                        cv.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                goToCamera(cameraID);
                            }
                        });

                        //Changing the background color to light grey if the camera status is off
                        myRefCamera.child(cameraID).child("cameraStatus").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.exists()) {

                                    if (snapshot.getValue(String.class).equals("off")) {
                                        cv.setCardBackgroundColor(getResources().getColor(R.color.cardBackgroundCamOff));

                                    } else {
                                        cv.setCardBackgroundColor(getResources().getColor(R.color.cardBackground));
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError arg0) {
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
        Intent intent = new Intent(this,NotificationsActivity.class);
        startActivity(intent);
    }

    private void goToSignInPage() {
        Intent intent = new Intent(this, GoogleSignInActivity.class);
        startActivity(intent);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String state = extras.getString("NOTIFICATION");

            //Update my view
            if (state.equals("1")) {

                BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
                navigation.getMenu().getItem(2).setIcon(getResources().getDrawable(R.drawable.ic_notifications_active_black_24dp));

                makeToast("Movement detected.");

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
                }else{
                    //deprecated in API 26
                    v.vibrate(500);
                }
                //navigation.getMenu().getItem(2).getIcon().setColorFilter(getResources().getColor(R.color.colorNotification), PorterDuff.Mode.);
            }

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

}

