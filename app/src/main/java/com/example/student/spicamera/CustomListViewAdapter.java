package com.example.student.spicamera;

/**
 * Created by Student on 4/4/2018.
 */

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CustomListViewAdapter extends ArrayAdapter<RowItem> {

    DatabaseReference dbReference;
    Context context;
    ArrayList<String> notificationsKeyList;

    public CustomListViewAdapter(Context context, int resourceId,
                                 List<RowItem> items, DatabaseReference dbReference,
                                 ArrayList<String> notificationsKeyList) {
        super(context, resourceId, items);
        this.context = context;
        this.dbReference = dbReference;
        this.notificationsKeyList = notificationsKeyList;

    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtDesc;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtDesc.setText(rowItem.getDesc());
        holder.txtTitle.setText(rowItem.getTitle());

        String path = rowItem.getImageUrl();
        Picasso.with(context).load(path).into(holder.imageView);

        final TextView desc = holder.txtDesc;
        final TextView title = holder.txtTitle;
        final View convertViewFinal = convertView;

        dbReference.child(notificationsKeyList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("seen").getValue(String.class).equals("No")){
                    convertViewFinal.setBackgroundColor(context.getResources().getColor(R.color.cardBackground));
                    desc.setTextColor(Color.BLACK);
                    title.setTextColor(Color.BLACK);
                }
                else{
                    convertViewFinal.setBackgroundColor(0x00000000);
                    desc.setTextColor(Color.WHITE);
                    title.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return convertView;
    }
}
