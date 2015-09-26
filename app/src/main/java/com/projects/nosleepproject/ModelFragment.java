package com.projects.nosleepproject;


import android.content.ContentValues;
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
    private List<ContentValues> contentArray;

    public static String MFRAG_BASE_URL = "http://www.reddit.com/r/nosleep/";
    public boolean scrollLoading;
    public boolean tableOneLoaded;

    public ModelFragment() {
        // Required empty public constructor
    }

    public List<ContentValues> getContentArray() {
        return contentArray;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        contentArray = new ArrayList<>();
        mDbHelper = ListingDbHelper.getInstance(getActivity());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MFRAG_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(ApiService.class);
      //  getListings("timestamp:338166428..1348009628", "", contentArray, 0);
    }

    public void getListings(final String timestamp, final String after,
                             final List<ContentValues> contentArray, int count, final String table) {
        scrollLoading = true;
        Call<ListingsModel> call = service.searchBulk(service.TOP, service.RAW_JSON,
                service.RESTRICT_SR, timestamp, service.SYNTAX, after, count);
        call.enqueue(new Callback<ListingsModel>() {
            @Override
            public void onResponse(Response<ListingsModel> response) {
                try {
                    String after = response.body().getData().getAfter();

                    Log.e("ModelFragment: ", after);
                    if(after != null) {
                        mDbHelper.insertTable(response.body(), contentArray, table);
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
        Log.e("Modelfragment before:", table);
    }
}
