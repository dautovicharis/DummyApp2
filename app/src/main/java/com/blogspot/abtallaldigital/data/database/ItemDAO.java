package com.blogspot.abtallaldigital.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface ItemDAO {
    @Insert
    Completable insert(com.blogspot.abtallaldigital.pojo.Item item);

    @Query("SELECT * FROM item_table")
    LiveData<List<com.blogspot.abtallaldigital.pojo.Item>> getAlItems();

    @Query("SELECT * FROM item_table WHERE title like '%' || :keyword || '%' ")
    Observable<List<com.blogspot.abtallaldigital.pojo.Item>> getItemsBySearch(String keyword);
}
