package com.blogspot.abtallaldigital.data.database;

import androidx.room.TypeConverter;

import com.blogspot.abtallaldigital.pojo.Item;
import com.blogspot.abtallaldigital.pojo.PostList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ItemTypeConverter {


    @TypeConverter
    public static String postListToString(Item item) {
        Gson gson = new Gson();
        return gson.toJson(item);
    }

    @TypeConverter
    public static Item stringToPostList(String value) {
        Type listType = new TypeToken<Item>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
}
