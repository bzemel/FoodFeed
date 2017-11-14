package com.example.android.newsfeed;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by benze on 8/4/2017.
 */

public class StoryAdapter extends ArrayAdapter<Story> {

    public StoryAdapter(Activity context, ArrayList<Story> stories) {
        super(context, 0, stories);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int arg0) {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the Story object located at this position in the list
        final Story currentStory = getItem(position);

        // Find the TextView in the list_item.xml layout that corresponds to the title
        // and set the title as text
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_textview);
        titleTextView.setText(currentStory.getTitle());

        // Find the TextView in the list_item.xml layout that corresponds to the content
        // and set the content as text
        TextView contentTextView = (TextView) listItemView.findViewById(R.id.content_textview);
        contentTextView.setText(currentStory.getContent());

        // Find the TextView in the list_item.xml layout that corresponds to the date
        // and set the content as text
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date_textview);
        dateTextView.setText(currentStory.getDate());

        // Find the TextView in the list_item.xml layout that corresponds to the date
        // and set the content as text
        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.section_textview);
        sectionTextView.setText(currentStory.getSection());

        //Set a click listener on listView
        listItemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(currentStory.getUrl()));
                        getContext().startActivity(i);
                    }
                }
        );

        return listItemView;
    }
}