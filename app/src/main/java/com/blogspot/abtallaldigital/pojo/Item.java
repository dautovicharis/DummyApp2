
package com.blogspot.abtallaldigital.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.squareup.moshi.Json;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "item_table")
public class Item implements Serializable, Parcelable {

    public int getReDefinedID() {
        return reDefinedID;
    }

    public void setReDefinedID(int reDefinedID) {
        this.reDefinedID = reDefinedID;
    }

    @PrimaryKey(autoGenerate = true)
    private int reDefinedID;

    @Json(name = "kind")
    private String kind;

    @Json(name = "id")
    private String id;

    @Json(name = "blog")
    //@DatabaseField (foreign = true, foreignAutoRefresh = true)
    @Ignore
    private com.blogspot.abtallaldigital.pojo.Blog blog;
    @Json(name = "published")
    private String published;

    @Json(name = "updated")
    private String updated;

    @Json(name = "etag")
    private String etag;

    @Json(name = "url")
    private String url;

    @Json(name = "selfLink")
    private String selfLink;

    @Json(name = "title")
    private String title;

    @Json(name = "content")
    private String content;

    @Json(name = "author")
    @Ignore
    private Author author;
    @Ignore
    @Json(name = "labels")
    private List<String> labels;


    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public com.blogspot.abtallaldigital.pojo.Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public com.blogspot.abtallaldigital.pojo.Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }


    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.reDefinedID);
        dest.writeString(this.kind);
        dest.writeString(this.id);
        dest.writeSerializable(this.blog);
        dest.writeString(this.published);
        dest.writeString(this.updated);
        dest.writeString(this.etag);
        dest.writeString(this.url);
        dest.writeString(this.selfLink);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeSerializable(this.author);
        dest.writeStringList(this.labels);
    }

    public void readFromParcel(Parcel source) {
        this.reDefinedID = source.readInt();
        this.kind = source.readString();
        this.id = source.readString();
        this.blog = (Blog) source.readSerializable();
        this.published = source.readString();
        this.updated = source.readString();
        this.etag = source.readString();
        this.url = source.readString();
        this.selfLink = source.readString();
        this.title = source.readString();
        this.content = source.readString();
        this.author = (Author) source.readSerializable();
        this.labels = source.createStringArrayList();
    }

    public Item() {
    }

    protected Item(Parcel in) {
        this.reDefinedID = in.readInt();
        this.kind = in.readString();
        this.id = in.readString();
        this.blog = (Blog) in.readSerializable();
        this.published = in.readString();
        this.updated = in.readString();
        this.etag = in.readString();
        this.url = in.readString();
        this.selfLink = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.author = (Author) in.readSerializable();
        this.labels = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
