package com.example.student.spicamera;

public class Notification {
    public String camera;
    public String date;
    public String receiver;
    public String seen;

    public Notification(String camera, String date, String receiver, String seen){
        this.camera = camera;
        this.date = date;
        this.receiver = receiver;
        this.seen = seen;
    }

    public Notification(){
        this.camera="";
        this.date="";
        this.receiver="";
        this.seen="";
    }

    public String getCamera(){return this.camera;}
    public String getDate(){return this.date;}
    public String getReceiver(){return this.receiver;}
    public String getSeen(){return this.seen;}

    public void setCamera(String camera){this.camera = camera;}
    public void setDate(String date){this.date = date;}
    public void setReceiver(String receiver){this.receiver = receiver;};
    public void setSeen(String seen){this.seen = seen;}

}
