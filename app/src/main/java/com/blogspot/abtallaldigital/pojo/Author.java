
package com.blogspot.abtallaldigital.pojo;

import java.io.Serializable;

public class Author implements Serializable {

    private String id;

    private String displayName;

    private String url;

    private com.blogspot.abtallaldigital.pojo.Image image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public com.blogspot.abtallaldigital.pojo.Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

}
