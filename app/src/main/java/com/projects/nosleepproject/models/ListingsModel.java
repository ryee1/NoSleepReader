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
        private String after;
        private String before;

        public List<Children> getChildren() {
            return children;
        }

        public String getAfter() {
            return after;
        }

        public String getBefore() {
            return before;
        }
    }

    public class Children{
        private ChildrenData data;

        public ChildrenData getChildrenData() {
            return data;
        }
    }

    public class ChildrenData{
        private String url;
        private String author;
        private String title;
        private int score;
        private String id;
        private String selftext_html;

        public String getSelftext_html() {
            return selftext_html;
        }

        public String getUrl() {
            return url;
        }

        public String getAuthor() {
            return author;
        }

        public String getTitle() {
            return title;
        }

        public String getId() {
            return id;
        }

        public int getScore() {
            return score;
        }
    }
}
