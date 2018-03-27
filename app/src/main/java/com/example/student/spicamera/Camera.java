package com.example.student.spicamera;

/**
 * Created by Student on 3/22/2018.
 */

public class Camera {
    public String ip;
    public String cameraStatus;
    public String registeredTo;

    public Camera(String ip,String cameraStatus,String registeredTo){
        this.ip = ip;
        this.cameraStatus = cameraStatus;
        this.registeredTo = registeredTo;
    }

    public Camera() {
        this.ip = "poo";
        this.cameraStatus = "poo";
        this.registeredTo = "poo";
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setCameraStatus(String cameraStatus) {
        this.cameraStatus = cameraStatus;
    }

    public void setRegisteredTo(String registeredTo) {
        this.registeredTo = registeredTo;
    }

    public String getIp(){
        return this.ip;
    }
    public String getCameraStatus(){
        return this.cameraStatus;
    }
    public String getRegisteredTo(){
        return this.registeredTo;
    }

}