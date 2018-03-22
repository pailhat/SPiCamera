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