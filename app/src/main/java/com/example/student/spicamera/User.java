package com.example.student.spicamera;

/**
 * Created by Student on 3/19/2018.
 */

public class User {
    public String deviceID;
    public String userID;
    public String email;
    public String registeredCameras;

    public User(String userID,String deviceID,String email,String registeredCameras){
        this.deviceID = deviceID;
        this.userID = userID;
        this.email = email;
        this.registeredCameras = registeredCameras;
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
    public String getRegisteredCameras(){
        return this.registeredCameras;
    }

}
