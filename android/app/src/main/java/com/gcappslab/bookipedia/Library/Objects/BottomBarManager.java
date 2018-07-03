package com.gcappslab.bookipedia.Library.Objects;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.gcappslab.bookipedia.Library.Browse.BrowseActivity;
import com.gcappslab.bookipedia.Library.Home.HomeActivity;
import com.gcappslab.bookipedia.Library.Library.LibraryActivity;
import com.gcappslab.bookipedia.Library.Search.SearchActivity;
import com.gcappslab.bookipedia.R;

/**
 * Sets up the actions of the bottomBar in any activity that calls setBar
 */

public class BottomBarManager {

    public static void setBar(final AppCompatActivity activity) {
        ImageView bbHome = (ImageView) activity.findViewById(R.id.bbHome);
        bbHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.getClass() != HomeActivity.class) {
                    Intent intent = new Intent(activity, HomeActivity.class);
                    activity.startActivity(intent);
                }
            }
        });

        ImageView bbLibrary = (ImageView) activity.findViewById(R.id.bbLibrary);
        bbLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.getClass() != LibraryActivity.class){
                    Intent intent = new Intent(activity, LibraryActivity.class);
                    activity.startActivity(intent);
                }
            }
        });

        ImageView bbBrowse = (ImageView) activity.findViewById(R.id.bbBrowse);
        bbBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.getClass() != BrowseActivity.class) {
                    Intent intent = new Intent(activity, BrowseActivity.class);
                    activity.startActivity(intent);
                }
            }
        });

        ImageView bbSearch = (ImageView) activity.findViewById(R.id.bbSearch);
        bbSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.getClass() != SearchActivity.class) {
                    Intent intent = new Intent(activity, SearchActivity.class);
                    activity.startActivity(intent);
                }
            }
        });
    }


}
