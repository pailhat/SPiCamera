package com.example.student.spicamera;

import android.app.LauncherActivity;
import android.content.Context;
import android.net.Uri;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {

    /*these are some objects which are used within the onCreate function*/
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView instructions;
    private ListView notificationsList;
    private DatabaseReference dbReference;
    private StorageReference userImagesRef;

    private int selectedItemIndex; //When a use long clicks an item from the listView, i quickly some things about the item(it's my only chance to do it)
    private String selectedItemCamera; //The camera name in the selected notification
    private String selectedItemDateTime; //Technically the name of the file in the selected notification
    private Context myContext;             //Context saved soon after onCreate, so that i can use it from inside listeners to launch new activities


    private ArrayList<String> notificationsKeyList = new ArrayList<>(); //Every notification in the database has a key. Thislist will hold the keys
    private ArrayList<String> arrayList = new ArrayList<>();            //in elements parallel to the arraylist of notifications(wish it was 2d array)
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

        myContext = this;
        //Below is some necessary code for the google sign-in/off
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        user = mAuth.getCurrentUser();

        dbReference = FirebaseDatabase.getInstance().getReference("notifications").child(user.getUid());//Get a reference to the 'notifications' part of the database
        userImagesRef = FirebaseStorage.getInstance().getReference(); //Get a storage reference to storage: child must be format user/camera/photo
                                                                        // unlike the database reference which uses multiple children

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList){// in here I link the array list to the adapter
            @NonNull
            @Override                                                                                   //and also implement querying for "seen" field
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { //to change color of text
                View view = super.getView(position, convertView, parent);

                final TextView tv = (TextView) view.findViewById(android.R.id.text1);

                dbReference.child(notificationsKeyList.get(position)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.child("mode").getValue(String.class).equals("auto")) {
                            if (dataSnapshot.child("seen").getValue(String.class).equals("No")) {
                                tv.setBackgroundColor(getResources().getColor(R.color.cardBackground));
                                tv.setTextColor(Color.BLACK);
                            } else {
                                tv.setBackgroundColor(0x00000000);
                                tv.setTextColor(Color.WHITE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return view;
            }
        };

        notificationsList = (ListView)findViewById(R.id.notificationsListView); //initialize the listView object
        notificationsList.setAdapter(adapter);
        registerForContextMenu(notificationsList);


        notificationsList.setOnItemClickListener(new OnItemClickListener() { //actions taken when there is a 'short' click on a notification
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int indexOfItem_InAllLists = i;
                String wholeItemString = adapterView.getItemAtPosition(i).toString();
                String[] selectedParts = wholeItemString.split("\n");
                selectedItemCamera = selectedParts[0].substring(selectedParts[0].indexOf(":")+2,selectedParts[0].length());//format is "camera: ***" So start
                selectedItemDateTime = selectedParts[1].substring(selectedParts[1].indexOf(":")+2,selectedParts[1].length());//substring two elements after
                                                                                                                            //colon
                userImagesRef.child(user.getUid()+"/"+selectedItemCamera+"/"+selectedItemDateTime).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Intent intent = new Intent(myContext, ViewImageActivity.class);
                        intent.putExtra("IMAGE_URL",uri.toString());
                        intent.putExtra("USER_ID",user.getUid());
                        intent.putExtra("NOTIFICATION_KEY",notificationsKeyList.get(indexOfItem_InAllLists));

                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeToast("Error getting reference to image!");
                    }
                });

            }
        });

        notificationsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //this is the response to a long click to any notification in the listView
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) { //on long click, i save some variables to the private members
                                                                                      //so that i can use them from inside the pop-up menu functions
                selectedItemIndex = i;
                String wholeItemString = adapterView.getItemAtPosition(i).toString();
                String[] selectedParts = wholeItemString.split("\n");
                selectedItemCamera = selectedParts[0].substring(selectedParts[0].indexOf(":")+2,selectedParts[0].length());//format is "camera: ***" So start
                selectedItemDateTime = selectedParts[1].substring(selectedParts[1].indexOf(":")+2,selectedParts[1].length());//substring two elements after
                return false;                                                                                               //colon to go over the space
            }
        });

        //The block below handles changes in the database, and manages the array list used for the list view
        dbReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {//actions taken when discovering things in the database
                if(dataSnapshot.child("mode").getValue(String.class).equals("auto")){
                    String cameraString = "Camera ID: " + dataSnapshot.child("camera").getValue(String.class);
                    String dateString = "When: " + dataSnapshot.child("date").getValue(String.class); //is also the name of the picture

                    arrayList.add(cameraString + "\n" + dateString );
                    notificationsKeyList.add(dataSnapshot.getKey()); //key for item in array list is kept in parallel element
                    adapter.notifyDataSetChanged();


                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { //actions taken when something in the database is removed
                if(dataSnapshot.child("mode").getValue(String.class).equals("auto")) {
                    final String cameraString = "Camera ID: " + dataSnapshot.child("camera").getValue(String.class);
                    final String dateString = "When: " + dataSnapshot.child("date").getValue(String.class); //is also the name of the picture
                    String wholeString = cameraString + "\n" + dateString;
                    if(arrayList.remove(wholeString) && notificationsKeyList.remove(dataSnapshot.getKey())){
                        //makeToast("Lost from local lists: " + wholeString);

                        final String imageDirectory = user.getUid()+"/"+dataSnapshot.child("camera").getValue(String.class)
                                +"/"+dataSnapshot.child("date").getValue(String.class);

                        userImagesRef.child(imageDirectory).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                makeToast("Successfully removed on Storage");
                                adapter.notifyDataSetChanged(); //finally notify adapter that everything is safe, so adjust the listView
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //makeToast("Removal failed: "+cameraString+"/"+dateString);
                                //makeToast(imageDirectory);
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }

                }
            }


            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); //If any changes to the database happen, the code in here is the response



        //Below is the code to generate the navbar for this activity
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.getMenu().getItem(2).setChecked(true);
        Log.w("BottomNav", navigation.getSelectedItemId() + "");

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }


    @Override// creation of pop-up menu
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) { //I had to use ctrl-O for some reason....
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.notification_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) { //actions taken when an option is selected from the pop-up menu
        switch(item.getItemId()){
            case R.id.snapshot_option:

                userImagesRef.child(user.getUid()+"/"+selectedItemCamera+"/"+selectedItemDateTime).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Intent intent = new Intent(myContext, ViewImageActivity.class);
                        intent.putExtra("IMAGE_URL",uri.toString());
                        intent.putExtra("USER_ID",user.getUid());
                        intent.putExtra("FROM_ACT","Notifications");
                        intent.putExtra("NOTIFICATION_KEY",notificationsKeyList.get(selectedItemIndex));
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeToast("Error getting reference to image!");
                    }
                });

                return true;
            case R.id.delete_option:
                dbReference.child(notificationsKeyList.get(selectedItemIndex)).removeValue();//This removes the notification from the database
                                                                                             //in theory the "onChildRemoved" for the database listener
                return true;                                                                 //should delete the item from the adapter's arrayList
            default:                                                                         //and from the array list of keys for each notification
                return super.onContextItemSelected(item);
        }
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
        Intent intent = new Intent(this,NotificationsActivity.class);
        startActivity(intent);
    }
    private void goToRegisterPage() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.getMenu().getItem(2).setChecked(true);
    }

}
