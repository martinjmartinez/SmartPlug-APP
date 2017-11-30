package com.example.martinjmartinez.proyectofinal.Models;

public class WifiConnection {

    private String SSID;

    private Integer signal;

    private String type;

    public WifiConnection(String SSID, Integer signal, String type) {
        this.SSID = SSID;
        this.signal = signal;

        if(type.contains("WPA")){
            this.type = "WPA";
        } else if(type.contains("WEP")) {
            this.type = "WEP";
        } else{
            this.type = "FREE";
        }
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public Integer getSignal() {
        return signal;
    }

    public void setSignal(Integer signal) {
        this.signal = signal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
