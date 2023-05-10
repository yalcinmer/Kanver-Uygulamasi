package com.example.kanver;

import com.google.firebase.auth.FirebaseAuth;

public class Kullanici {

    String Id, ad, email, kanGrubu, adres, sehir, durum, kanDurumu;

    public Kullanici(){}

    public Kullanici(String Id, String ad, String email, String kanGrubu, String adres, String sehir, String durum, String kanDurumu){
        this.Id = Id;
        this.ad = ad;
        this.email = email;
        this.kanGrubu = kanGrubu;
        this.sehir = sehir;
        this.adres = adres;
        this.durum = durum;
        this.kanDurumu = kanDurumu;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getId() { return Id; }

    public void setId(String id) { Id = id; }

    public String getDurum() { return durum; }

    public void setDurum(String durum) { this.durum = durum; }

    public String getSehir() { return sehir; }

    public void setSehir(String sehir) { this.sehir = sehir; }

    public String getAd() { return ad;}

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getKanGrubu() {
        return kanGrubu;
    }

    public void setKanGrubu(String kanGrubu) {
        this.kanGrubu = kanGrubu;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public String getKanDurumu() { return kanDurumu; }

    public void setKanDurumu(String kanDurumu) { this.kanDurumu = kanDurumu; }
}
