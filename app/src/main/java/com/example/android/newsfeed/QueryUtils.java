package com.example.android.newsfeed;

/**
 * Created by benze on 8/4/2017.
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by benze on 7/31/2017.
 */

public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // News API keys
    private static final String API_KEY_RESPONSE = "response";
    private static final String API_KEY_RESULTS = "results";
    private static final String API_KEY_PUBLISHED_DATE = "webPublicationDate";
    private static final String API_KEY_TITLE = "webTitle"; // same key used for news title and author name
    private static final String API_KEY_WEBURL = "webUrl";
    private static final String API_KEY_SECTION = "sectionName";


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query theguardian API and return an {@link Story} object to represent a single story
     */
    public static ArrayList<Story> fetchStoryData(String requestUrl) {

        //logging method
        Log.v("fetchStoryData", "fetching");

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList<Story> stories = extractStories(jsonResponse);

        // Return the {@link Event}
        return stories;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the story JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Story} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Story> extractStories(String response) {

        //logging method
        Log.v("extractStories", "extracting");


        // Create an empty ArrayList that we can start adding stories to
        ArrayList<Story> stories = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {


            JSONArray sampleData = new JSONObject(response).getJSONObject(API_KEY_RESPONSE).optJSONArray(API_KEY_RESULTS);
            JSONObject storyObject;

            String title = "No title found";
            String content = "No content found";
            String date = "No date listed";
            String section = "No section found";
            String url = "https://theguardian.com";

            //Checking to see if the JSONObject is empty
            if (sampleData != null) {

                for (int i = 0; i < sampleData.length(); i++) {
                    //find the properties object in the JSON response
                    storyObject = sampleData.getJSONObject(i);


                    //Checking to see if JSON object has title
                    if (storyObject.has(API_KEY_TITLE)) {
                        title = storyObject.getString(API_KEY_TITLE);
                        title = title.replace("| Comment", "");
                        title = title.replace("| ", "\n");
                    }

                    //Checking to see if JSON object has content
                    if (storyObject.getJSONObject("blocks").optJSONArray("body").getJSONObject(0).has("bodyTextSummary")) {
                        content = storyObject.getJSONObject("blocks").optJSONArray("body").getJSONObject(0).getString("bodyTextSummary");
                    }

                    //Checking to see if JSON object has a web url
                    if (storyObject.has(API_KEY_WEBURL)) {
                        url = storyObject.getString(API_KEY_WEBURL);
                    }

                    //Checking to see if JSON object has a date
                    if (storyObject.has(API_KEY_PUBLISHED_DATE)) {
                        String dateTime = storyObject.getString(API_KEY_PUBLISHED_DATE);
                        String[] split = dateTime.split("T");
                        date = split[0];
                    }

                    //Checking to see if JSON object has a section
                    if (storyObject.has(API_KEY_SECTION)) {
                        section = storyObject.getString(API_KEY_SECTION);
                    }

                    // build up a list of Story objects with the corresponding data.
                    stories.add(new Story(title, content, url, date, section));
                }
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the story JSON results", e);
        }

        // Return the list of stories
        return stories;
    }
}
