package com.blogspot.abtallaldigital.di;

import com.blogspot.abtallaldigital.data.network.PostAPIService;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.blogspot.abtallaldigital.utils.Constans.getBaseUrl;


@InstallIn(SingletonComponent.class)
@Module
public class NetworkModule {


    @Singleton
    @Provides
    public static OkHttpClient provideHTTPClient() {
        return new OkHttpClient.Builder().readTimeout(
                15, TimeUnit.SECONDS
        ).connectTimeout(15, TimeUnit.SECONDS).build();
    }

    @Singleton
    @Provides
    public static com.blogspot.abtallaldigital.data.network.PostAPIService postAPIService() {
        return new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .client(provideHTTPClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build().create(PostAPIService.class);
    }

}
