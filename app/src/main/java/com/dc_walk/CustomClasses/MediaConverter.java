package com.dc_walk.CustomClasses;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class MediaConverter {


   static  Gson gson = new Gson();

    @TypeConverter
    public static List<MediaPojo> MediaListObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<MediaPojo>> () {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String parameterListToString(List<MediaPojo> someObjects) {
        return gson.toJson(someObjects);
    }
}
