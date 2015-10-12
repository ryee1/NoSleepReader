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

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projects.nosleepreader.data.ListingDbHelper;
import com.projects.nosleepreader.models.DetailModel;
import com.projects.nosleepreader.services.ApiService;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


public class ReaderFragment extends Fragment{

    private ListingDbHelper mDbHelper;
    private ShareActionProvider mShareActionProvider;
    private ApiService service;
    private TextView textView;
    private TextView title;
    private String htmltext = null;
    private String url;
    private ContentValues values = null;
    private int mfontSize;
    private int mtitleSize;


    @Override
    public void onResume() {
        super.onResume();
        getFontSizes();
    }

    private void getFontSizes(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String font = prefs.getString(getContext().getString(R.string.pref_font_size_key),
                getContext().getString(R.string.pref_font_size_default));
        mfontSize = Integer.parseInt(font);
        if(mfontSize < 16)
            mtitleSize = 20;
        else if(mfontSize > 16)
            mtitleSize = 28;
        else
            mtitleSize = 24;

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mfontSize);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, mtitleSize);

    }

    public static ReaderFragment getInstance(String url){
        ReaderFragment f = new ReaderFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ReaderActivity.READER_URL_KEY, url);
        f.setArguments(bundle);
        return f;
    }

    public ReaderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reader, container, false);

        setHasOptionsMenu(true);
        mDbHelper = ListingDbHelper.getInstance(getContext());
        textView = (TextView) view.findViewById(R.id.webView_fragment);
        title = (TextView) view.findViewById(R.id.reader_title);

        Bundle bundle = this.getArguments();
            String url = this.url = bundle.getString(ReaderActivity.READER_URL_KEY);

            String base_url = url.substring(0, url.length() - 1);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(ApiService.class);
            Call<DetailModel[]> call = service.getText(ApiService.RAW_JSON);
            call.enqueue(new Callback<DetailModel[]>() {
                @Override
                public void onResponse(Response<DetailModel[]> response) {
                    try {
                        DetailModel.ChildrenData data = response.body()[0].getData().getChildren().get(0).getChildrenData();
                        convertToContentValues(data);
                        htmltext = data.getSelftext_html();
                        title.setText(data.getTitle());
                        textView.setText(Html.fromHtml(htmltext));
                        textView.setMovementMethod(LinkMovementMethod.getInstance());
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("detail failure: ", "");
                }
            });
        return view;
    }

    public void convertToContentValues(DetailModel.ChildrenData data) {

        ContentValues values = new ContentValues();
        values.put(ListingDbHelper.COLUMN_ID, data.getId());
        values.put(ListingDbHelper.COLUMN_AUTHOR, data.getAuthor());
        values.put(ListingDbHelper.COLUMN_TITLE, data.getTitle());
        values.put(ListingDbHelper.COLUMN_SCORE, data.getScore());
        values.put(ListingDbHelper.COLUMN_URL, data.getUrl());

        this.values = values;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_reader, menu);
        MenuItem item = menu.findItem(R.id.reader_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        mShareActionProvider.setShareIntent(getDefaultIntent());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (values == null){
            return false;
        }
        switch (id) {
            case R.id.open_url_in_browser:
                String url = values.getAsString(ListingDbHelper.COLUMN_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;
            case R.id.reader_favorite:
                mDbHelper.insertFavorites(values);
                break;
            case R.id.action_settings:
                getActivity().startActivity(new Intent(getActivity(), PreferenceActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent getDefaultIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.setType("text/plain");
        return intent;
    }
}
