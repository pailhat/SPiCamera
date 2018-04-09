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
        sendCharacter("3");
    }

    public void left()  throws Exception {
        sendCharacter("2");
    }

    public void right()  throws Exception {
        sendCharacter("1");
    }

    private void sendCharacter(String c) throws Exception {
        final String input = c;
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {


                    BufferedReader toSend = new BufferedReader(new InputStreamReader(System.in));

                    Socket clientSocket = new Socket(getIp(), PORT);

                    DataOutputStream toPi = new DataOutputStream(clientSocket.getOutputStream());

                    //input = toSend.readLine();

                    toPi.writeBytes(input);

                    clientSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }


}
