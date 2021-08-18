package com.blogspot.abtallaldigital.data;


import com.blogspot.abtallaldigital.data.LocalDataSource;
import com.blogspot.abtallaldigital.data.RemoteDataSource;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ActivityRetainedScoped;

@ActivityRetainedScoped
public class Repository {

    public RemoteDataSource remoteDataSource;
    public LocalDataSource localDataSource;


    @Inject
    public Repository(RemoteDataSource remoteDataSource, LocalDataSource localDataSource) {
        this.remoteDataSource = remoteDataSource;
        this.localDataSource = localDataSource;

    }
}
