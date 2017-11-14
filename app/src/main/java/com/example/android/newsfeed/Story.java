package com.example.android.newsfeed;

/**
 * Created by benze on 8/4/2017.
 */

public class Story {
    //String title
    private String mTitle;

    //String content
    private String mContent;

    //String url
    private String mUrl;

    //String publication date
    private String mDate;

    //String section
    private String mSection;

    /**
     * @param title   is the title of the story
     * @param content is the actual story content
     * @param url     is the link to the story
     * @param date    is the date that the story was published
     * @param section is the section that the story is in
     */
    public Story(String title, String content, String url, String date, String section) {
        mTitle = title;
        mContent = content;
        mUrl = url;
        mDate = date;
        mSection = section;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getContent() {
        return mContent;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getDate() {
        return mDate;
    }

    public String getSection() {
        return mSection;
    }
}
