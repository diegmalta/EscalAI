package com.ufrj.escalaiv2.utils;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

public class MapConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static Map<String, Integer> fromString(String value) {
        if (value == null || value.isEmpty()) {
            return Collections.emptyMap();
        }
        Type mapType = new TypeToken<Map<String, Integer>>() {}.getType();
        try {
            return gson.fromJson(value, mapType);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    @TypeConverter
    public static String fromMap(Map<String, Integer> map) {
        if (map == null) {
            return null;
        }
        return gson.toJson(map);
    }
}