package com.dc_walk.KmlDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.dc_walk.Model.ObstaclePojo;

import java.util.List;

@Dao
public interface KmlDao {

    @Insert
    public void insertKmlItems(List <KmlItems> obstacleList);

    @Query("select * from kmlitems order by routeNo ASC")
    public List <KmlItems> getKmlCoordinates();

    @Query("UPDATE KmlItems SET isMapped=:isMapped WHERE routeNo = :route")
    public void updateKmlItems(boolean isMapped, int route);


}
