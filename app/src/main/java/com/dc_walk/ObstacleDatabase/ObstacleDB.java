package com.dc_walk.ObstacleDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.dc_walk.Model.ObstaclePojo;
import com.dc_walk.Model.Para_DropDown;
import com.dc_walk.Model.ParameterPojo;
import com.dc_walk.Model.StructurePojo;
import com.dc_walk.Model.UserEntriesPojo;

@Database(entities = {ObstaclePojo.class, ParameterPojo.class, StructurePojo.class, Para_DropDown.class, UserEntriesPojo.class}, version = 1, exportSchema = false)
public abstract class ObstacleDB extends RoomDatabase {

    public abstract ObstacleDao obstacleDao();


    private static final String DB_NAME = "ObstacleItems.db";
    private static volatile ObstacleDB instance;

    public static synchronized ObstacleDB getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static ObstacleDB create(final Context context) {
        return Room.databaseBuilder(
                context,
                ObstacleDB.class,
                DB_NAME).allowMainThreadQueries().build();
    }
}
