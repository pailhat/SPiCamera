package com.example.student.spicamera;

import android.app.LauncherActivity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
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
import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {

    /*these are some objects which are used within the onCreate function*/
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView mTextMessage;
    private ListView notificationsList;
    private DatabaseReference dbReference;

    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter<String> adapter; //the adapter between the database and listView

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
        setContentView(R.layout.activity_notifications);

        //Below is some necessary code for the google sign-in/off
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        user = mAuth.getCurrentUser();

        dbReference = FirebaseDatabase.getInstance().getReference("notifications");//Get a reference to the 'notifications' part of the database
        //dbReference.orderByChild("receiver").equalTo(user.getUid());

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        notificationsList = (ListView)findViewById(R.id.notificationsListView); //initialize the listView object
        notificationsList.setAdapter(adapter);

        //The block below handles changes in the database, and manages the array list used for the list view
        dbReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(user.getUid().equals(dataSnapshot.child("receiver").getValue(String.class))){
                    String cameraString = "Camera ID: " + dataSnapshot.child("camera").getValue(String.class);
                    String dateString = "When: " + dataSnapshot.child("date").getValue(String.class); //is also the name of the picture

                    arrayList.add(cameraString + "\n" + dateString );
                    adapter.notifyDataSetChanged();

                    //makeToast(notificationsList.getChildCount()+"");
                    //adapter.getView(arrayList.size() -1,null,notificationsList).setBackgroundColor(getResources().getColor(R.color.notificationSeen));

                    //notificationsList.getChildAt(arrayList.size()-1).setBackgroundColor(getResources().getColor(R.color.notificationSeen));
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(user.getUid().equals(dataSnapshot.child("receiver").getValue(String.class))) {
                    String cameraString = dataSnapshot.child("camera").getValue(String.class);
                    String dateString = dataSnapshot.child("date").getValue(String.class); //is also the name of the picture
                    String wholeString = cameraString + "\n" + dateString;
                    arrayList.remove(wholeString);
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

        //Below is the code to generate the navbar for this activity
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.getMenu().getItem(2).setChecked(true);
        Log.w("BottomNav", navigation.getSelectedItemId() + "");

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }



    //***Below are copy pasted functions from other activities:
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
    private void goToNotifications(){
        Intent intent = new Intent(this,NotificationsActivity.class); //TODO: make activity
        startActivity(intent);
    }
    private void goToRegisterPage() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
