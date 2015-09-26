package com.projects.nosleepproject;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.projects.nosleepproject.data.ListingDbHelper;
import com.projects.nosleepproject.events.ListingLoadedEvent;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private ModelFragment mFrag;
    private ListView mListView;
    private ListViewAdapter mAdapter;
    private List<ContentValues> mValuesArray;
    private String mAfter;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private int mPosition = ListView.INVALID_POSITION;

    private String mCurrentTable;

    private int count = 0;
    private boolean firstRun = true;

    private static final String MFRAG_TAG = "mfrag";
    public static final String LIST_POSITION_TAG = "list_position";
    public static final String CURRENT_TABLE_TAG = "current_table_tag";
    public static final String MAFTER_TAG = "mafter_tag";
    public static final String COUNT_TAG = "count_tag";

    public ProgressBar loadingPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    public void setupDrawerContent(NavigationView nView) {
        nView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.nav_year_one:
                        resetList();
                        mCurrentTable = ListingDbHelper.TABLE_NAME_YEAR_ONE;
                        mFrag.getListings(ListingDbHelper.UNIX_YEAR_ONE, "", mValuesArray, count,
                                ListingDbHelper.TABLE_NAME_YEAR_ONE);
                        mDrawer.closeDrawers();
                        break;
                    case R.id.nav_year_two:
                        resetList();
                        mCurrentTable = ListingDbHelper.TABLE_NAME_YEAR_TWO;
                        mFrag.getListings(ListingDbHelper.UNIX_YEAR_TWO, "", mValuesArray, count,
                                ListingDbHelper.TABLE_NAME_YEAR_TWO);
                        mDrawer.closeDrawers();
                        break;
                    case R.id.nav_year_three:
                        resetList();
                        mCurrentTable = ListingDbHelper.TABLE_NAME_YEAR_THREE;
                        mFrag.getListings(ListingDbHelper.UNIX_YEAR_THREE, "", mValuesArray, count,
                                ListingDbHelper.TABLE_NAME_YEAR_THREE);
                        mDrawer.closeDrawers();
                        break;
                    case R.id.nav_year_four:
                        resetList();
                        mCurrentTable = ListingDbHelper.TABLE_NAME_YEAR_FOUR;
                        mFrag.getListings(ListingDbHelper.UNIX_YEAR_FOUR, "", mValuesArray, count,
                                ListingDbHelper.TABLE_NAME_YEAR_FOUR);
                        mDrawer.closeDrawers();
                        break;
                    case R.id.nav_year_five:
                        resetList();
                        mCurrentTable = ListingDbHelper.TABLE_NAME_YEAR_FIVE;
                        mFrag.getListings(ListingDbHelper.UNIX_YEAR_FIVE, "", mValuesArray, count,
                                ListingDbHelper.TABLE_NAME_YEAR_FIVE);
                        mDrawer.closeDrawers();
                        break;
                    case R.id.nav_year_six:
                        resetList();
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
        mValuesArray = new ArrayList<>();
        mAfter = null;
        firstRun = true;
        count = 0;
        loadingPanel.setVisibility(View.VISIBLE);
    }

    public void getCurrentList(){
        String timestamp = null;
        String table = null;
        switch(mCurrentTable){
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
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().registerSticky(this);
        if(mPosition != ListView.INVALID_POSITION){

            Log.e("onResume", Integer.toString(mPosition));
            mListView.setSelection(mPosition);
        }


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mPosition = savedInstanceState.getInt(LIST_POSITION_TAG);
        mCurrentTable = savedInstanceState.getString(CURRENT_TABLE_TAG);
        count = savedInstanceState.getInt(COUNT_TAG);
        mAfter = savedInstanceState.getString(MAFTER_TAG);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mPosition = mListView.getFirstVisiblePosition();

        outState.putInt(LIST_POSITION_TAG, mPosition);
        outState.putString(CURRENT_TABLE_TAG, mCurrentTable);
        outState.putString(MAFTER_TAG, mAfter);
        outState.putInt(COUNT_TAG, count);
        super.onSaveInstanceState(outState);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(ListingLoadedEvent event) {
//        if(mFrag.scrollLoading)
//            return;
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
            loadingPanel.setVisibility(View.VISIBLE);
            count += 30;
            Log.e("count: ", Integer.toString(count));
            getCurrentList();
        }
//        //Added to fix progressbar showing when end of list is reached
//        if(!mFrag.scrollLoading){
//            loadingPanel.setVisibility(View.GONE);
//        }
    }
}
