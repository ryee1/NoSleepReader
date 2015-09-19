package com.projects.nosleepproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.projects.nosleepproject.events.ApiService;
import com.projects.nosleepproject.models.ListingsModel;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class ModelFragment extends Fragment {

    private ApiService service;
    public static String BASE_URL = "http://www.reddit.com/r/nosleep/";

    public ModelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(ApiService.class);
        getListings("timestamp%3A338166428..1348009628", "");
    }

    private void getListings(String timestamp, String after){
//        Call<ListingsModel> call =  service.search(service.TOP, service.SYNTAX, timestamp,
//                service.RESTRICT_SR, service.LIMIT);
        Call<ListingsModel> call =  service.test(service.TOP, service.RESTRICT_SR, service.LIMIT,
                timestamp, service.SYNTAX);

        Log.d("timestamp: ", timestamp);
        call.enqueue(new Callback<ListingsModel>() {
            @Override
            public void onResponse(Response<ListingsModel> response) {
                String a = response.body().getData().getChildren().get(0).getChildrenData().getSelftext_html();
                Log.d("Test: ", a);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Failed to get list: ", t.getMessage());
            }
        });
    }
}
