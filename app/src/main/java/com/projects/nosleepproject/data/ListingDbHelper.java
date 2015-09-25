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

    public static final String TABLE_NAME_YEAR_ONE = "year_2010";
    public static final String TABLE_NAME_YEAR_TWO = "year_2011";
    public static final String TABLE_NAME_YEAR_THREE = "year_2012";
    public static final String TABLE_NAME_YEAR_FOUR = "year_2013";
    public static final String TABLE_NAME_YEAR_FIVE = "year_2014";
    public static final String TABLE_NAME_YEAR_SIX = "year_2015";

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
                COLUMN_ID + " VARCHAR NOT NULL, " +
                COLUMN_AUTHOR + " TEXT UNIQUE NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_SCORE + " INTEGER NOT NULL, " +
                COLUMN_URL + " TEXT NOT NULL );";

        final String SQL_CREATE_LIST_TABLE_TWO = "CREATE TABLE " + TABLE_NAME_YEAR_TWO + " ( " +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ID + " VARCHAR NOT NULL, " +
                COLUMN_AUTHOR + " TEXT UNIQUE NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_SCORE + " INTEGER NOT NULL, " +
                COLUMN_URL + " TEXT NOT NULL );";

        final String SQL_CREATE_LIST_TABLE_THREE = "CREATE TABLE " + TABLE_NAME_YEAR_THREE + " ( " +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ID + " VARCHAR NOT NULL, " +
                COLUMN_AUTHOR + " TEXT UNIQUE NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_SCORE + " INTEGER NOT NULL, " +
                COLUMN_URL + " TEXT NOT NULL );";

        final String SQL_CREATE_LIST_TABLE_FOUR = "CREATE TABLE " + TABLE_NAME_YEAR_FOUR + " ( " +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ID + " VARCHAR NOT NULL, " +
                COLUMN_AUTHOR + " TEXT UNIQUE NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_SCORE + " INTEGER NOT NULL, " +
                COLUMN_URL + " TEXT NOT NULL );";

        final String SQL_CREATE_LIST_TABLE_FIVE = "CREATE TABLE " + TABLE_NAME_YEAR_FIVE + " ( " +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ID + " VARCHAR NOT NULL, " +
                COLUMN_AUTHOR + " TEXT UNIQUE NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_SCORE + " INTEGER NOT NULL, " +
                COLUMN_URL + " TEXT NOT NULL );";

        final String SQL_CREATE_LIST_TABLE_SIX = "CREATE TABLE " + TABLE_NAME_YEAR_SIX + " ( " +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ID + " VARCHAR NOT NULL, " +
                COLUMN_AUTHOR + " TEXT UNIQUE NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_SCORE + " INTEGER NOT NULL, " +
                COLUMN_URL + " TEXT NOT NULL );";

        sqLiteDatabase.execSQL(SQL_CREATE_LIST_TABLE_ONE);
        sqLiteDatabase.execSQL(SQL_CREATE_LIST_TABLE_TWO);
        sqLiteDatabase.execSQL(SQL_CREATE_LIST_TABLE_THREE);
        sqLiteDatabase.execSQL(SQL_CREATE_LIST_TABLE_FOUR);
        sqLiteDatabase.execSQL(SQL_CREATE_LIST_TABLE_FIVE);
        sqLiteDatabase.execSQL(SQL_CREATE_LIST_TABLE_SIX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.e("onUpgrade: ", "How did I get here");
    }

    public synchronized void insertTable(ListingsModel listingsModel, List<ContentValues> valuesArray,
                                         String table){
        new LoadListingEvent(listingsModel, valuesArray, table).start();
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
                Log.e("ListingDbHelper: ", valuesArray.get(0).getAsString(COLUMN_AUTHOR));
                EventBus.getDefault().postSticky(new ListingLoadedEvent(valuesArray, after));
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
