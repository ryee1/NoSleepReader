package com.projects.nosleepproject.events;

import android.content.ContentValues;

import java.util.List;

/**
 * Created by ry on 9/19/15.
 */
public class ListingLoadedEvent {
    private List<ContentValues> values;

    public ListingLoadedEvent(List<ContentValues> values) {
        this.values = values;
    }

    public List<ContentValues> getValues() {
        return values;
    }
}
