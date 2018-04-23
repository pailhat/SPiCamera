package com.example.student.spicamera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;

    private WebView wv1;
    private String url = "";
    private String cameraID;
    private DatabaseReference notificationsRef;
    private DatabaseReference myRefCamera;
    private EditText urlText;
    private CameraController cameraController;
    private MyBroadcastReceiver receiver;
    private IntentFilter intentFilter;

    private ValueEventListener vanguardListenerHandle; //handle for the listener which prompts the other one to work
    private ChildEventListener snapshotListenerHandle; //listener that begins checking for latest added children, after go ahead from vanguard
    private boolean beginListening;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    goHome();
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
        setContentView(R.layout.activity_main);

        //[GOOGLE SIGN IN OBJECTS NEEDED TO LOGOUT]
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //[END GOOGLE]


        //Get the camera ID and set the webview
        cameraID = (String) getIntent().getExtras().get("CAMERA_ID");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRefCamera = database.getReference("cameras").child(cameraID);
        notificationsRef = database.getReference("notifications").child(mAuth.getCurrentUser().getUid());
        final Context context = this;
        //Query for camera
        myRefCamera.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Camera camRetrieved = snapshot.getValue(Camera.class);

                url = camRetrieved.getIp();

                //makeToast(url);
                loadCameraFeed(url);
                cameraController = new CameraController(url,context,myRefCamera);
                setUpCameraButtons();
                //makeToast(cameraController.getIp());

            }
            @Override
            public void onCancelled(DatabaseError arg0) {
            }
        });

        Button floatingActionButton = (Button) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToImages(cameraID);
            }
        });

        FloatingActionButton powerButton = (FloatingActionButton) findViewById(R.id.powerButton);
        powerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cameraOnOff();
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        //Change bell icon when notificaiton is received
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.my.app.onMessageReceived");
        receiver = new MyBroadcastReceiver();
    }

    private void loadCameraFeed(String url) {
        wv1=(WebView)findViewById(R.id.webView);
        wv1.setWebViewClient(new MyBrowser());
        wv1.setInitialScale(1);
        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setLoadWithOverviewMode(true);
        wv1.getSettings().setUseWideViewPort(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.getSettings().setBuiltInZoomControls(true);
        wv1.getSettings().setDisplayZoomControls(false);
        wv1.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        wv1.loadUrl(url);
    }
    private void setUpCameraButtons(){
        Button leftButton = (Button) findViewById(R.id.buttonLeft);
        leftButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    cameraController.left();
                } catch (Exception e) {
                    //Nothing
                    //Log.w("CameraController", e.getMessage());
                }
            }
        });

        Button rightButton = (Button) findViewById(R.id.buttonRight);
        rightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    cameraController.right();
                } catch (Exception e) {
                    //Nothing
                }
            }
        });

        Button snapButton = (Button) findViewById(R.id.buttonSnapShot);
        snapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    cameraController.takeSnap();
                } catch (Exception e) {
                    //Nothing
                }
            }
        });
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

    private void goToImages(String cameraID) {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra("CAMERA_ID",cameraID);
        startActivity(intent);

    }

    private void goToNotifications(){
        Intent intent = new Intent(this,NotificationsActivity.class);
        startActivity(intent);

    }
    private void goToRegisterPage() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            view.loadData("<html><center><BR><BR><BR><div style='font-size: 70px'>Camera feed not available</div></center></html>", "", "");

            //On error, attempt to reload the camera feed in 10 seconds
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    loadCameraFeed(url);
                    //makeToast("trying again");
                }
            }, 7000);
        }
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

    private void disableButtons() {
        Button leftButton = (Button) findViewById(R.id.buttonLeft);
        leftButton.setEnabled(false);

        Button rightButton = (Button) findViewById(R.id.buttonRight);
        rightButton.setEnabled(false);

        Button snapButton = (Button) findViewById(R.id.buttonSnapShot);
        snapButton.setEnabled(false);
    }

    private void enableButtons() {
        Button leftButton = (Button) findViewById(R.id.buttonLeft);
        Button rightButton = (Button) findViewById(R.id.buttonRight);
        Button snapButton = (Button) findViewById(R.id.buttonSnapShot);
        FloatingActionButton powerButton = (FloatingActionButton) findViewById(R.id.powerButton);

        leftButton.setEnabled(true);
        rightButton.setEnabled(true);
        snapButton.setEnabled(true);
        powerButton.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        notificationsRef.removeEventListener(snapshotListenerHandle); //TODO debug this; I never restablish it within onResume, so how is it working?
        //beginListening = false; //when we come back to this activity; ignore the last notification once more
        //makeToast("made false"); //TODO remove this
        disableButtons();
    }

    private void cameraOnOff() {
        DatabaseReference camStatusRef = myRefCamera.child("cameraStatus");
        final FloatingActionButton powerButton = (FloatingActionButton) findViewById(R.id.powerButton);
        //Query for camera
        camStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                String statusToUpload = "on";
                if (status.equals("on")) {
                    statusToUpload = "off";
                }
                //makeToast("l");
                Map<String, Object> updatesCamera = new HashMap<>();
                updatesCamera.put("cameraStatus", statusToUpload);
                myRefCamera.updateChildren(updatesCamera);

                //Update UI
                if (statusToUpload.equals("on")) {
                    powerButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cameraOff)));
                    powerButton.setImageResource(R.drawable.ic_videocam_off_black_24dp);
                    powerButton.setEnabled(false);

                    makeToast("Turing on the camera...");

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            enableButtons();
                        }
                    }, 3500);
                } else {
                    powerButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cameraOn)));
                    powerButton.setImageResource(R.drawable.ic_videocam_black_24dp);
                    disableButtons();
                    makeToast("Camera off.");
                }

            }
            @Override
            public void onCancelled(DatabaseError arg0) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);

        DatabaseReference camStatusRef = myRefCamera.child("cameraStatus");
        final FloatingActionButton powerButton = (FloatingActionButton) findViewById(R.id.powerButton);
        //Query for camera
        camStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
;
                //Update UI
                if (status.equals("on")) {
                    powerButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cameraOff)));
                    powerButton.setImageResource(R.drawable.ic_videocam_off_black_24dp);

                    //Enable the buttons after 2 seconds
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            enableButtons();
                        }
                    }, 2000);

                } else {
                    powerButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cameraOn)));
                    powerButton.setImageResource(R.drawable.ic_videocam_black_24dp);
                    disableButtons();
                }

            }
            @Override
            public void onCancelled(DatabaseError arg0) {
            }
        });


        //these three lines below are used to check if manual snapshots worked. First listener signals the second one to begin looking for new manual snapshots,
        //by flipping the flag below. LOGIC:First listener flips the flag after the second listener has looked over all of the old notifications.
        beginListening = false; //this is for the purpose of ignoring the initial "last" notification read //TODO debug these lines below

        notificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //makeToast(String.valueOf(dataSnapshot.hasChild("-L9vIQnnXnRuiUWw1krA")));
                beginListening = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        snapshotListenerHandle = notificationsRef.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(beginListening && dataSnapshot.child("camera").getValue(String.class).equals(cameraID)&& dataSnapshot.child("mode").getValue(String.class).equals("manual")){
                    makeToast("Snapshot taken!");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        notificationsRef.removeEventListener(snapshotListenerHandle);
    }
}
