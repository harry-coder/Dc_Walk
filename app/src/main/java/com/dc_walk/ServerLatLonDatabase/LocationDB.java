package com.dc_walk.ServerLatLonDatabase;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.dc_walk.Model.PlaceInfoPojo;


@Database(entities = {PlaceInfoPojo.class}, version = 1, exportSchema = false)
public abstract class LocationDB extends RoomDatabase {

    public abstract LocationDao myDao();


    private static final String DB_NAME = "RouteLocation.db";
    private static volatile LocationDB instance;

    public static synchronized LocationDB getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static LocationDB create(final Context context) {
        return Room.databaseBuilder(
                context,
                LocationDB.class,
                DB_NAME).build();
    }
    static final Migration FROM_5_TO_6 = new Migration(5, 6) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE location");
        }
    };
}
