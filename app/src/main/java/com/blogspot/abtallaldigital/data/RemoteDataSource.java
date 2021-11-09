package com.blogspot.abtallaldigital.data;

import com.blogspot.abtallaldigital.data.network.PostAPIService;
import com.blogspot.abtallaldigital.pojo.PostList;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class RemoteDataSource {

    private final com.blogspot.abtallaldigital.data.network.PostAPIService postAPIService;


    @Inject
    public RemoteDataSource(PostAPIService postAPIService) {
        this.postAPIService = postAPIService;
    }

    public Observable<Response<PostList>> getPostList(String URL) {
        return postAPIService.getPostList(URL);
    }

    public Observable<Response<PostList>> getPostListByLabel(String URL) {
        return postAPIService.getPostListByLabel(URL);
    }

    public Observable<Response<PostList>> getPostListBySearch(String URL){
        return postAPIService.getPostListBySearch(URL);
    }

}
