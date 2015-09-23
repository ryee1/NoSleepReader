package com.projects.nosleepproject.services;

import com.projects.nosleepproject.models.DetailModel;
import com.projects.nosleepproject.models.ListingsModel;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by ry on 9/18/15.
 */


public interface ApiService {
    public static String TOP = "top";
    public static String SYNTAX = "cloudsearch";
    public static String RESTRICT_SR = "ON";
    public static int RAW_JSON = 1;
    public static int LIMIT = 100;

    @GET("search.json")
    Call<ListingsModel> searchBulk(@Query("sort") String TOP, @Query("raw_json") int RAW_JSON,
                                   @Query("restrict_sr") String RESTRICT_SR, @Query("limit") int LIMIT,
                                   @Query("q") String q, @Query("syntax") String SYNTAX, @Query("after") String after);

    @GET(".json")
    Call<DetailModel[]> getText(@Query("raw_json") int RAW_JSON);
}

