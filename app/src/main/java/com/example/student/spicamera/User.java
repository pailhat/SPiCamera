package com.example.student.spicamera;

/**
 * Created by Student on 3/19/2018.
 */

public class User {
    public String deviceID;
    public String userID;
    public String email;
    public String camera1;
    public String camera2;
    public String camera3;
    public String camera4;


    public User(String userID,String deviceID,String email){
        this.deviceID = deviceID;
        this.userID = userID;
        this.email = email;
        this.camera1 = "";
        this.camera2 = "";
        this.camera3 = "";
        this.camera4 = "";
    }

    public void setUserID(String userID){
        this.userID=userID;
    }

    public String getDeviceID(){
        return this.deviceID;
    }
    public String getEmail(){
        return this.email;
    }
    public String getUserID(){
        return this.userID;
    }
    public String getCamera1(){
        return this.camera1;
    }
    public String getCamera2(){
        return this.camera2;
    }
    public String getCamera3(){
        return this.camera3;
    }
    public String getCamera4(){
        return this.camera4;
    }

}
