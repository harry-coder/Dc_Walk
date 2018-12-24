package com.dc_walk.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.net.Uri;

import com.dc_walk.CustomClasses.MediaConverter;
import com.dc_walk.CustomClasses.MediaPojo;
import com.dc_walk.CustomClasses.ParameterConverter;
import com.dc_walk.CustomClasses.UserData;
import com.dc_walk.CustomClasses.UserDataConverter;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "UserEntriesTable")
public class UserEntriesPojo {

    @PrimaryKey(autoGenerate = true)
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getRoute_no() {
        return route_no;
    }

    public void setRoute_no(String route_no) {
        this.route_no = route_no;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getObstacleTypeId() {
        return obstacleTypeId;
    }

    public void setObstacleTypeId(String obstacleTypeId) {
        this.obstacleTypeId = obstacleTypeId;
    }

    public String getStructureId() {
        return structureId;
    }

    public void setStructureId(String structureId) {
        this.structureId = structureId;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getObsRemark() {
        return obsRemark;
    }

    public void setObsRemark(String obsRemark) {
        this.obsRemark = obsRemark;
    }






    public List <MediaPojo> getUriList() {
        return uriList;
    }

    public void setUriList(List <MediaPojo> uriList) {
        this.uriList = uriList;
    }

    public String route_no, lat, lon, obstacleTypeId, structureId, side, obsRemark;



    @TypeConverters(MediaConverter.class)
    List <MediaPojo> uriList;

    public List <UserData> getUserDataList() {
        return userDataList;
    }

    public void setUserDataList(List <UserData> userDataList) {
        this.userDataList = userDataList;
    }

    @TypeConverters(UserDataConverter.class)
    List <UserData> userDataList;


}
