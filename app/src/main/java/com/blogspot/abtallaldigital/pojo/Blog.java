
package com.blogspot.abtallaldigital.pojo;

import com.squareup.moshi.Json;

import java.io.Serializable;

public class Blog implements Serializable {

    @Json(name = "id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
