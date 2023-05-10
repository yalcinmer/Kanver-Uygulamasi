package com.example.kanver;

public class Chat {
    String uId;
    String mesaj;
    String time;
    String mesajId;

    public Chat(){}

    public Chat(String uId, String mesaj, String time){
        this.uId = uId;
        this.mesaj=mesaj;
        this.time = time;
    }

    public Chat(String uId, String mesaj){
        this.uId = uId;
        this.mesaj=mesaj;
    }

    public String getMesajId() { return mesajId; }

    public void setMesajId(String mesajId) { this.mesajId = mesajId; }

    public String getuId() { return uId; }

    public void setuId(String uId) { this.uId = uId; }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public String getTime() { return time;}

    public void setTime(String time) { this.time = time; }
}
