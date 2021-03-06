package com.example.student.spicamera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageActivity extends AppCompatActivity implements
        OnItemClickListener {

    ListView listView;
    List<RowItem> rowItems;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference dbReference;
    private CustomListViewAdapter adapter;
    private ArrayList<String> notificationsKeyList = new ArrayList<>();

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

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        //Below is some necessary code for the google sign-in/off
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        user = mAuth.getCurrentUser();

        dbReference = FirebaseDatabase.getInstance().getReference("notifications").child(user.getUid());//Get a reference to the 'notifications' part of the database

        //Creates the list items with the images from the arrays above
        //here we jsut need to query firebase to get this information instead


        final StorageReference myRef = FirebaseStorage.getInstance().getReference().child(user.getUid());

        rowItems = new ArrayList<RowItem>();
        final Context context = this;

        listView = (ListView) findViewById(R.id.imagesListView);
        adapter = new CustomListViewAdapter(context,
                R.layout.list_item, rowItems, dbReference, notificationsKeyList);
        listView.setAdapter(adapter);

        final String cameraID = (String) getIntent().getExtras().get("CAMERA_ID");

        //The block below handles changes in the database, and manages the array list used for the list view
        dbReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                if (cameraID.equals(dataSnapshot.child("camera").getValue(String.class))) {
                    final String cameraString = dataSnapshot.child("camera").getValue(String.class);
                    final String dateString = dataSnapshot.child("date").getValue(String.class); //is also the name of the picture

                    Log.w("ImageActvitiy", user.getUid() + "\n" + cameraString + "\n" + dateString);

                    myRef.child(cameraString).child(dateString).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            //makeToast(uri.toString());
                            RowItem item = new RowItem(uri.toString(), dateString, cameraString, dateString);
                            rowItems.add(0,item);

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            adapter.notifyDataSetChanged();
                            notificationsKeyList.add(0,dataSnapshot.getKey());

                        }
                    });

                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (user.getUid().equals(dataSnapshot.child("receiver").getValue(String.class))) {

                    String dateString = dataSnapshot.child("date").getValue(String.class); //is also the name of the picture

                    rowItems.remove(new RowItem(dateString));

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        listView.setOnItemClickListener(this);

        //Below is the code to generate the navbar for this activity
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.getMenu().getItem(0).setChecked(true);
        Log.w("BottomNav", navigation.getSelectedItemId() + "");

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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

    private void makeToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {


        Toast toast = Toast.makeText(getApplicationContext(),
                "Item " + (position + 1) + ": " + rowItems.get(position),
                Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent(this, ViewImageActivity.class);
        intent.putExtra("IMAGE_URL", rowItems.get(position).getImageUrl());
        intent.putExtra("USER_ID",user.getUid());
        intent.putExtra("NOTIFICATION_KEY",notificationsKeyList.get(position));

        startActivity(intent);


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
    private void goToNotifications(){
        Intent intent = new Intent(this,NotificationsActivity.class);
        startActivity(intent);
    }
    private void goToRegisterPage() {
        Intent intent = new Intent(this, RegisterActivity.class);
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
