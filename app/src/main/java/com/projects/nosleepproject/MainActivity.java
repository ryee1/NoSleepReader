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

package com.projects.nosleepproject;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.projects.nosleepproject.data.ListingDbHelper;
import com.projects.nosleepproject.events.FailedLoadEvent;
import com.projects.nosleepproject.events.ListingLoadedEvent;
import com.projects.nosleepproject.events.QueryListingEvent;

import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener {

    private ModelFragment mFrag;
    private ListView mListView;
    private ListViewAdapter mAdapter;
    private List<ContentValues> mValuesArray;
    private String mAfter;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private int mPosition = ListView.INVALID_POSITION;
    private ListingDbHelper mDbHelper;

    // For onScroll when user is in Get More From Author list
    private String mAuthor;

    private String mCurrentTable;

    private int count = 0;
    private boolean firstRun = true;

    private static final String MFRAG_TAG = "mfrag";
    public static final String LIST_POSITION_TAG = "list_position";
    public static final String CURRENT_TABLE_TAG = "current_table_tag";
    public static final String MAFTER_TAG = "mafter_tag";
    public static final String COUNT_TAG = "count_tag";

    public static final String FRONTPAGE_TAG = "front_page";
    public static final String CURRENT_AUTHOR_TAG = "current_author";

    public static final String LOADING_TOAST_TEXT = "Still Loading...";
    public static final String FAILED_LOAD_TOAST = "Network Error: Failed to Load List";


    public ProgressBar loadingPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = ListingDbHelper.getInstance(this);
        setContentView(R.layout.activity_main);


        if (mFrag == null) {
            getSupportFragmentManager().beginTransaction().add(new ModelFragment(), MFRAG_TAG).commit();
            getSupportFragmentManager().executePendingTransactions();
            Log.e("MainActivity Frag: ", "Ran");
        }
        mFrag = (ModelFragment) getSupportFragmentManager().findFragmentByTag(MFRAG_TAG);

        loadingPanel = (ProgressBar) findViewById(R.id.loading_panel);
        mListView = (ListView) findViewById(R.id.listView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nView = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nView);

        registerForContextMenu(mListView);
    }

    public void onFirstRun() {
        resetList();
        getSupportActionBar().setTitle(R.string.frontpage_title);
        mCurrentTable = FRONTPAGE_TAG;
        mFrag.getFrontPage("", mValuesArray, 0);
    }

    public void setupDrawerContent(NavigationView nView) {
        nView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (mFrag.scrollLoading) {
                    Toast.makeText(getBaseContext(), LOADING_TOAST_TEXT, Toast.LENGTH_SHORT).show();
                    return true;
                }
                switch (id) {
                    case R.id.front_page:
                        getSupportActionBar().setTitle(R.string.frontpage_title);
                        resetList();
                        mCurrentTable = FRONTPAGE_TAG;
                        mFrag.getFrontPage("", mValuesArray, 0);
                        mDrawer.closeDrawers();
                        break;
                    case R.id.favorites:
                        resetList();
                        getSupportActionBar().setTitle(R.string.favorites_title);
                        mCurrentTable = ListingDbHelper.TABLE_NAME_FAVORITES;
                        mFrag.getFavorites(mValuesArray);
                        mDrawer.closeDrawers();
                        break;
                    case R.id.nav_year_one:
                        resetList();
                        getSupportActionBar().setTitle(R.string.best_of_2010_title);
                        mCurrentTable = ListingDbHelper.TABLE_NAME_YEAR_ONE;
                        mFrag.getListings(ListingDbHelper.UNIX_YEAR_ONE, "", mValuesArray, count,
                                ListingDbHelper.TABLE_NAME_YEAR_ONE);
                        mDrawer.closeDrawers();
                        break;
                    case R.id.nav_year_two:
                        resetList();
                        getSupportActionBar().setTitle(R.string.best_of_2011_title);
                        mCurrentTable = ListingDbHelper.TABLE_NAME_YEAR_TWO;
                        mFrag.getListings(ListingDbHelper.UNIX_YEAR_TWO, "", mValuesArray, count,
                                ListingDbHelper.TABLE_NAME_YEAR_TWO);
                        mDrawer.closeDrawers();

                        break;
                    case R.id.nav_year_three:
                        resetList();
                        getSupportActionBar().setTitle(R.string.best_of_2012_title);
                        mCurrentTable = ListingDbHelper.TABLE_NAME_YEAR_THREE;
                        mFrag.getListings(ListingDbHelper.UNIX_YEAR_THREE, "", mValuesArray, count,
                                ListingDbHelper.TABLE_NAME_YEAR_THREE);
                        mDrawer.closeDrawers();
                        break;
                    case R.id.nav_year_four:
                        resetList();
                        getSupportActionBar().setTitle(R.string.best_of_2013_title);
                        mCurrentTable = ListingDbHelper.TABLE_NAME_YEAR_FOUR;
                        mFrag.getListings(ListingDbHelper.UNIX_YEAR_FOUR, "", mValuesArray, count,
                                ListingDbHelper.TABLE_NAME_YEAR_FOUR);
                        mDrawer.closeDrawers();
                        break;
                    case R.id.nav_year_five:
                        resetList();
                        getSupportActionBar().setTitle(R.string.best_of_2014_title);
                        mCurrentTable = ListingDbHelper.TABLE_NAME_YEAR_FIVE;
                        mFrag.getListings(ListingDbHelper.UNIX_YEAR_FIVE, "", mValuesArray, count,
                                ListingDbHelper.TABLE_NAME_YEAR_FIVE);
                        mDrawer.closeDrawers();
                        break;
                    case R.id.nav_year_six:
                        resetList();
                        getSupportActionBar().setTitle(R.string.best_of_2015_title);
                        mCurrentTable = ListingDbHelper.TABLE_NAME_YEAR_SIX;
                        mFrag.getListings(ListingDbHelper.UNIX_YEAR_SIX, "", mValuesArray, count,
                                ListingDbHelper.TABLE_NAME_YEAR_SIX);
                        mDrawer.closeDrawers();
                        break;

                }

                return true;
            }
        });
    }

    public void resetList() {
        if (mValuesArray != null)
            mValuesArray.clear();
        mValuesArray = mFrag.getContentArray();
        mPosition = ListView.INVALID_POSITION;
        mAfter = null;
        firstRun = true;
        count = 0;
        loadingPanel.setVisibility(View.VISIBLE);
    }

    public void getCurrentList() {
        String timestamp = null;
        String table = null;
        switch (mCurrentTable) {
            case ListingDbHelper.TABLE_NAME_FAVORITES:
                ;
                return;
            case CURRENT_AUTHOR_TAG:
                mFrag.getAuthor(mAfter, mValuesArray, count, mAuthor);
                return;
            case FRONTPAGE_TAG:
                mFrag.getFrontPage(mAfter, mValuesArray, count);
            case ListingDbHelper.TABLE_NAME_YEAR_ONE:
                table = ListingDbHelper.TABLE_NAME_YEAR_ONE;
                timestamp = ListingDbHelper.UNIX_YEAR_ONE;
                break;
            case ListingDbHelper.TABLE_NAME_YEAR_TWO:
                table = ListingDbHelper.TABLE_NAME_YEAR_TWO;
                timestamp = ListingDbHelper.UNIX_YEAR_TWO;
                break;
            case ListingDbHelper.TABLE_NAME_YEAR_THREE:
                table = ListingDbHelper.TABLE_NAME_YEAR_THREE;
                timestamp = ListingDbHelper.UNIX_YEAR_THREE;
                break;
            case ListingDbHelper.TABLE_NAME_YEAR_FOUR:
                table = ListingDbHelper.TABLE_NAME_YEAR_FOUR;
                timestamp = ListingDbHelper.UNIX_YEAR_FOUR;
                break;
            case ListingDbHelper.TABLE_NAME_YEAR_FIVE:
                table = ListingDbHelper.TABLE_NAME_YEAR_FIVE;
                timestamp = ListingDbHelper.UNIX_YEAR_FIVE;
                break;
            case ListingDbHelper.TABLE_NAME_YEAR_SIX:
                table = ListingDbHelper.TABLE_NAME_YEAR_SIX;
                timestamp = ListingDbHelper.UNIX_YEAR_SIX;
                break;
        }
        mFrag.getListings(timestamp, mAfter, mValuesArray, count, table);
    }

    @Override
    protected void onPause() {
        if (firstRun != true) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(LIST_POSITION_TAG, mPosition);
            editor.putString(CURRENT_TABLE_TAG, mCurrentTable);
            editor.putString(MAFTER_TAG, mAfter);
            editor.putInt(COUNT_TAG, count);
            editor.commit();
        }
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().registerSticky(this);
        if (mPosition != ListView.INVALID_POSITION) {

            Log.e("onResume", Integer.toString(mPosition));
            mListView.setSelection(mPosition);
        }
        if (firstRun != true) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            mPosition = sharedPref.getInt(LIST_POSITION_TAG, mPosition);
            mCurrentTable = sharedPref.getString(CURRENT_TABLE_TAG, mCurrentTable);
            mAfter = sharedPref.getString(MAFTER_TAG, mAfter);
            count = sharedPref.getInt(COUNT_TAG, count);
        } else {
            onFirstRun();
        }

    }


    @SuppressWarnings("unused")
    public void onEventMainThread(ListingLoadedEvent event) {
        mValuesArray = event.getValues();
        mAfter = event.getAfter();
        if (firstRun) {
            mAdapter = new ListViewAdapter(this, mValuesArray);
            mListView.setAdapter(mAdapter);
            count = 0;
            mListView.setOnItemClickListener(this);
            mListView.setOnScrollListener(this);
            loadingPanel.setVisibility(View.GONE);
            firstRun = false;
        } else {
            mAdapter.notifyDataSetChanged();
            loadingPanel.setVisibility(View.GONE);
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(QueryListingEvent event) {
        mValuesArray = event.getValuesArray();
        mAdapter = new ListViewAdapter(this, mValuesArray);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        Log.e("mPosition: ", Integer.toString(mPosition));
        if (mPosition != ListView.INVALID_POSITION)
            mListView.setSelection(mPosition);
        loadingPanel.setVisibility(View.GONE);
        firstRun = false;
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(FailedLoadEvent event) {
        Toast.makeText(this, FAILED_LOAD_TOAST, Toast.LENGTH_SHORT).show();
        resetList();
        mValuesArray = event.getValues();
        mAdapter = new ListViewAdapter(this, mValuesArray);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        Log.e("mPosition: ", Integer.toString(mPosition));
        if (mPosition != ListView.INVALID_POSITION)
            mListView.setSelection(mPosition);
        loadingPanel.setVisibility(View.GONE);
        firstRun = false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            if (mValuesArray.get(i).getAsString(ListingDbHelper.COLUMN_URL).equals("")) {
                return;
            }
            String url = mValuesArray.get(i).getAsString(ListingDbHelper.COLUMN_URL);
            Intent intent = new Intent(this, ReaderActivity.class);
            intent.putExtra(ReaderActivity.READER_URL_KEY, url);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("MainActivity: ", "OnItemClick out of bound");
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount
            , int totalItemCount) {

        int lastInScreen = firstVisibleItem + visibleItemCount;
        if (lastInScreen == totalItemCount && !mFrag.scrollLoading && mAfter != null) {
            if (mCurrentTable.equals(CURRENT_AUTHOR_TAG)) {
                loadingPanel.setVisibility(View.VISIBLE);
                count += 25;
                getCurrentList();
            } else if (!mCurrentTable.equals(ListingDbHelper.TABLE_NAME_FAVORITES)) {
                loadingPanel.setVisibility(View.VISIBLE);
                count += 25;
                getCurrentList();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu_main, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(mValuesArray.get(info.position).getAsString(ListingDbHelper.COLUMN_TITLE));
        MenuItem favorites = menu.findItem(R.id.context_menu_favorite);
        if (mCurrentTable.equals(ListingDbHelper.TABLE_NAME_FAVORITES)) {
            favorites.setTitle("Remove from Favorites");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (mFrag.scrollLoading) {
            Toast.makeText(this, LOADING_TOAST_TEXT, Toast.LENGTH_SHORT);
            return true;
        }
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = item.getItemId();
        switch (id) {
            case R.id.copy_url:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", mValuesArray.get(info.position)
                        .getAsString(ListingDbHelper.COLUMN_URL));
                clipboard.setPrimaryClip(clip);
                break;
            case R.id.context_menu_favorite:
                if (mCurrentTable.equals(ListingDbHelper.TABLE_NAME_FAVORITES)) {
                    mPosition = (info.position - 1 >= 0) ? info.position - 1 : 0;
                    mDbHelper.deleteFavoites(mValuesArray.get(info.position).getAsString(ListingDbHelper.COLUMN_ID));
                    mFrag.getFavorites(mValuesArray);
                } else
                    mDbHelper.insertFavorites(mValuesArray.get(info.position));
                break;
            case R.id.context_menu_author:
                firstRun = true;
                mCurrentTable = CURRENT_AUTHOR_TAG;
                mAuthor = "author:" + mValuesArray.get(info.position).getAsString(ListingDbHelper.COLUMN_AUTHOR);
                resetList();
                Log.e("contextauthor", "mafter: " + mAfter + "count: " + count + "author: " + mAuthor);
                mFrag.getAuthor(mAfter, mValuesArray, count, mAuthor);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear().commit();
        resetList();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
            return;
        }
        super.onBackPressed();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear().commit();
        resetList();
    }
}
