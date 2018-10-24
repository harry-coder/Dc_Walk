package com.dc_walk.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "ServerRouteTable")
public class PlaceInfoPojo {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;

    String lat;
    String lng;

    public boolean isMapped() {
        return isMapped;
    }

    public void setMapped(boolean mapped) {
        isMapped = mapped;
    }


    public boolean isMapped;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public int getRouteNo() {


        return routeNo;
    }

    public void setRouteNo(@NonNull int routeNo) {
        this.routeNo = routeNo;
    }


    int routeNo;
}
