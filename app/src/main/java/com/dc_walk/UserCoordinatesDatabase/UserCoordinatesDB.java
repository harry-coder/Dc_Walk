package com.dc_walk.UserCoordinatesDatabase;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.dc_walk.Model.PlaceInfoPojo;
import com.dc_walk.Model.UserCoordinatesPojo;
import com.dc_walk.ServerLatLonDatabase.LocationDao;

@Database(entities = {UserCoordinatesPojo.class}, version = 1,exportSchema = false)
public abstract class UserCoordinatesDB extends RoomDatabase {

    public abstract UserCoordinatesDao myDao();


    private static final String DB_NAME = "UserCoordinates.db";
    private static volatile UserCoordinatesDB instance;

    public static synchronized UserCoordinatesDB getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static UserCoordinatesDB create(final Context context) {
        return Room.databaseBuilder(
                context,
                UserCoordinatesDB.class,
                DB_NAME).allowMainThreadQueries ().build();
    }
    static final Migration FROM_5_TO_6 = new Migration(6, 7) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
         //
               database.execSQL("DROP TABLE UserCoordinates");
        }
    };
}
