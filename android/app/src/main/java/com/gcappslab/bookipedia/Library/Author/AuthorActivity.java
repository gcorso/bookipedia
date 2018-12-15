package com.gcappslab.bookipedia.Library.Author;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.gcappslab.bookipedia.Library.Databases.JSONParser;
import com.gcappslab.bookipedia.Library.Databases.LoadBooks;
import com.gcappslab.bookipedia.Library.Home.BookHorizGridAdapter;
import com.gcappslab.bookipedia.Library.Library.LibraryActivity;
import com.gcappslab.bookipedia.Library.Objects.Author;
import com.gcappslab.bookipedia.Library.Objects.Book;
import com.gcappslab.bookipedia.Library.Objects.BottomBarManager;
import com.gcappslab.bookipedia.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_AUTHOR;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_AUTHORID;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_BIOGRAPHY;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_NAME;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_SUCCESS;
import static com.gcappslab.bookipedia.Library.Objects.Constants.URL_API;

public class AuthorActivity extends AppCompatActivity {

    private TextView tvAuthorName;
    private TextView tvBiography;
    private HorizontalGridView gridBooks;
    private BookHorizGridAdapter adapterBooks;

    private Author author;
    private int id;

    private ProgressDialog pDialog;
    private JSONParser jsonParser = new JSONParser();
    private static final String url_author_details = URL_API+"get_author_details.php";
    private boolean connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

        // get authorid from previous activity
        Intent intent = getIntent();
        id = intent.getIntExtra("authorId", 0);

        tvAuthorName = (TextView) findViewById(R.id.tvAuthorName);
        tvBiography = (TextView) findViewById(R.id.tvBiography);

        // Populating author details
        new GetAuthorDetails().execute();

        // Populating Books
        String whereBooks = "WHERE authorid = " + Integer.toString(id) + " AND file IS NOT NULL ORDER BY rating*LN(reviews+1)*(RAND()+2) DESC  LIMIT 30";
        gridBooks = (HorizontalGridView) findViewById(R.id.gridBooks);
        new LoadBooks(whereBooks, new LoadBooks.LoadBooksAsyncResponse() {
            @Override
            public void processFinish(List<Book> output, boolean connection) {
                if (connection){
                    adapterBooks = new BookHorizGridAdapter(AuthorActivity.this, output, false);
                    gridBooks.setAdapter(adapterBooks);
                } else {
                    Intent intent = new Intent(AuthorActivity.this, LibraryActivity.class);
                    Toast.makeText(AuthorActivity.this, "Internet connection not available. Please check your connection.", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
            }
        }).execute();

        // setting up action bar (top) and bottom bar
        ActionBar myActionBar = getSupportActionBar();
        if (myActionBar != null) {
            myActionBar.hide();
        }
        BottomBarManager.setBar(AuthorActivity.this);
    }

    // sets the details in the page
    private void setUp(){
        tvAuthorName.setText(author.getAuthorName());
        tvBiography.setText(author.getBiography());
    }

    /**
     * Background Async Task to get complete author details
     * */
    private class GetAuthorDetails extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AuthorActivity.this);
            pDialog.setMessage("Loading author details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            Log.i("Retrieve id", String.valueOf(id));
        }

        protected String doInBackground(String... params) {
            int success;
            try {
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair(TAG_AUTHORID, Integer.toString(id)));
                JSONObject json = jsonParser.makeHttpRequest(url_author_details, "GET", param);

                if(json==null) {
                    connection = false;
                    return null;
                }
                connection = true;

                //Log.d("Single Author Details", json.toString());

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    JSONArray productObj = json.getJSONArray(TAG_AUTHOR);
                    JSONObject jauthor = productObj.getJSONObject(0);

                    String name = jauthor.getString(TAG_NAME);
                    String biography = jauthor.getString(TAG_BIOGRAPHY);
                    author = new Author(id, name, biography );
                } else {
                    connection = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (connection){
                setUp();
            } else {
                Toast.makeText(AuthorActivity.this, "Internet connection not available. Please check your connection.", Toast.LENGTH_LONG).show();
                AuthorActivity.this.finish();
            }
        }
    }
}
