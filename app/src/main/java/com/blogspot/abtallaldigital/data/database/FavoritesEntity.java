package com.blogspot.abtallaldigital.data.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.blogspot.abtallaldigital.pojo.Item;

@Entity(tableName = "favorites")
public class FavoritesEntity implements Parcelable {


    @PrimaryKey(autoGenerate = true)
    private int id;

    private Item item;


    public FavoritesEntity(int id, Item item) {
        this.id = id;
        this.item = item;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeParcelable(this.item, flags);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readInt();
        this.item = source.readParcelable(Item.class.getClassLoader());
    }

    protected FavoritesEntity(Parcel in) {
        this.id = in.readInt();
        this.item = in.readParcelable(Item.class.getClassLoader());
    }

    public static final Parcelable.Creator<FavoritesEntity> CREATOR = new Parcelable.Creator<FavoritesEntity>() {
        @Override
        public FavoritesEntity createFromParcel(Parcel source) {
            return new FavoritesEntity(source);
        }

        @Override
        public FavoritesEntity[] newArray(int size) {
            return new FavoritesEntity[size];
        }
    };
}
