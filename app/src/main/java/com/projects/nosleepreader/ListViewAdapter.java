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
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.projects.nosleepreader.data.ListingDbHelper;

import java.util.List;

/**
 * Created by ry on 9/19/15.
 */
public class ListViewAdapter extends BaseAdapter {

    private List<ContentValues> mValuesArray;
    private LayoutInflater mInflater;
    public ListViewAdapter(Context context, List mValuesArray){
        this.mValuesArray = mValuesArray;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mValuesArray.size();
    }

    @Override
    public Object getItem(int i) {
        return mValuesArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        String author = mValuesArray.get(position).getAsString(ListingDbHelper.COLUMN_AUTHOR);
        if(row == null){
            row = mInflater.inflate(R.layout.list_view_row, parent, false);
        }

        holder = (ViewHolder) row.getTag();
        if(holder == null){
            holder = new ViewHolder(row);
        }
        holder.title.setText(mValuesArray.get(position).getAsString(ListingDbHelper.COLUMN_TITLE));
        holder.score.setText(mValuesArray.get(position).getAsString(ListingDbHelper.COLUMN_SCORE));
        holder.author.setText("Submitted by: " + author);

        // Dynamically set Score's width to the width taken up by the String "0000"
        float measureText = holder.score.getPaint().measureText("0000");
        holder.score.setWidth(holder.score.getPaddingLeft() + holder.score.getPaddingRight() + (int) measureText);
        return row;
    }

    private class ViewHolder{
        public TextView title;
        public TextView score;
        public TextView author;

        public ViewHolder(View view){
            title = (TextView) view.findViewById(R.id.submission_title);
            score = (TextView) view.findViewById(R.id.score);
            author = (TextView) view.findViewById(R.id.author);
        }
    }
}
