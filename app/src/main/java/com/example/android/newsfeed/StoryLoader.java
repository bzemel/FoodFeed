package com.example.android.newsfeed;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

/**
 * Created by benze on 8/4/2017.
 */

public class StoryLoader extends AsyncTaskLoader<ArrayList<Story>> {

    private String mUrl;

    public StoryLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This method is invoked (or called) on a background thread, so we can perform
     * long-running operations like making a network request.
     * <p>
     * It is NOT okay to update the UI from a background thread, so we just return an
     * {@link ArrayList<Story>} object as the result.
     */
    @Override
    public ArrayList<Story> loadInBackground() {

        // Don't perform the request if there are no URLs, or the first URL is null.
        if (mUrl == null) {
            return null;
        }

        ArrayList<Story> result = QueryUtils.fetchStoryData(mUrl);

        return result;
    }
}