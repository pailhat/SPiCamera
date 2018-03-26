package com.example.student.spicamera;

/**
 * Created by Student on 3/22/2018.
 */

public class Camera {
    public String IP;
    public String cameraStatus;
    public String registeredTo;

    public Camera(String IP,String cameraStatus,String registeredTo){
        this.IP = IP;
        this.cameraStatus = cameraStatus;
        this.registeredTo = registeredTo;
    }

    public Camera() {
        this.IP = "";
        this.cameraStatus = "";
        this.registeredTo = "";
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setCameraStatus(String cameraStatus) {
        this.cameraStatus = cameraStatus;
    }

    public void setRegisteredTo(String registeredTo) {
        this.registeredTo = registeredTo;
    }

    public String getIP(){
        return this.IP;
    }
    public String getCameraStatus(){
        return this.cameraStatus;
    }
    public String getRegisteredTo(){
        return this.registeredTo;
    }

}