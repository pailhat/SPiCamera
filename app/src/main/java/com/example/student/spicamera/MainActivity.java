package com.example.student.spicamera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;

    private WebView wv1;
    private String url = "";
    private DatabaseReference myRefCamera;
    private EditText urlText;
    private CameraController cameraController;
    private MyBroadcastReceiver receiver;
    private IntentFilter intentFilter;

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
        final String cameraID = (String) getIntent().getExtras().get("CAMERA_ID");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefCamera = database.getReference("cameras").child(cameraID);
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
                makeToast(cameraController.getIp());

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
            view.loadData("<html><center><BR><BR><BR><div style='font-size: 75px'>Camera feed not available!</div></center></html>", "", "");
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

        leftButton.setEnabled(true);
        rightButton.setEnabled(true);
        snapButton.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);

        disableButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                enableButtons();
            }
        }, 2000);



    }
}
