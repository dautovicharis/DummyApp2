package com.blogspot.abtallaldigital.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.blogspot.abtallaldigital.data.Repository;
import com.blogspot.abtallaldigital.data.database.FavoritesEntity;
import com.blogspot.abtallaldigital.pojo.Item;
import com.blogspot.abtallaldigital.pojo.PostList;
import com.blogspot.abtallaldigital.utils.Constants;
import com.blogspot.abtallaldigital.utils.Utils;

import org.reactivestreams.Subscription;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Response;

@SuppressWarnings("unused")
@HiltViewModel
public class PostViewModel extends ViewModel {

    public static final String TAG = "PostViewModel";


    private final com.blogspot.abtallaldigital.data.Repository repository;
    public final MutableLiveData<PostList> postListMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<String> finalURL = new MutableLiveData<>();
    public final MutableLiveData<String> token = new MutableLiveData<>();
    public final MutableLiveData<String> label = new MutableLiveData<>();
    public final MutableLiveData<Integer> errorCode = new MutableLiveData<>();
    public final MutableLiveData<Boolean> searchError = new MutableLiveData<>();
    public final LiveData<List<Item>> getAllItemsFromDataBase;
    public final MutableLiveData<List<Item>>
            getItemsBySearchMT = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public final MutableLiveData<String> recyclerViewLayoutMT = new MutableLiveData<>();
    private final Utils.DataStoreRepository dataStoreRepository;
    public final MutableLiveData<Integer> currentDestination = new MutableLiveData<>();


//    public MutableLiveData<Boolean> ifAnythingWrongHappened = new MutableLiveData<>();

    @Inject
    public PostViewModel(Repository repository, Utils.DataStoreRepository dataStoreRepository) {
        this.repository = repository;
        getAllItemsFromDataBase = repository.localDataSource.getAllItems();
        this.dataStoreRepository = dataStoreRepository;
        dataStoreRepository.readLayoutFlow
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<String>() {
                    @Override
                    public void onSubscribe(@NonNull Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(String layout) {
                        if (layout != null) {
                            recyclerViewLayoutMT.setValue(layout);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError: " + t.getMessage());
                        Log.e(TAG, "onError: " + t.getCause());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        dataStoreRepository.readCurrentDestination
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .firstOrError().subscribeWith(new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull Integer destination) {
                currentDestination.setValue(destination);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }
        });

    }

    public void saveRecyclerViewLayout(String layout) {
        dataStoreRepository.saveRecyclerViewLayout("recyclerViewLayout", layout);
    }

    public void saveCurrentDestination(int currentDestination) {
        dataStoreRepository
                .saveCurrentDestination("CURRENT_DESTINATION", currentDestination);
    }


    //    @Override
//    protected void onCleared() {
//        super.onCleared();
//        postListMutableLiveData.setValue(null);
//        finalURL.setValue(null);
//        token.setValue(null);
//    }

    public void getPosts() {
        Log.e(TAG, finalURL.getValue());

        isLoading.setValue(true);
        repository.remoteDataSource.getPostList(finalURL.getValue())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<PostList>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Response<PostList> postListResponse) {

                        if (postListResponse.isSuccessful()) {
                            if (postListResponse.body() != null
                                    && postListResponse.body().getNextPageToken() != null) {
                                Log.e(TAG, postListResponse.body().getNextPageToken());
                                token.setValue(postListResponse.body().getNextPageToken());
                                isLoading.setValue(false);
                            }
                            postListMutableLiveData.setValue(postListResponse.body());

                            for (int i = 0; i < postListResponse.body().getItems().size(); i++) {
                                repository.localDataSource.insertItem(postListResponse.body()
                                        .getItems().get(i))
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
                            isLoading.setValue(false);
                            errorCode.setValue(postListResponse.code());
                            Log.e(TAG, "onNext: " + postListResponse.code());
                            Log.e(TAG, "onNext: " + postListResponse.errorBody());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        isLoading.setValue(false);
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

        isLoading.setValue(true);
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
                            isLoading.setValue(false);
                            finalURL.postValue(Constants.getBaseUrlPostsByLabel()
                                    + "posts?labels=" + label.getValue() + "&pageToken="
                                    + token.getValue()
                                    + "&key=" + Constants.getKEY());
                        } else {
                            isLoading.setValue(false);
                            errorCode.setValue(postListResponse.code());
                            Log.e(TAG, "onNext: " + postListResponse.code());
                            Log.e(TAG, "onNext: " + postListResponse.errorBody());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        isLoading.setValue(false);
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

        isLoading.setValue(true);
        searchError.setValue(false);

        String url = Constants.getBaseUrl() +
                "search?q=" + keyword + "&key=" + Constants.getKEY();

        Log.e(TAG, "getItemsBySearch: " + url);

        repository.remoteDataSource.getPostListBySearch(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<com.blogspot.abtallaldigital.pojo.PostList>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Response<PostList> postListResponse) {

                        if (postListResponse.isSuccessful()) {
                            if (postListResponse.body() != null
                                    && postListResponse.body().getNextPageToken() != null) {
                                Log.e(TAG, postListResponse.body().getNextPageToken());
                                token.setValue(postListResponse.body().getNextPageToken());
                                isLoading.setValue(false);
                            }
                            postListMutableLiveData.setValue(postListResponse.body());
                        } else {
                            isLoading.setValue(false);
                            searchError.setValue(true);
                            Log.e(TAG, "onNext: list is null");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        isLoading.setValue(false);
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
                        } else {
                            getItemsBySearchMT.setValue(items);
                            Log.d(TAG, "onNext: " + items.size());
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

    public void insertFavorites(FavoritesEntity favoritesEntity) {
        repository.localDataSource.insertFavorites(favoritesEntity)
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
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    public LiveData<List<FavoritesEntity>> getAllFavorites() {
        return repository.localDataSource.getAllFavorites();
    }

    public void deleteFavoritePost(FavoritesEntity favoritesEntity) {
        repository.localDataSource.deleteFavorite(favoritesEntity);
    }

    public void deleteAllFavorites() {
        repository.localDataSource.deleteAllFavorites()
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
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                });


    }


}
