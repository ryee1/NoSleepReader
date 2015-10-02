package com.projects.nosleepproject.events;

import android.content.ContentValues;

import java.util.List;

/**
 * Created by ry on 9/19/15.
 */
public class QueryListingEvent {

    private List<ContentValues> valuesArray;

    public QueryListingEvent(List<ContentValues> valuesArray){
        this.valuesArray = valuesArray;

    }

    public List<ContentValues> getValuesArray() {
        return valuesArray;
    }

}
