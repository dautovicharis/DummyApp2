package com.blogspot.abtallaldigital.data.database;

import com.blogspot.abtallaldigital.pojo.Item;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Item.class, FavoritesEntity.class}, version = 1, exportSchema = false)
//@TypeConverters(Converters.class)
@TypeConverters(ItemTypeConverter.class)
public abstract class ItemsDatabase extends RoomDatabase {

    //    private static ItemsDatabase INSTANCE;
//
    public abstract ItemDAO itemDAO();
//
//    public static synchronized ItemsDatabase getINSTANCE(Context context) {
//        if (INSTANCE == null) {
//            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ItemsDatabase.class,
//                    "items_database")
//                    .fallbackToDestructiveMigration()
//                    .addCallback(roomCallBack)
//                    .allowMainThreadQueries()
//                    .build();
//        }
//        return INSTANCE;
//    }
//
//    private static final Callback roomCallBack = new Callback() {
//        @Override
//        public void onCreate(@NonNull SupportSQLiteDatabase db) {
//            super.onCreate(db);
//        }
//
//        @Override
//        public void onOpen(@NonNull SupportSQLiteDatabase db) {
//            super.onOpen(db);
//        }
//    };


}
