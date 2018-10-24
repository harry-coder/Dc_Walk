package com.dc_walk.UserCoordinatesDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.dc_walk.Model.PlaceInfoPojo;
import com.dc_walk.Model.UserCoordinatesPojo;

import java.util.List;

@Dao
public interface UserCoordinatesDao {

    @Insert
    public void insertData(UserCoordinatesPojo coordinatesPojo);

    @Query("select * from UserCoordinatesTable ")
    public List <UserCoordinatesPojo> getUserCoordinatesList();

    @Query("select route from UserCoordinatesTable    where route > :previousRoute and route <:currentRoute ")
    public List <Double> getrouteNoList(int previousRoute, int currentRoute);

    @Query("select route from UserCoordinatesTable    where route > :currentRoute ")
    public List <Double> getFirstRouteNoList(int currentRoute);
}
