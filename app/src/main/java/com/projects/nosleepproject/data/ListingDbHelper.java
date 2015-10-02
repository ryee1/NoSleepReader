
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

package com.projects.nosleepproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Process;
import android.provider.BaseColumns;
import android.util.Log;

import com.projects.nosleepproject.events.ListingLoadedEvent;
import com.projects.nosleepproject.events.QueryListingEvent;
import com.projects.nosleepproject.models.ListingsModel;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by ry on 9/19/15.
 */
public class ListingDbHelper extends SQLiteOpenHelper implements BaseColumns{

    private static int DATABASE_VERSION = 1;
    static String DATABASE_NAME = "listing.db";

    public static final String TABLE_NAME_YEAR_ONE = "year_2010";
    public static final String TABLE_NAME_YEAR_TWO = "year_2011";
    public static final String TABLE_NAME_YEAR_THREE = "year_2012";
    public static final String TABLE_NAME_YEAR_FOUR = "year_2013";
    public static final String TABLE_NAME_YEAR_FIVE = "year_2014";
    public static final String TABLE_NAME_YEAR_SIX = "year_2015";
    public static final String TABLE_NAME_FAVORITES = "favorites_table";

    public static final String COLUMN_ID = "r_id";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_URL = "url";

    public static final String UNIX_YEAR_ONE = "timestamp:1262304000..1293839999";
    public static final String UNIX_YEAR_TWO = "timestamp:1293840000..1325375999";
    public static final String UNIX_YEAR_THREE = "timestamp:1325376000..1356998399";
    public static final String UNIX_YEAR_FOUR = "timestamp:1356998400..1388534399";
    public static final String UNIX_YEAR_FIVE = "timestamp:1388534400..1420070399";
    public static final String UNIX_YEAR_SIX = "timestamp:1420070400.." + System.currentTimeMillis() / 1000L;

    private static ListingDbHelper singleton = null;
    public synchronized static ListingDbHelper getInstance(Context ctxt){
        if(singleton == null){
            singleton = new ListingDbHelper(ctxt.getApplicationContext());
        }
        return singleton;
    }

    public ListingDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_LIST_TABLE_ONE = "CREATE TABLE " + TABLE_NAME_YEAR_ONE + " ( " +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ID + " VARCHAR UNIQUE NOT NULL, " +
                COLUMN_AUTHOR + " TEXT NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_SCORE + " INTEGER NOT NULL, " +
                COLUMN_URL + " TEXT NOT NULL );";

        final String SQL_CREATE_LIST_TABLE_TWO = "CREATE TABLE " + TABLE_NAME_YEAR_TWO + " ( " +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ID + " VARCHAR UNIQUE NOT NULL, " +
                COLUMN_AUTHOR + " TEXT NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_SCORE + " INTEGER NOT NULL, " +
                COLUMN_URL + " TEXT NOT NULL );";

        final String SQL_CREATE_LIST_TABLE_THREE = "CREATE TABLE " + TABLE_NAME_YEAR_THREE + " ( " +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ID + " VARCHAR UNIQUE NOT NULL, " +
                COLUMN_AUTHOR + " TEXT NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_SCORE + " INTEGER NOT NULL, " +
                COLUMN_URL + " TEXT NOT NULL );";

        final String SQL_CREATE_LIST_TABLE_FOUR = "CREATE TABLE " + TABLE_NAME_YEAR_FOUR + " ( " +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ID + " VARCHAR UNIQUE NOT NULL, " +
                COLUMN_AUTHOR + " TEXT NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_SCORE + " INTEGER NOT NULL, " +
                COLUMN_URL + " TEXT NOT NULL );";

        final String SQL_CREATE_LIST_TABLE_FIVE = "CREATE TABLE " + TABLE_NAME_YEAR_FIVE + " ( " +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ID + " VARCHAR UNIQUE NOT NULL, " +
                COLUMN_AUTHOR + " TEXT NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_SCORE + " INTEGER NOT NULL, " +
                COLUMN_URL + " TEXT NOT NULL );";

        final String SQL_CREATE_LIST_TABLE_SIX = "CREATE TABLE " + TABLE_NAME_YEAR_SIX + " ( " +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ID + " VARCHAR UNIQUE NOT NULL, " +
                COLUMN_AUTHOR + " TEXT NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_SCORE + " INTEGER NOT NULL, " +
                COLUMN_URL + " TEXT NOT NULL );";

        final String SQL_CREATE_LIST_TABLE_FAVORITES = "CREATE TABLE " + TABLE_NAME_FAVORITES + " ( " +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ID + " VARCHAR UNIQUE NOT NULL, " +
                COLUMN_AUTHOR + " TEXT NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_SCORE + " INTEGER NOT NULL, " +
                COLUMN_URL + " TEXT NOT NULL );";

        sqLiteDatabase.execSQL(SQL_CREATE_LIST_TABLE_ONE);
        sqLiteDatabase.execSQL(SQL_CREATE_LIST_TABLE_TWO);
        sqLiteDatabase.execSQL(SQL_CREATE_LIST_TABLE_THREE);
        sqLiteDatabase.execSQL(SQL_CREATE_LIST_TABLE_FOUR);
        sqLiteDatabase.execSQL(SQL_CREATE_LIST_TABLE_FIVE);
        sqLiteDatabase.execSQL(SQL_CREATE_LIST_TABLE_SIX);;
        sqLiteDatabase.execSQL(SQL_CREATE_LIST_TABLE_FAVORITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_YEAR_ONE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_YEAR_TWO);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_YEAR_THREE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_YEAR_FOUR);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_YEAR_FIVE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_YEAR_SIX);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FAVORITES);
        onCreate(sqLiteDatabase);
    }

    public void loadTable(ListingsModel listingsModel, List<ContentValues> valuesArray,
                                       String table){
        new LoadListingEvent(listingsModel, valuesArray, table).start();
    }

    public void queryTable(String table, List<ContentValues> valuesarray){
        new QueryListingThread(table, valuesarray).start();
    }

    public void insertFavorites(ContentValues values){
        SQLiteDatabase db = getWritableDatabase();
        db.insertWithOnConflict(TABLE_NAME_FAVORITES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void deleteFavoites(String id){
        SQLiteDatabase db = getWritableDatabase();
        Log.e("deletefav", id);
        db.delete(TABLE_NAME_FAVORITES, COLUMN_ID + "=?", new String[] {id});
    }
    private class LoadListingEvent extends Thread{
        private ListingsModel listingsModel;
        private List<ContentValues> valuesArray;
        private String table;

        //Transfer listingsModel content into valuesArray to be posted through the EventBus
        public LoadListingEvent(ListingsModel listingsModel, List<ContentValues> valuesArray,
                                String table){
            this.listingsModel = listingsModel;
            this.table = table;
            this.valuesArray = valuesArray;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            SQLiteDatabase db = getWritableDatabase();
            List<ListingsModel.Children> list = listingsModel.getData().getChildren();
            try {
                db.beginTransaction();
                for (int i = 0; i < listingsModel.getData().getChildren().size(); i++) {

                    ContentValues values = new ContentValues();
                    values.put(COLUMN_ID, list.get(i).getChildrenData().getId());
                    values.put(COLUMN_AUTHOR, list.get(i).getChildrenData().getAuthor());
                    values.put(COLUMN_TITLE, list.get(i).getChildrenData().getTitle());
                    values.put(COLUMN_SCORE, list.get(i).getChildrenData().getScore());
                    values.put(COLUMN_URL, list.get(i).getChildrenData().getUrl());
                    valuesArray.add(values);
                    db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                }
                String after = listingsModel.getData().getAfter();
                EventBus.getDefault().postSticky(new ListingLoadedEvent(valuesArray, after));
                db.setTransactionSuccessful();
            } catch(Exception e){
                Log.e("LoadListingEvent: ", "Error inserting DB");
                e.printStackTrace();
            } finally{
                db.endTransaction();
            }
        }
    }

    private class QueryListingThread extends Thread{

        private String table;
        List<ContentValues> valuesArray;
        public QueryListingThread(String table, List<ContentValues> valuesArray){
            this.table = table;
            this.valuesArray = valuesArray;
            this.valuesArray.clear();
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            SQLiteDatabase db = getReadableDatabase();

            Cursor cursor = db.query(table, null, null, null, null, null, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                ContentValues values = new ContentValues();
                values.put(_ID, cursor.getInt(cursor.getColumnIndex(_ID)));
                values.put(COLUMN_ID, cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                values.put(COLUMN_AUTHOR, cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)));
                values.put(COLUMN_TITLE, cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                values.put(COLUMN_SCORE, cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE)));
                values.put(COLUMN_URL, cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
                valuesArray.add(values);
                cursor.moveToNext();
            }
            if(valuesArray.size() == 0){
                ContentValues values = new ContentValues();
                values.put(COLUMN_ID, "1");
                values.put(COLUMN_AUTHOR, "");
                values.put(COLUMN_TITLE, "Empty Favorites List");
                values.put(COLUMN_SCORE, "");
                values.put(COLUMN_URL, "");
                valuesArray.add(values);
            }
            EventBus.getDefault().postSticky(new QueryListingEvent(valuesArray));
        }
    }
}
