package com.projects.nosleepproject;


import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.projects.nosleepproject.data.ListingDbHelper;
import com.projects.nosleepproject.events.ListingLoadedEvent;
import com.projects.nosleepproject.models.ListingsModel;
import com.projects.nosleepproject.services.ApiService;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class ModelFragment extends Fragment {

    private ApiService searchService;
    private ListingDbHelper mDbHelper;
    private List<ContentValues> contentArray;

    public static String MFRAG_BASE_URL = "http://www.reddit.com/r/nosleep/";
    public boolean scrollLoading;
    public boolean tableOneLoaded;

    public ModelFragment() {
        // Required empty public constructor
    }

    public synchronized List<ContentValues> getContentArray() {
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
        searchService = retrofit.create(ApiService.class);
    }

    public synchronized void getAuthor(String after, final List<ContentValues> contentArray,
                                       int count, String author){
        scrollLoading = true;
        Call<ListingsModel> call = searchService.searchAuthor(searchService.RAW_JSON, searchService.RESTRICT_SR,
                author, after, count);
        call.enqueue(new Callback<ListingsModel>() {
            @Override
            public void onResponse(Response<ListingsModel> response) {
                try {
                    String after = response.body().getData().getAfter();
                    ModelToContentvalue(response.body());
                    EventBus.getDefault().postSticky(new ListingLoadedEvent(contentArray, after));
                }catch (Exception e){

                }finally{
                    scrollLoading = false;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("getAuthor", "Failed to get Author List");
            }
        });
    }

    public synchronized void getListings(final String timestamp, final String after,
                             final List<ContentValues> contentArray, int count, final String table) {
        scrollLoading = true;
        Call<ListingsModel> call = searchService.searchBulk(searchService.TOP, searchService.RAW_JSON,
                searchService.RESTRICT_SR, timestamp, searchService.SYNTAX, after, count);
        call.enqueue(new Callback<ListingsModel>() {
            @Override
            public void onResponse(Response<ListingsModel> response) {
                try {
                    String after = response.body().getData().getAfter();

                    Log.e("ModelFragment: ", after);
                        mDbHelper.loadTable(response.body(), contentArray, table);

                } catch (Exception e) {
                    e.printStackTrace();
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
        Log.e("getListings:", table);
    }

    public synchronized void getFrontPage(String after, final List<ContentValues> contentArray, int count){
        scrollLoading = true;
        Call<ListingsModel> call = searchService.loadfrontPage(ApiService.RAW_JSON, ApiService.RESTRICT_SR, after, count);
        call.enqueue(new Callback<ListingsModel>() {
            @Override
            public void onResponse(Response<ListingsModel> response) {
                try {
                    String after = response.body().getData().getAfter();

                    List<ListingsModel.Children> list = response.body().getData().getChildren();
                    if (after != null) {
                        ModelToContentvalue(response.body());
                        EventBus.getDefault().postSticky(new ListingLoadedEvent(contentArray, after));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("getFrontpage:", "onResponse error");
                } finally {
                    scrollLoading = false;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                scrollLoading = false;
                Log.e("getFrontpage: ", "failed to get front page");
            }
        });
    }

    public synchronized void getFavorites(List<ContentValues> contentArray){
        mDbHelper.queryTable(ListingDbHelper.TABLE_NAME_FAVORITES, contentArray);
    }

    // Helper method to convert ListingsModel into an Arraylist of ContentValues
    private void ModelToContentvalue(ListingsModel listingsModel){
        List<ListingsModel.Children> list = listingsModel.getData().getChildren();
        for (int i = 0; i < listingsModel.getData().getChildren().size(); i++) {
            ContentValues values = new ContentValues();
            values.put(ListingDbHelper.COLUMN_ID, list.get(i).getChildrenData().getId());
            values.put(ListingDbHelper.COLUMN_AUTHOR, list.get(i).getChildrenData().getAuthor());
            values.put(ListingDbHelper.COLUMN_TITLE, list.get(i).getChildrenData().getTitle());
            values.put(ListingDbHelper.COLUMN_SCORE, list.get(i).getChildrenData().getScore());
            values.put(ListingDbHelper.COLUMN_URL, list.get(i).getChildrenData().getUrl());
            contentArray.add(values);
        }
    }
}
