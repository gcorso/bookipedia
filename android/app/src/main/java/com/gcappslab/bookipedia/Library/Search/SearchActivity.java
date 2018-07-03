package com.gcappslab.bookipedia.Library.Search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gcappslab.bookipedia.Library.Databases.LoadAuthors;
import com.gcappslab.bookipedia.Library.Databases.LoadBooks;
import com.gcappslab.bookipedia.Library.Library.LibraryActivity;
import com.gcappslab.bookipedia.Library.Objects.Author;
import com.gcappslab.bookipedia.Library.Objects.Book;
import com.gcappslab.bookipedia.Library.Objects.BottomBarManager;
import com.gcappslab.bookipedia.R;
import com.gcappslab.bookipedia.Settings.SuggestionActivity;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ExpandableHeightListView lvBooksSearched;
    private ExpandableHeightListView lvAuthorsSearched;
    private SearchBookListAdapter booksAdapter;
    private SearchAuthorListAdapter authorsAdapter;
    private LinearLayout llEmptySearch;
    private LinearLayout llNoResults;
    private LinearLayout llItemsSearched;
    private LinearLayout llBooksSearched;
    private LinearLayout llAuthorsSearched;
    private boolean noBooks;
    private boolean noAuthors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final ActionBar abar = getSupportActionBar();
        abar.hide();
        BottomBarManager.setBar(SearchActivity.this);

        noBooks = false;
        noAuthors = false;

        llEmptySearch = (LinearLayout) findViewById(R.id.emptySearchLL);
        llNoResults = (LinearLayout) findViewById(R.id.noResultsLL);
        llItemsSearched = (LinearLayout) findViewById(R.id.itemsSearchedLL);
        llBooksSearched = (LinearLayout) findViewById(R.id.booksSearchedLL);
        llAuthorsSearched = (LinearLayout) findViewById(R.id.authorsSearchedLL);
        llEmptySearch.setVisibility(View.VISIBLE);
        llNoResults.setVisibility(View.GONE);
        llItemsSearched.setVisibility(View.GONE);

        lvBooksSearched = (ExpandableHeightListView) findViewById(R.id.lvBooksSearched);
        lvAuthorsSearched = (ExpandableHeightListView) findViewById(R.id.lvAuthorsSearched);
        EditText etSearch = (EditText) findViewById(R.id.etSearch);

        llNoResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("touch", "in");
                Intent intent = new Intent(SearchActivity.this, SuggestionActivity.class);
                startActivity(intent);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length()==0){
                    llEmptySearch.setVisibility(View.VISIBLE);
                    llNoResults.setVisibility(View.GONE);
                    llItemsSearched.setVisibility(View.GONE);
                } else  {
                    String whereBook = " WHERE name LIKE '%" + s.toString() + "%' AND file IS NOT NULL  ORDER BY rating*LN(reviews+1) DESC  LIMIT 20";

                    new LoadBooks(whereBook, new LoadBooks.LoadBooksAsyncResponse() {
                        @Override
                        public void processFinish(List<Book> output, boolean connection) {
                            if (connection){

                                if (output.size()==0){
                                    noBooks = true;
                                    llBooksSearched.setVisibility(View.GONE);
                                    if(noAuthors){
                                        llEmptySearch.setVisibility(View.GONE);
                                        llNoResults.setVisibility(View.VISIBLE);
                                        llItemsSearched.setVisibility(View.GONE);
                                    }
                                } else {
                                    noBooks = false;
                                    llBooksSearched.setVisibility(View.VISIBLE);
                                    booksAdapter = new SearchBookListAdapter(SearchActivity.this, output);
                                    lvBooksSearched.setAdapter(booksAdapter);
                                    lvBooksSearched.setExpanded(true);
                                    llEmptySearch.setVisibility(View.GONE);
                                    llNoResults.setVisibility(View.GONE);
                                    llItemsSearched.setVisibility(View.VISIBLE);
                                }

                            } else {
                                Intent intent = new Intent(SearchActivity.this, LibraryActivity.class);
                                Toast.makeText(SearchActivity.this, "Internet connection not available. Please check your connection.", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            }
                        }
                    }).execute();


                    String whereAuthor = " WHERE name LIKE '%" + s.toString() + "%'  ORDER BY name  LIMIT 20";

                    new LoadAuthors(whereAuthor, new LoadAuthors.LoadAuthorsAsyncResponse() {
                        @Override
                        public void processFinish(List<Author> output, boolean connection) {

                            if (connection){

                                if (output.size()==0){
                                    noAuthors = true;
                                    llAuthorsSearched.setVisibility(View.GONE);
                                    if(noBooks){
                                        llEmptySearch.setVisibility(View.GONE);
                                        llNoResults.setVisibility(View.VISIBLE);
                                        llItemsSearched.setVisibility(View.GONE);
                                    }
                                } else {
                                    noAuthors = false;
                                    llAuthorsSearched.setVisibility(View.VISIBLE);
                                    authorsAdapter = new SearchAuthorListAdapter(SearchActivity.this, output);
                                    lvAuthorsSearched.setAdapter(authorsAdapter);
                                    lvAuthorsSearched.setExpanded(true);
                                    llEmptySearch.setVisibility(View.GONE);
                                    llNoResults.setVisibility(View.GONE);
                                    llItemsSearched.setVisibility(View.VISIBLE);
                                }

                            } else {
                                Intent intent = new Intent(SearchActivity.this, LibraryActivity.class);
                                Toast.makeText(SearchActivity.this, "Internet connection not available. Please check your connection.", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            }
                        }
                    }).execute();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }
}