package com.blogspot.abtallaldigital.di;

import android.content.Context;

import androidx.room.Room;

import com.blogspot.abtallaldigital.data.database.ItemDAO;
import com.blogspot.abtallaldigital.data.database.ItemsDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class DatabaseModule {

    @Singleton
    @Provides
    public static ItemsDatabase provideDataBase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, ItemsDatabase.class, "items_database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    @Singleton
    @Provides
    public static ItemDAO provideDAO(ItemsDatabase itemsDatabase) {
        return itemsDatabase.itemDAO();
    }
}
