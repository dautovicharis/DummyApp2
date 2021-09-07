package com.blogspot.abtallaldigital.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

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

//    public final void is_EEA_country(Context context) {
//
//        String EEA_COUNRIES[] = {"Austria", "Belgium",
//                "Bulgaria", "Croatia", "Republic of Cyprus",
//                "Czech Republic", "Denmark", "Estonia", "Finland",
//                "France", "Germany", "Greece", "Hungary", "Ireland",
//                "Italy", "Latvia", "Lithuania", "Luxembourg", "Malta",
//                "Netherlands", "Poland", "Portugal", "Romania", "Slovakia",
//                "Slovenia", "Spain", "Sweden", "United Kingdom"};
//
//        GeoIpService ipApiService = ServicesManager.getGeoIpService();
//        ipApiService.getGeoIp().enqueue(new Callback<GeoIpResponseModel>() {
//            @Override
//            public void onResponse(@NonNull Call<GeoIpResponseModel> call,
//                                   @NonNull retrofit2.Response<GeoIpResponseModel> response) {
//                if (response.isSuccessful()) {
//
//                    assert response.body() != null;
//                    String countryName = response.body().getCountryName();
//
//                    if (Arrays.asList(EEA_COUNRIES).contains(countryName)) {
//
//
//                    } else {
//                        if (context instanceof MainActivity) {
//
//                        } else {
//
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<GeoIpResponseModel> call, @NonNull Throwable t) {
//                Log.e(TAG,t.toString());
//            }
//        });
//
//    }

}
