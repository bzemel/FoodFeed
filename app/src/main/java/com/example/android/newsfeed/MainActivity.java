package com.example.android.newsfeed;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<Story>> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getSupportLoaderManager().initLoader(1, null, this);

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }

    public String createUrl() {

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Date newDate = new Date(c.getTimeInMillis() - 604800000L);

        String formattedDate = df.format(newDate);

        return "https://content.guardianapis.com/search?q=food&from-date=" + formattedDate + "&show-blocks=all&api-key=441ec9e2-c228-4f12-94bf-cb3612890431";
    }

    public void updateUi(ArrayList<Story> stories) {

        // Find a reference to the {@link ListView} in the layout
        ListView storyListView = (ListView) findViewById(R.id.list);

        // Create an {@link StoryAdapter}, whose data source is a list of
        // {@link story}s. The adapter knows how to create list item views for each item
        // in the list.
        StoryAdapter storyAdapter = new StoryAdapter(this, stories);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        storyListView.setAdapter(storyAdapter);
    }

    @Override
    public Loader<ArrayList<Story>> onCreateLoader(int i, Bundle bundle) {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            return new StoryLoader(MainActivity.this, createUrl());
        } else {

            //Hide the progress bar
            ProgressBar spinner = (ProgressBar) findViewById(R.id.loading_spinner);
            spinner.setVisibility(View.GONE);

            // Find a reference to the {@link ListView} in the layout and set it to empty state
            ListView storyListView = (ListView) findViewById(R.id.list);
            TextView emptyStateTextView = (TextView) findViewById(R.id.empty_view);
            storyListView.setEmptyView(emptyStateTextView);
            emptyStateTextView.setText(R.string.no_connection);
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Story>> loader, ArrayList<Story> stories) {
        //Hide the progress bar
        ProgressBar spinner = (ProgressBar) findViewById(R.id.loading_spinner);
        spinner.setVisibility(View.GONE);

        // If there is no result, do nothing.
        if (stories == null) {
            return;
            //If the stories ArrayList is empty, show the no stories found test
        } else if (stories.isEmpty()) {
            // Find a reference to the {@link ListView} in the layout and set it to empty state
            ListView storyListView = (ListView) findViewById(R.id.list);
            TextView emptyStateTextView = (TextView) findViewById(R.id.empty_view);
            storyListView.setEmptyView(emptyStateTextView);
            emptyStateTextView.setText(R.string.no_data_available);
            return;
        }

        updateUi(stories);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Story>> loader) {
        updateUi(new ArrayList<Story>());
    }

}
