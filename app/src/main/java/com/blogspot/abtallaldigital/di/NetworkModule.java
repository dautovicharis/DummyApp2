package com.blogspot.abtallaldigital.di;

import com.blogspot.abtallaldigital.data.network.PostAPIService;
import com.squareup.moshi.Moshi;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static com.blogspot.abtallaldigital.utils.Constants.getBaseUrl;


@InstallIn(SingletonComponent.class)
@Module
public class NetworkModule {

    @Singleton
    @Provides
    public static Moshi provideMoshi() {
        return new Moshi.Builder().build();
    }

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
                .addConverterFactory(MoshiConverterFactory.create(provideMoshi()))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build().create(PostAPIService.class);
    }

}
