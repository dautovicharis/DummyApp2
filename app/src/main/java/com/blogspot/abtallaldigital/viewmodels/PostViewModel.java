package com.blogspot.abtallaldigital.viewmodels;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.blogspot.abtallaldigital.data.Repository;
import com.blogspot.abtallaldigital.pojo.Item;
import com.blogspot.abtallaldigital.pojo.PostList;
import com.blogspot.abtallaldigital.utils.Constans;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Response;

@SuppressWarnings("unused")
@HiltViewModel
public class PostViewModel extends ViewModel {

    public static final String TAG = "PostViewModel";


    private final com.blogspot.abtallaldigital.data.Repository repository;
    public final MutableLiveData<com.blogspot.abtallaldigital.pojo.PostList> postListMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<String> finalURL = new MutableLiveData<>();
    public final MutableLiveData<String> token = new MutableLiveData<>();
    public final MutableLiveData<String> label = new MutableLiveData<>();
    public final MutableLiveData<Integer> errorCode = new MutableLiveData<>();
    public final MutableLiveData<Boolean> searchError = new MutableLiveData<>();
    public final LiveData<List<com.blogspot.abtallaldigital.pojo.Item>> getAllItemsFromDataBase;
    public final MutableLiveData<List<com.blogspot.abtallaldigital.pojo.Item>> getItemsBySearchMT = new MutableLiveData<>();

//    public MutableLiveData<Boolean> ifAnythingWrongHappened = new MutableLiveData<>();

    @Inject
    public PostViewModel(Repository repository) {
        this.repository = repository;
        getAllItemsFromDataBase = repository.localDataSource.getAllItems();
    }


    //    @Override
//    protected void onCleared() {
//        super.onCleared();
//        postListMutableLiveData.setValue(null);
//        finalURL.setValue(null);
//        token.setValue(null);
//    }

    @SuppressLint("CheckResult")
    public void getPosts() {
//        ifAnythingWrongHappened.setValue(false);
        Log.e(TAG, finalURL.getValue());

        repository.remoteDataSource.getPostList(finalURL.getValue())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<com.blogspot.abtallaldigital.pojo.PostList>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Response<com.blogspot.abtallaldigital.pojo.PostList> postListResponse) {

                        if (postListResponse.isSuccessful()) {
                            if (postListResponse.body() != null
                                    && postListResponse.body().getNextPageToken() != null) {
                                Log.e(TAG, postListResponse.body().getNextPageToken());
                                token.setValue(postListResponse.body().getNextPageToken());
                            }
                            postListMutableLiveData.setValue(postListResponse.body());

                            for (int i = 0; i < postListResponse.body().getItems().size(); i++) {
                                repository.localDataSource.insertItem(postListResponse.body().getItems().get(i))
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new CompletableObserver() {
                                            @Override
                                            public void onSubscribe(@NonNull Disposable d) {

                                            }

                                            @Override
                                            public void onComplete() {

                                            }

                                            @Override
                                            public void onError(@NonNull Throwable e) {

                                            }
                                        });
                            }

                            finalURL.setValue(finalURL.getValue() + "&pageToken=" + token.getValue());
                        } else {
                            errorCode.setValue(postListResponse.code());
                            Log.e(TAG, "onNext: " + postListResponse.code());
                            Log.e(TAG, "onNext: " + postListResponse.errorBody());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, e.getMessage() + e.getCause());
//                        ifAnythingWrongHappened.setValue(true);
                        if (e instanceof HttpException) {
                            errorCode.setValue(((HttpException) e).code());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    public void getPostListByLabel() {

//        ifAnythingWrongHappened.setValue(false);

        Log.e(TAG, finalURL.getValue());

        repository.remoteDataSource.getPostListByLabel(finalURL.getValue())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<com.blogspot.abtallaldigital.pojo.PostList>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Response<com.blogspot.abtallaldigital.pojo.PostList> postListResponse) {
                        if (postListResponse.isSuccessful()) {
                            if (postListResponse.body() != null) {
//                                Log.e(TAG, postListResponse.body().getNextPageToken());
                                token.setValue(postListResponse.body().getNextPageToken());
                            }
                            postListMutableLiveData.setValue(postListResponse.body());
                            finalURL.postValue(com.blogspot.abtallaldigital.utils.Constans.getBaseUrlPostsByLabel()
                                    + "posts?labels=" + label.getValue() + "&pageToken="
                                    + token.getValue()
                                    + "&key=" + com.blogspot.abtallaldigital.utils.Constans.getKEY());
                        } else {
                            errorCode.setValue(postListResponse.code());
                            Log.e(TAG, "onNext: " + postListResponse.code());
                            Log.e(TAG, "onNext: " + postListResponse.errorBody());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, e.getMessage() + e.getCause());
                        if (e instanceof HttpException) {
                            errorCode.setValue(((HttpException) e).code());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void getItemsBySearch(String keyword) {

        searchError.setValue(false);

        String url = com.blogspot.abtallaldigital.utils.Constans.getBaseUrl() +
                "search?q=" + keyword + "&key=" + Constans.getKEY();

        Log.e(TAG, "getItemsBySearch: " + url);

        repository.remoteDataSource.getPostListBySearch(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<com.blogspot.abtallaldigital.pojo.PostList>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Response<com.blogspot.abtallaldigital.pojo.PostList> postListResponse) {
                        PostList list = postListResponse.body();
                        if (list != null) {
                            if (list.getItems() == null) {
                                searchError.setValue(true);
                                Log.e(TAG, "onNext: list is null");
                            } else {
                                postListMutableLiveData.setValue(list);
                                token.setValue(list.getNextPageToken());
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, e.getMessage() + e.getCause());
//                        ifAnythingWrongHappened.setValue(true);
                        if (e instanceof HttpException) {
                            errorCode.setValue(((HttpException) e).code());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void getItemsBySearchInDB(String keyword) {
        Log.d(TAG, "getItemsBySearchInDB: called");
        repository.localDataSource.getItemsBySearch(keyword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<com.blogspot.abtallaldigital.pojo.Item>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<Item> items) {
                        if (items.isEmpty()) {
                            searchError.setValue(true);
                            Log.e(TAG, "onNext: list is empty");
                        }else {
                            getItemsBySearchMT.setValue(items);
                            Log.d(TAG, "onNext: "+ items.size());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        searchError.setValue(true);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

}
