package com.dc_walk.KmlDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {KmlItems.class}, version = 1, exportSchema = false)
public abstract class KmlDB extends RoomDatabase {


    public abstract KmlDao kmlDao();


    private static final String DB_NAME = "KmlCoordinates.db";
    private static volatile KmlDB instance;

    public static synchronized KmlDB getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static KmlDB create(final Context context) {
        return Room.databaseBuilder(
                context,
                KmlDB.class,
                DB_NAME).allowMainThreadQueries ().build();
    }
   /* static final Migration FROM_5_TO_6 = new Migration(5, 6) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE location");
        }
    };*/
}
