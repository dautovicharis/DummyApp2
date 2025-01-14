package com.blogspot.abtallaldigital.utils;

import com.blogspot.abtallaldigital.BuildConfig;

public class Constants {
    private static final String KEY = BuildConfig.BLOGGER_KEY;
    private static final String BASE_URL = "https://www.googleapis.com/blogger/v3/blogs/4294497614198718393/posts/";
    private static final String BASE_URL_POSTS_BY_LABEL
            = "https://www.googleapis.com/blogger/v3/blogs/4294497614198718393/";

    public static String getKEY() {
        return KEY;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getBaseUrlPostsByLabel() {
        return BASE_URL_POSTS_BY_LABEL;
    }


}