package com.dc_walk.ObstacleDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.net.Uri;

import com.dc_walk.Model.ObstaclePojo;
import com.dc_walk.Model.Para_DropDown;
import com.dc_walk.Model.ParameterPojo;
import com.dc_walk.Model.StructurePojo;
import com.dc_walk.Model.UserEntriesPojo;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ObstacleDao {

    @Insert
    public void insertObstacleData(List <ObstaclePojo> list);

    @Insert
    public void insertStructureData(List <StructurePojo> list);

    @Insert
    public void insertParameterData(List <ParameterPojo> list);

    @Insert
    public void insertParaDropDownData(List <Para_DropDown> list);


    @Query("select * from obstacletable")
    public List <ObstaclePojo> getObstacleItems();

    @Query("select * from structuretable where obstacleId=:obstacleId")
    public List <StructurePojo> getStructureItems(String obstacleId);

    @Query("select * from parametertable where structureId=:structureId")
    public List <ParameterPojo> getParameterItems(String structureId);

    @Query("select * from parameterdroptable where paraId=:paraId")
    public List <Para_DropDown> getParameterDropItems(String paraId);

    /*@Insert
    public void insertUserEntries(ArrayList<String> userEntries,ArrayList<String>paraList,ArrayList<String>paraRemarkList,ArrayList<String>paraDropList,ArrayList<Uri> uriList );
*/

    @Insert
    public void insertUserEntries(UserEntriesPojo userEntriesPojo);


    @Query("select * from UserEntriesTable ")
    public List <UserEntriesPojo> getAllUserEntries();

    @Query("select count(*) from UserEntriesTable")
    public int getTotalUserRecords();

    @Query("delete from UserEntriesTable")
    public void deleteUserEntriesTable();
}

