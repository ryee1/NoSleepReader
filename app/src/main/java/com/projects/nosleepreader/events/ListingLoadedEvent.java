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

package com.projects.nosleepreader.events;

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
