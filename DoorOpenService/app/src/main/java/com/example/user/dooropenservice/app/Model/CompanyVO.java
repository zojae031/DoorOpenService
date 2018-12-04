package com.example.user.dooropenservice.app.Model;

import java.io.Serializable;

public class CompanyVO implements Serializable {
    private String company;
    private double latitude;
    private double longitude;
    private double scope;
    public CompanyVO(String company, double lat ,double lon,double scope){
        this.company = company;
        this.latitude = lat;
        this.longitude = lon;
        this.scope = scope;
    }
    public void setCompany(String company) {
        this.company = company;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setScope(double scope) {
        this.scope = scope;
    }

    public String getCompany() {
        return company;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getScope() {
        return scope;
    }
}
