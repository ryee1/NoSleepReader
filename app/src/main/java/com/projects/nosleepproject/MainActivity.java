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

import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener{

    private ModelFragment mFrag;
    private ListView mListView;
    private ListViewAdapter mAdapter;
    private List<ContentValues> mValuesArray;
    private String mAfter;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;

    private int count = 0;
    private boolean firstRun;
    private static String MFRAG_TAG = "mfrag";

    public ProgressBar loadingPanel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(mFrag == null){
            getSupportFragmentManager().beginTransaction().add(new ModelFragment(), MFRAG_TAG).commit();
        }

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

    public void setupDrawerContent(NavigationView nView){
        nView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                switch(id){

                }

                return true;
            }
        });
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
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(ListingLoadedEvent event){
        mValuesArray = event.getValues();
        mAfter = event.getAfter();
        if(!firstRun){
            mAdapter = new ListViewAdapter(this, mValuesArray);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(this);
            mListView.setOnScrollListener(this);
            loadingPanel.setVisibility(View.GONE);
            firstRun = true;
        }
        else {
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
        }
        catch (Exception e){
            Log.e("MainActivity: ", "OnItemClick out of bound");
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount
            , int totalItemCount) {
        mFrag = (ModelFragment) getSupportFragmentManager().findFragmentByTag(MFRAG_TAG);

        int lastInScreen = firstVisibleItem + visibleItemCount;
        Log.e("After: ", mAfter);
        if(lastInScreen == totalItemCount && !mFrag.scrollLoading){
            loadingPanel.setVisibility(View.VISIBLE);
            count += 30;
            Log.e("count: ", Integer.toString(count));
            mFrag.getListings("timestamp:338166428..1348009628", mAfter, mValuesArray, count);
        }
    }
}
