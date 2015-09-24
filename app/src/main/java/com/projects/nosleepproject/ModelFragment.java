package com.projects.nosleepproject;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.projects.nosleepproject.data.ListingDbHelper;
import com.projects.nosleepproject.models.ListingsModel;
import com.projects.nosleepproject.services.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class ModelFragment extends Fragment {

    private ApiService service;
    private ListingDbHelper mDbHelper;
    public static String MFRAG_BASE_URL = "http://www.reddit.com/r/nosleep/";
    public boolean scrollLoading;
    public boolean tableOneLoaded;

    public ModelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);


        List<ContentValues> contentArray = new ArrayList<>();
        mDbHelper = ListingDbHelper.getInstance(getActivity());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MFRAG_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(ApiService.class);
        getListings("timestamp:338166428..1348009628", "", contentArray, 0);
    }

    public void getListings(final String timestamp, final String after,
                             final List<ContentValues> contentArray, int count) {
        scrollLoading = true;
        Call<ListingsModel> call = service.searchBulk(service.TOP, service.RAW_JSON,
                service.RESTRICT_SR, 30, timestamp, service.SYNTAX, after, count);
        call.enqueue(new Callback<ListingsModel>() {

            @Override
            public void onResponse(Response<ListingsModel> response) {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                try {
                    String after = response.body().getData().getAfter();
                    if(after != null) {
                        Log.e("Model After: ", after);
                        mDbHelper.insertTable(response.body(), contentArray);
                    }

                } catch (Exception e) {
                    Log.e("getListings error: ", after);
                }
                finally{
                    scrollLoading = false;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                scrollLoading = false;
                Log.e("getListings: ", "failed to get list");
            }
        });
    }
}
