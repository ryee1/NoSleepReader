package com.projects.nosleepproject.events;

import android.content.ContentValues;

import java.util.List;

/**
 * Created by ry on 9/19/15.
 */
public class ListingLoadedEvent {
    private List<ContentValues> values;
    private String after;

    public ListingLoadedEvent(List<ContentValues> values, String after) {
        this.values = values;
        this.after = after;
    }

    public String getAfter() {
        return after;
    }

    public List<ContentValues> getValues() {
        return values;
    }
}
