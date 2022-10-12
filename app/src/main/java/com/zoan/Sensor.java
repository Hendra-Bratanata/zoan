package com.zoan;

import android.os.Parcel;
import android.os.Parcelable;

public class Sensor implements Parcelable {
    private String ID;
    private String Nitrogen;
    private String Phosphor;
    private String Kalium;
    private String Soil_PH;
    private String Soil_Humidity;
    private String Soil_Temperature;
    private String Light_Intensity;
    private String Timestamp;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNitrogen() {
        return Nitrogen;
    }

    public void setNitrogen(String nitrogen) {
        Nitrogen = nitrogen;
    }

    public String getPhosphor() {
        return Phosphor;
    }

    public void setPhosphor(String phosphor) {
        Phosphor = phosphor;
    }

    public String getKalium() {
        return Kalium;
    }

    public void setKalium(String kalium) {
        Kalium = kalium;
    }

    public String getSoil_PH() {
        return Soil_PH;
    }

    public void setSoil_PH(String soil_PH) {
        Soil_PH = soil_PH;
    }

    public String getSoil_Humidity() {
        return Soil_Humidity;
    }

    public void setSoil_Humidity(String soil_Humidity) {
        Soil_Humidity = soil_Humidity;
    }

    public String getSoil_Temperature() {
        return Soil_Temperature;
    }

    public void setSoil_Temperature(String soil_Temperature) {
        Soil_Temperature = soil_Temperature;
    }

    public String getLight_Intensity() {
        return Light_Intensity;
    }

    public void setLight_Intensity(String light_Intensity) {
        Light_Intensity = light_Intensity;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
