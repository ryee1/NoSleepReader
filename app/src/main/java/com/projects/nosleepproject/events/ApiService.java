package com.projects.nosleepproject.events;

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
    public static int LIMIT = 100;

    @GET("search.json")
    Call<ListingsModel> search(@Query("sort") String TOP, @Query("syntax") String SYNTAX,
                                    @Query("q") String timestamp, @Query("restrict_sr") String ON,
                                    @Query("limit") int LIMIT);

    @GET("search.json")
    Call<ListingsModel> test(@Query("sort") String TOP, @Query("restrict_sr") String RESTRICT_SR,
                               @Query("limit") int LIMIT,
                             @Query("q") String q, @Query("syntax") String SYNTAX);
}

