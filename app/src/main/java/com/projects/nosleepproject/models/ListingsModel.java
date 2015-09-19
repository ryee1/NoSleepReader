package com.projects.nosleepproject.models;

import java.util.List;

/**
 * Created by ry on 9/18/15.
 */
public class ListingsModel {
    private Data data;

    public Data getData() {
        return data;
    }

    public class Data{
        private List<Children> children;

        public List<Children> getChildren() {
            return children;
        }
    }

    public class Children{
        private ChildrenData data;

        public ChildrenData getChildrenData() {
            return data;
        }
    }

    public class ChildrenData{
        private String selftext_html;
        private String url;
        private String author;
        private int score;

        public String getSelftext_html() {
            return selftext_html;
        }

        public String getUrl() {
            return url;
        }

        public String getAuthor() {
            return author;
        }

        public int getScore() {
            return score;
        }
    }
}
