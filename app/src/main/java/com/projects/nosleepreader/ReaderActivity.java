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

package com.projects.nosleepreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.projects.nosleepreader.analytics.AnalyticsApplication;

public class ReaderActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ShareActionProvider mShareActionProvider;
    private String url;
    private static String TAG = ReaderActivity.class.getName();

    public static String READER_URL_KEY = "url_key";

    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Analytics
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        setContentView(R.layout.activity_reader);
        Intent intent = getIntent();
        if(intent.getData() != null){
            url = intent.getData().toString();
        }
        else {
            url = intent.getStringExtra(READER_URL_KEY);
        }
        ReaderFragment readerFragment = ReaderFragment.getInstance(url);
        getSupportFragmentManager().beginTransaction().replace(R.id.reader_fragment_container,
                readerFragment).commit();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Analytics
        Log.i(TAG, "Setting screen name: " + url);
        mTracker.setScreenName("Image~" + url);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
