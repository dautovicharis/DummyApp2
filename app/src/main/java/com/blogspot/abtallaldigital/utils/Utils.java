package com.blogspot.abtallaldigital.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import com.blogspot.abtallaldigital.R;

import java.util.Arrays;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class Utils {

    private static final String TAG = "Utils";

    //    private SharedPreferences sharedPreferences;
//    private boolean isUserApproved = false;


    @SuppressWarnings("unused")
    public static boolean hasNetworkAccess(Context context) {

        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static AlertDialog setProgressDialog(Context context) {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(context);
        tvText.setText("Loading ...");
        tvText.setTextColor(context.getResources().getColor(R.color.black));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(ll);

        AlertDialog dialog = builder.create();
//        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
        return dialog;
    }

    @ActivityRetainedScoped
    public static class DataStoreRepository {
        RxDataStore<Preferences> dataStore;

        public static Preferences.Key<String> RECYCLER_VIEW_LAYOUT_KEY
                = PreferencesKeys.stringKey("recyclerViewLayout");

        public static Preferences.Key<Integer> CURRENT_DESTINATION =
                PreferencesKeys.intKey("CURRENT_DESTINATION");

        public final Flowable<String> readLayoutFlow;
        public final Flowable<Integer> readCurrentDestination;

        // Array of fragment IDs
        private final Integer[] fragments = {
                R.id.nav_home,
                R.id.nav_accessory,
                R.id.nav_arcade,
                R.id.nav_fashion,
                R.id.nav_food,
                R.id.nav_heath,
                R.id.nav_lifestyle,
                R.id.nav_sports,
                R.id.nav_favorites
        };

        @Inject
        public DataStoreRepository(@ApplicationContext Context context) {
            dataStore =
                    new RxPreferenceDataStoreBuilder(Objects.requireNonNull(context), /*name=*/ "settings").build();


            readLayoutFlow = dataStore.data().map(preferences -> {
                if (preferences.get(RECYCLER_VIEW_LAYOUT_KEY) != null) {
                    return preferences.get(RECYCLER_VIEW_LAYOUT_KEY);
                } else {
                    return "cardLayout";
                }
            });

            readCurrentDestination =
                    dataStore.data().map(preferences -> {

                        Integer fragIndex = preferences.get(CURRENT_DESTINATION);
                        if (fragIndex == null) fragIndex = 0;

                        if (fragIndex >= 0 && fragIndex <= fragments.length) {
                            // Navigate to the fragIndex
                            return fragments[fragIndex];
                        } else {
                            return R.id.nav_home;
                        }
                    });
        }


        public void saveRecyclerViewLayout(String keyName, String value) {

            RECYCLER_VIEW_LAYOUT_KEY = PreferencesKeys.stringKey(keyName);

            dataStore.updateDataAsync(prefsIn -> {
                MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
                String currentKey = prefsIn.get(RECYCLER_VIEW_LAYOUT_KEY);

                if (currentKey == null) {
                    saveRecyclerViewLayout(keyName, value);
                }

                mutablePreferences.set(RECYCLER_VIEW_LAYOUT_KEY,
                        currentKey != null ? value : "cardLayout");
                return Single.just(mutablePreferences);
            }).subscribe();
        }

        public void saveCurrentDestination(String keyName, int value){

            int fragmentIndex = Arrays.asList(fragments).indexOf(value);

            CURRENT_DESTINATION = PreferencesKeys.intKey(keyName);
            dataStore.updateDataAsync(prefsIn -> {
                MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
                Integer currentKey = prefsIn.get(CURRENT_DESTINATION);

                if (currentKey == null) {
                    saveCurrentDestination(keyName,value);
                }

                mutablePreferences.set(CURRENT_DESTINATION,
                        currentKey != null ? fragmentIndex : 0);
                return Single.just(mutablePreferences);
            }).subscribe();

        }

    }

}
