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

    public User(String userID,String deviceID,String email) {
        this.deviceID = deviceID;
        this.userID = userID;
        this.email = email;
        this.camera1 = "";
        this.camera2 = "";
        this.camera3 = "";
        this.camera4 = "";
    }

    public User() {
        this.deviceID = "";
        this.userID = "";
        this.email = "";
        this.camera1 = "";
        this.camera2 = "";
        this.camera3 = "";
        this.camera4 = "";
    }

    public void setUserID(String userID){
        this.userID=userID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCamera1(String camera1) {
        this.camera1 = camera1;
    }

    public void setCamera2(String camera2) {
        this.camera2 = camera2;
    }

    public void setCamera3(String camera3) {
        this.camera3 = camera3;
    }

    public void setCamera4(String camera4) {
        this.camera4 = camera4;
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
