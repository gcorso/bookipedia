package com.gcappslab.bookipedia.Library.Browse;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gcappslab.bookipedia.Library.Databases.LoadBooks;
import com.gcappslab.bookipedia.Library.Library.LibraryActivity;
import com.gcappslab.bookipedia.Library.Objects.Book;
import com.gcappslab.bookipedia.Library.Objects.BottomBarManager;
import com.gcappslab.bookipedia.Library.Objects.Constants;
import com.gcappslab.bookipedia.R;

import java.util.List;

public class GenresActivity extends AppCompatActivity {

    private GridView gridView;
    private BookTableGridBaseAdapter adapter;
    private String genre;
    int sort;

    public final static int ORDER_RATING = 0;
    public final static int ORDER_POPULAR = 1;
    public final static int ORDER_NAME = 2;
    public final static int ORDER_AUTHOR = 3;
    public final static int ORDER_YEAR = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);

        Intent intent = getIntent();
        genre = intent.getStringExtra("genre");

        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_titletext_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText(genre);
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);

        BottomBarManager.setBar(GenresActivity.this);
        gridView = (GridView) findViewById(R.id.bookTable);

        //get preferences for order
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        sort = sharedPref.getInt(Constants.prefBackendOrder, 0);

        showBooks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_library, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort) {

            final Dialog dialog = new Dialog(GenresActivity.this);
            dialog.setContentView(R.layout.sort_dialog_backend);

            RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupSort);

            if (sort == ORDER_RATING){
                radioGroup.check(R.id.radioRating);
            } else if (sort == ORDER_POPULAR){
                radioGroup.check(R.id.radioPopular);
            } else if (sort == ORDER_NAME){
                radioGroup.check(R.id.radioTitle);
            } else if (sort == ORDER_AUTHOR){
                radioGroup.check(R.id.radioAuthor);
            } else if (sort == ORDER_YEAR){
                radioGroup.check(R.id.radioYear);
            }

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    if (checkedId == R.id.radioRating){
                        sort = ORDER_RATING;
                    } else if (checkedId == R.id.radioPopular){
                        sort = ORDER_POPULAR;
                    } else if (checkedId == R.id.radioTitle){
                        sort = ORDER_NAME;
                    } else if (checkedId == R.id.radioAuthor){
                        sort = ORDER_AUTHOR;
                    } else if (checkedId == R.id.radioYear){
                        sort = ORDER_YEAR;
                    }
                    showBooks();
                    dialog.dismiss();
                }
            });
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // sets up and launches the query to retrieve the books
    private void showBooks(){
        String whereBook = " WHERE genre LIKE '%" + genre + "%'  AND file IS NOT NULL ORDER BY rating*LN(reviews+1)*(RAND()+2) DESC  LIMIT 100";

        if (sort == ORDER_POPULAR){
            whereBook = " WHERE genre LIKE '%" + genre + "%'  AND file IS NOT NULL ORDER BY downloads*(RAND()+2) DESC  LIMIT 100";
        } else if (sort == ORDER_NAME){
            whereBook = " WHERE genre LIKE '%" + genre + "%'  AND file IS NOT NULL ORDER BY name  LIMIT 100";
        } else if (sort == ORDER_AUTHOR){
            whereBook = " WHERE genre LIKE '%" + genre + "%'  AND file IS NOT NULL ORDER BY authorname  LIMIT 100";
        } else if (sort == ORDER_YEAR){
            whereBook = " WHERE genre LIKE '%" + genre + "%'  AND file IS NOT NULL ORDER BY year DESC  LIMIT 100";
        }
        new LoadBooks(whereBook, new LoadBooks.LoadBooksAsyncResponse() {
            @Override
            public void processFinish(List<Book> output, boolean connection) {
                if (connection){
                    adapter = new BookTableGridBaseAdapter(GenresActivity.this, output);
                    gridView.setAdapter(adapter);
                } else {
                    Intent intent = new Intent(GenresActivity.this, LibraryActivity.class);
                    Toast.makeText(GenresActivity.this, "Internet connection not available. Please check your connection.", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
            }
        }).execute();
    }
}

