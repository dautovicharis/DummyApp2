
package com.blogspot.abtallaldigital.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

public class PostList implements Parcelable {

    @Json(name = "kind")
    private String kind;

    @Json(name = "nextPageToken")
    private String nextPageToken;

    @Json(name = "items")
    private List<com.blogspot.abtallaldigital.pojo.Item> items = null;

    @Json(name = "etag")
    private String etag;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<com.blogspot.abtallaldigital.pojo.Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.kind);
        dest.writeString(this.nextPageToken);
        dest.writeList(this.items);
        dest.writeString(this.etag);
    }

    public void readFromParcel(Parcel source) {
        this.kind = source.readString();
        this.nextPageToken = source.readString();
        this.items = new ArrayList<Item>();
        source.readList(this.items, Item.class.getClassLoader());
        this.etag = source.readString();
    }

    public PostList() {
    }

    protected PostList(Parcel in) {
        this.kind = in.readString();
        this.nextPageToken = in.readString();
        this.items = new ArrayList<>();
        in.readList(this.items, Item.class.getClassLoader());
        this.etag = in.readString();
    }

    public static final Parcelable.Creator<PostList> CREATOR = new Parcelable.Creator<PostList>() {
        @Override
        public PostList createFromParcel(Parcel source) {
            return new PostList(source);
        }

        @Override
        public PostList[] newArray(int size) {
            return new PostList[size];
        }
    };
}
