package com.projects.nosleepproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Process;
import android.provider.BaseColumns;
import android.util.Log;

import com.projects.nosleepproject.events.ListingLoadedEvent;
import com.projects.nosleepproject.models.ListingsModel;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by ry on 9/19/15.
 */
public class ListingDbHelper extends SQLiteOpenHelper implements BaseColumns{

    private static int DATABASE_VERSION = 1;
    static String DATABASE_NAME = "listing.db";

    public static final String TABLE_NAME_YEAR_ONE = "year_one";
    public static final String TABLE_NAME_YEAR_TWO = "year_two";
    public static final String TABLE_NAME_YEAR_THREE = "year_three";
    public static final String TABLE_NAME_YEAR_FOUR = "year_four";

    public static final String COLUMN_ID = "r_id";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_URL = "url";


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

        // CREATE TABLE year one ( _ID INTEGER PRIMARY KEY, author TEXT NOT NULL, title TEXT NOT NULL,
        // score INTEGER NOT NULL, url TEXT NOT NULL );

        final String SQL_CREATE_LIST_TABLE = "CREATE TABLE " + TABLE_NAME_YEAR_ONE + " ( " +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ID + " VARCHAR NOT NULL, " +
                COLUMN_AUTHOR + " TEXT UNIQUE NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_SCORE + " INTEGER NOT NULL, " +
                COLUMN_URL + " TEXT NOT NULL );";

        sqLiteDatabase.execSQL(SQL_CREATE_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.e("onUpgrade: ", "How did I get here");
    }

    public void insertTable(ListingsModel listingsModel, List<ContentValues> valuesArray){
        new LoadListingEvent(listingsModel, valuesArray).start();
    }

    private class LoadListingEvent extends Thread{
        private ListingsModel listingsModel;
        private boolean lastPage;
        private List<ContentValues> valuesArray;
        public LoadListingEvent(ListingsModel listingsModel, List<ContentValues> valuesArray){
            this.listingsModel = listingsModel;
            this.lastPage = lastPage;
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
                    db.insertWithOnConflict(TABLE_NAME_YEAR_ONE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                EventBus.getDefault().post(new ListingLoadedEvent(valuesArray));
                db.setTransactionSuccessful();
            } catch(Exception e){
                Log.e("Error Loading db: ", valuesArray.get(0).getAsString(COLUMN_TITLE));
                e.printStackTrace();
            } finally{
                db.endTransaction();
            }
        }
    }

    private class QueryListingThread extends Thread{

        SQLiteDatabase db = getReadableDatabase();
        public QueryListingThread(){

        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        }
    }
}
