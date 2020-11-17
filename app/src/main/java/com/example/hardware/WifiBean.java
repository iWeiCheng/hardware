package com.example.hardware;

/**
 * @author : danjiajun
 * @created : 2020/11/17 15:56
 * @description :
 */
public class WifiBean {

    public WifiBean(String ssid, String bssid, String cap, int level, int freq) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.cap = cap;
        this.level = level;
        this.freq = freq;
    }

    /**
     * ssid : HUAWEI-Pro2_5G
     * bssid : e4:19:c1:e1:92:94
     * cap : [WPA2-PSK-CCMP][WPS][ESS]
     * level : -46
     * freq : 5765
     */

    private String ssid;
    private String bssid;
    private String cap;
    private int level;
    private int freq;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }
}
