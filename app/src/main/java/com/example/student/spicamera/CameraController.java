package com.example.student.spicamera;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Paul on 4/4/2018.
 */

public class CameraController {
    private String ip;

    public CameraController(String ip) {
        this.ip = ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void takeSnap() throws Exception {
        //TODO send s to camera so it takes a picture

        //THIS ISNT DONE YET I JUST COPY PASTED FROM STACKOVERFLOW
        String sentence;
        String modifiedSentence;
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        Socket clientSocket = new Socket("localhost", 6789);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        sentence = inFromUser.readLine();
        outToServer.writeBytes(sentence + '\n');
        modifiedSentence = inFromServer.readLine();
        System.out.println(modifiedSentence);
        clientSocket.close();
    }

    public void left()  throws Exception {
        //TODO send s to camera so it takes a picture
    }

    public void right()  throws Exception {
        //TODO send s to camera so it takes a picture
    }

    public static String parseIp(String ip) {

        return ip;
    }
}
