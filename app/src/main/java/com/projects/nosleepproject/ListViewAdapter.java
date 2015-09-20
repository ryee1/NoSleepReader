package com.projects.nosleepproject;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.projects.nosleepproject.data.ListingDbHelper;

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
        if(row == null){
            row = mInflater.inflate(R.layout.list_view_row, parent, false);
        }

        holder = (ViewHolder) row.getTag();
        if(holder == null){
            holder = new ViewHolder(row);
        }
        holder.title.setText(mValuesArray.get(position).getAsString(ListingDbHelper.COLUMN_TITLE));
        return row;
    }

    private class ViewHolder{
        public TextView title;

        public ViewHolder(View view){
            title = (TextView) view.findViewById(R.id.submission_title);
        }
    }
}
