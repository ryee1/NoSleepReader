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
    public static String BASE_URL = "http://www.reddit.com/r/nosleep/";

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
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(ApiService.class);
        getListings("timestamp:338166428..1348009628", "", contentArray, 500);
    }

    private void getListings(final String timestamp, String after,
                             final List<ContentValues> contentArray, final int minVotes){
//        Call<ListingsModel> call =  service.search(service.TOP, service.SYNTAX, timestamp,
//                service.RESTRICT_SR, service.LIMIT);
        Call<ListingsModel> call =  service.search(service.TOP, service.RAW_JSON,
                service.RESTRICT_SR, service.LIMIT, timestamp, service.SYNTAX, after);
        call.enqueue(new Callback<ListingsModel>() {

            @Override
            public void onResponse(Response<ListingsModel> response) {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                String after = response.body().getData().getAfter();
                int submissionsCount = response.body().getData().getChildren().size();
                int voteThreshold = response.body().getData().getChildren()
                        .get(submissionsCount - 1).getChildrenData().getScore();
                try {
                    if (after != null && voteThreshold > minVotes) {
                        Log.e("after: ", after);
                        mDbHelper.insertTable(response.body(), contentArray);
                        getListings(timestamp, after, contentArray, minVotes);
                    } else {
                        mDbHelper.insertTable(response.body(), contentArray);
                        tableOneLoaded = true;
                    }
                }catch(Exception e){
                    Log.e("getListings error: ", e.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("getListings: ", "failed to get list");
            }
        });
    }
}
