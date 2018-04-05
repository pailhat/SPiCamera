package com.example.student.spicamera;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Paul on 4/4/2018.
 */

public class CameraController {
    private final int PORT = 12000;

    private String ip;

    public CameraController(String ip) {
        this.ip = parseIp(ip);
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
        sendCharacter("s");
    }

    public void left()  throws Exception {
        sendCharacter("l");
    }

    public void right()  throws Exception {
        sendCharacter("r");
    }

    private void sendCharacter(String c) throws Exception {
        //String response;
        //BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        try {
            Log.w("CameraController", "0. Sending " + c);
            Socket clientSocket = new Socket(this.ip, PORT);
            Log.w("CameraController", "0.5. Sending " + c);
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            Log.w("CameraController", "1. Sending " + c);
            outToServer.writeBytes( c + '\n');

            clientSocket.close();

            Log.w("CameraController", "2. Sending " + c);
        }catch(Exception e){
            Log.w("CameraController", e.getMessage());
            Log.w("CameraController", e.toString());
        }

        //BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


        //response = inFromServer.readLine();
        //System.out.println(response);

    }


}
