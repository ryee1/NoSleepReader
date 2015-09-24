package com.projects.nosleepproject;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.projects.nosleepproject.data.ListingDbHelper;
import com.projects.nosleepproject.events.ListingLoadedEvent;

import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ModelFragment mFrag;
    private ListView mListView;
    private ListViewAdapter mAdapter;
    private List<ContentValues> mValuesArray;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;

    private boolean firstRun;
    private static String MFRAG_TAG = "mfrag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(mFrag == null){
            getSupportFragmentManager().beginTransaction().add(new ModelFragment(), MFRAG_TAG).commit();
        }

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
        if(!firstRun){
            mAdapter = new ListViewAdapter(this, mValuesArray);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(this);
            firstRun = true;
        }
        else {
            mAdapter.notifyDataSetChanged();
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
        String url = mValuesArray.get(i).getAsString(ListingDbHelper.COLUMN_URL);
        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra(ReaderActivity.READER_URL_KEY, url);
        startActivity(intent);
    }
}
