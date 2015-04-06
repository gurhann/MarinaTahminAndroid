package com.kayra.marinatahmin.model;

/**
 * Created by gurhan on 15.03.2015.
 */
public class Liman {
    private int id;
    private String limanAdi;

    public Liman(int id, String limanAdi) {
        this.id = id;
        this.limanAdi = limanAdi;
    }
    public String getLimanAdi() {
        return limanAdi;
    }

    public void setLimanAdi(String limanAdi) {
        this.limanAdi = limanAdi;
    }

    @Override
    public String toString() {
        return limanAdi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
