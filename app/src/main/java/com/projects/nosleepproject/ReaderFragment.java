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

package com.projects.nosleepproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.projects.nosleepproject.models.DetailModel;
import com.projects.nosleepproject.services.ApiService;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


public class ReaderFragment extends Fragment{

    private ApiService service;
    private WebView webView;
    private TextView textView;
    private String htmltext = null;

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

        textView = (TextView) view.findViewById(R.id.webView_fragment);
            Bundle bundle = this.getArguments();
            String url = bundle.getString(ReaderActivity.READER_URL_KEY);

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
                        htmltext = response.body()[0].getData().getChildren().get(0).getChildrenData().getSelftext_html();
                        textView.setText(Html.fromHtml(htmltext));
                        textView.setMovementMethod(LinkMovementMethod.getInstance());
                    }catch (Exception e) {
                        Log.e("ReaderFragment: ", "onResponse failure");
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("detail failure: ", "");
                }
            });
        return view;
    }
}
