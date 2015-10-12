/*
 * Copyright 2015 Richard Yee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.projects.nosleepreader.services;

import com.projects.nosleepreader.models.DetailModel;
import com.projects.nosleepreader.models.ListingsModel;

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

    //etc: http://www.reddit.com/r/nosleep/search.json?sort=top&raw_json=1&restrict_sr=ON&limit=30&q=timestamp:1293840000..1293926399&syntax=cloudsearch&after=&count=0
    @GET("search.json")
    Call<ListingsModel> searchBulk(@Query("sort") String TOP, @Query("raw_json") int RAW_JSON,
                                   @Query("restrict_sr") String RESTRICT_SR,
                                   @Query("q") String q, @Query("syntax") String SYNTAX, @Query("after")
                                   String after, @Query("count") int count);

    //front page
    @GET(".json")
    Call<ListingsModel> loadfrontPage(@Query("raw_json") int RAW_JSON, @Query("restrict_sr") String RESTRICT_SR,
                                     @Query("after") String after, @Query("count") int count);

    @GET(".json")
    Call<DetailModel[]> getText(@Query("raw_json") int RAW_JSON);


    @GET("search.json")
    Call<ListingsModel> searchAuthor(@Query("raw_json") int RAW_JSON, @Query("restrict_sr") String RESTRICT_SR,
                                   @Query("q") String q, @Query("after") String after, @Query("count") int count);
}

