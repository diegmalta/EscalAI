package com.ufrj.escalaiv2.utils;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Converte Map<String,Integer> <-> JSON garantindo que números retornem como Integer.
 * <p>
 * Gson tende a desserializar literais numéricos como Double.  Ao ler como
 * Map<String,Number> e depois converter para Integer resolvemos o problema.
 */
public class MapConverter {

    private static final Gson gson = new Gson();
    private static final Type RAW_TYPE =
            new TypeToken<Map<String, Number>>() {}.getType();

    /** JSON -> Map<String,Integer> */
    @TypeConverter
    public static Map<String, Integer> fromString(String value) {
        if (value == null || value.isEmpty()) return Collections.emptyMap();
        try {
            Map<String, Number> raw = gson.fromJson(value, RAW_TYPE);
            if (raw == null || raw.isEmpty()) return Collections.emptyMap();

            Map<String, Integer> clean = new HashMap<>(raw.size());
            for (Map.Entry<String, Number> e : raw.entrySet()) {
                clean.put(e.getKey(),
                        e.getValue() == null ? 0 : e.getValue().intValue());
            }
            return clean;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    /** Map<String,Integer> -> JSON */
    @TypeConverter
    public static String fromMap(Map<String, Integer> map) {
        return map == null ? null : gson.toJson(map);
    }
}