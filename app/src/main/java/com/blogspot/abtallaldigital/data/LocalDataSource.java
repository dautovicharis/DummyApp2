package com.blogspot.abtallaldigital.data;

import androidx.lifecycle.LiveData;

import com.blogspot.abtallaldigital.data.database.FavoritesEntity;
import com.blogspot.abtallaldigital.data.database.ItemDAO;
import com.blogspot.abtallaldigital.pojo.Item;
import com.blogspot.abtallaldigital.pojo.PostList;


import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public class LocalDataSource {

    private final ItemDAO itemDAO;

    @Inject
    public LocalDataSource(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    public Completable insertItem(Item item) {
        return itemDAO.insert(item);
    }

    public LiveData<List<Item>> getAllItems() {
        return itemDAO.getAlItems();
    }

    public Observable<List<Item>> getItemsBySearch(String keyword) {
        return itemDAO.getItemsBySearch(keyword);
    }

    public Completable insertFavorites(FavoritesEntity favoritesEntity) {
        return itemDAO.insertFavorites(favoritesEntity);
    }

    public LiveData<List<FavoritesEntity>> getAllFavorites() {
        return itemDAO.getAllFavorites();
    }

    public void deleteFavorite(FavoritesEntity favoritesEntity) {
        itemDAO.deleteFavorite(favoritesEntity);
    }

    public Completable deleteAllFavorites() {
        return itemDAO.deleteAllFavorites();
    }
}
