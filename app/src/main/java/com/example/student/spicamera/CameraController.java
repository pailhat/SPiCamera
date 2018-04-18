package com.example.student.spicamera;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Paul on 4/4/2018.
 */

public class CameraController {
    private final int PORT = 12000;
    private int allowCommand;
    private String ip;
    private Context context;
    private DatabaseReference dbRef;

    public CameraController(String ip, Context context, DatabaseReference dbRef) {
        this.ip = parseIp(ip);
        this.allowCommand = 1;
        this.context = context;
        this.dbRef = dbRef;
    }

    private String parseIp(String ip) {
        //Gets just the IP from the camera feed url we have stored in firebase
        String justTheIp;

        justTheIp = ip.substring(ip.indexOf("@") + 1);
        justTheIp = justTheIp.substring(0,justTheIp.indexOf(":"));

        return justTheIp;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void takeSnap() throws Exception {
        sendCharacter("3");
    }

    public void left()  throws Exception {
        sendCharacter("2");
    }

    public void right()  throws Exception {
        sendCharacter("1");
    }

    private void makeToast(String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void sendCharacter(String c) throws Exception {
        final String input = c;

        dbRef.child("cd").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String cd = snapshot.getValue(String.class);
                if (cd.equals("done")) {

                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try  {

                                Socket clientSocket = new Socket(getIp(), PORT);

                                DataOutputStream toPi = new DataOutputStream(clientSocket.getOutputStream());

                                toPi.writeBytes(input);

                                clientSocket.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    thread.start();

                } else {
                    makeToast("Please wait before issuing another command.");
                }

            }
            @Override
            public void onCancelled(DatabaseError arg0) {
            }
        });






    }


}
