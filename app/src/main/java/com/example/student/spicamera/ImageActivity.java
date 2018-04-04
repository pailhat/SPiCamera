package com.example.student.spicamera;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageActivity extends AppCompatActivity implements
        OnItemClickListener {

    public static final String[] titles = new String[] { "Strawberry",
            "Banana", "Orange", "Mixed" };

    public static final String[] descriptions = new String[] {
            "It is an aggregate accessory fruit",
            "It is the largest herbaceous flowering plant", "Citrus Fruit",
            "Mixed Fruits" };

    public static final String[]images = { "https://firebasestorage.googleapis.com/v0/b/spi-camera.appspot.com/o/cAzM9WQkyt%2F2018-03-22%2019%3A39%3A54.473197?alt=media&token=0fa979fd-bb0f-40fd-99e9-00cbb7a7f2d2",
            "https://firebasestorage.googleapis.com/v0/b/spi-camera.appspot.com/o/cAzM9WQkyt%2F2018-03-22%2019%3A44%3A35.043504?alt=media&token=02505b9f-0104-4d36-968c-e3bb5cce2da9",
            "https://firebasestorage.googleapis.com/v0/b/spi-camera.appspot.com/o/cAzM9WQkyt%2F2018-03-22%2019%3A39%3A54.473197?alt=media&token=0fa979fd-bb0f-40fd-99e9-00cbb7a7f2d2",
            "https://firebasestorage.googleapis.com/v0/b/spi-camera.appspot.com/o/cAzM9WQkyt%2F2018-03-22%2019%3A44%3A35.043504?alt=media&token=02505b9f-0104-4d36-968c-e3bb5cce2da9"};

    ListView listView;
    List<RowItem> rowItems;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        //Creates the list items with the images from the arrays above
        //here we jsut need to query firebase to get this information instead
        //TODO Query firebase for all images associated with the camera
        StorageReference myRef = FirebaseStorage.getInstance().getReference().child("dummyCameraID");

        rowItems = new ArrayList<RowItem>();
        final Context context = this;

        //TODO use databse instead of storage to get the URLS
        myRef.child("2018-03-21 20:22:36.361915").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                makeToast(uri.toString());
                RowItem item = new RowItem(uri.toString(), "HAHA", "ha");
                rowItems.add(item);
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                listView = (ListView) findViewById(R.id.imagesListView);

                CustomListViewAdapter adapter = new CustomListViewAdapter(context,
                        R.layout.list_item, rowItems);
                listView.setAdapter(adapter);


            }
        });


        //listView.setOnItemClickListener(this);

    }

    private void makeToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        /*TODO -In this onclick adapter, we edit it to open an activity showing a big picture of the image*/
        Toast toast = Toast.makeText(getApplicationContext(),
                "Item " + (position + 1) + ": " + rowItems.get(position),
                Toast.LENGTH_SHORT);
        toast.show();
    }
}
