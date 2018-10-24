package com.dc_walk.ServerLatLonDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.dc_walk.Model.PlaceInfoPojo;

import java.util.List;

@Dao
public interface LocationDao {

    @Insert
    public void insertData(List<PlaceInfoPojo> location);

    @Query("select * from ServerRouteTable ")
    public List<PlaceInfoPojo> getUserList();

    @Query("UPDATE ServerRouteTable SET isMapped=:isMapped WHERE routeNo = :route")
    public void getUpdatedList(boolean isMapped, int route);


}
