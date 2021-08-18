package com.blogspot.abtallaldigital.data.network;

import com.blogspot.abtallaldigital.pojo.PostList;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Url;
import com.blogspot.abtallaldigital.pojo.PostList;

public interface PostAPIService {

    @GET
    Observable<Response<PostList>> getPostList(@Url String URL);

    @GET
    Observable<Response<PostList>> getPostListByLabel(@Url String URL);

    @GET
    Observable<Response<PostList>> getPostListBySearch(@Url String URL);
}
