package com.blogspot.abtallaldigital.data;


import com.blogspot.abtallaldigital.data.LocalDataSource;
import com.blogspot.abtallaldigital.data.RemoteDataSource;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ActivityRetainedScoped;

@ActivityRetainedScoped
public class Repository {

    public final RemoteDataSource remoteDataSource;
    public final LocalDataSource localDataSource;


    @Inject
    public Repository(RemoteDataSource remoteDataSource, LocalDataSource localDataSource) {
        this.remoteDataSource = remoteDataSource;
        this.localDataSource = localDataSource;

    }
}
