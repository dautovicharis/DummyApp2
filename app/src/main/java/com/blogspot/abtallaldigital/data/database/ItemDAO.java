package com.blogspot.abtallaldigital.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.blogspot.abtallaldigital.pojo.Item;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface ItemDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Item item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertFavorites(FavoritesEntity favoritesEntity);

    @Query("SELECT * FROM FAVORITES")
    LiveData<List<FavoritesEntity>> getAllFavorites();

    @Delete
    void deleteFavorite(FavoritesEntity favoritesEntity);

    @Query("DELETE FROM FAVORITES")
    Completable deleteAllFavorites();


    @Query("SELECT * FROM item_table order by datetime(published) DESC")
    LiveData<List<Item>> getAlItems();

    @Query("SELECT * FROM item_table WHERE title like '%' || :keyword || '%' ")
    Observable<List<Item>> getItemsBySearch(String keyword);
}
