package com.gcappslab.bookipedia.Library.Browse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gcappslab.bookipedia.Library.Databases.LoadBooks;
import com.gcappslab.bookipedia.Library.Home.BookHorizGridAdapter;
import com.gcappslab.bookipedia.Library.Library.LibraryActivity;
import com.gcappslab.bookipedia.Library.Objects.Book;
import com.gcappslab.bookipedia.Library.Objects.BottomBarManager;
import com.gcappslab.bookipedia.R;
import com.gcappslab.bookipedia.Settings.HelpAndFeedbackActivity;
import com.gcappslab.bookipedia.Settings.SuggestionActivity;

import java.util.List;

public class BrowseActivity extends AppCompatActivity {

    private HorizontalGridView gridPopular;
    private BookHorizGridAdapter adapterPopular;
    private static final String wherePopular = " WHERE file IS NOT NULL ORDER BY downloads*(RAND()+2) DESC  LIMIT 20";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_titletext_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Browse");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);

        BottomBarManager.setBar(BrowseActivity.this);

        GenresTableGridAdapter adapterGenres = new GenresTableGridAdapter(this);
        ExpandableHeightGridView gridThemes = (ExpandableHeightGridView) findViewById(R.id.gridGenres);
        gridThemes.setAdapter(adapterGenres);
        gridThemes.setExpanded(true);

        // Populating Popular Books
        gridPopular = (HorizontalGridView) findViewById(R.id.gridPopular);
        new LoadBooks(wherePopular, new LoadBooks.LoadBooksAsyncResponse() {
            @Override
            public void processFinish(List<Book> output, boolean connection) {
                if (connection){
                    adapterPopular = new BookHorizGridAdapter(BrowseActivity.this, output, false);
                    gridPopular.setAdapter(adapterPopular);
                } else {
                    Intent intent = new Intent(BrowseActivity.this, LibraryActivity.class);
                    Toast.makeText(BrowseActivity.this, "Internet connection not available. Please check your connection.", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
            }
        }).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_browse, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_help_and_feedback) {
            Intent intent = new Intent(BrowseActivity.this, HelpAndFeedbackActivity.class);
            startActivity(intent);

        } else if (id == R.id.action_suggestion) {
            Intent intent = new Intent(BrowseActivity.this, SuggestionActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
