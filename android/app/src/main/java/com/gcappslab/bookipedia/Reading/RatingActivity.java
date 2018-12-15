package com.gcappslab.bookipedia.Reading;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gcappslab.bookipedia.Library.Databases.BooksLDH;
import com.gcappslab.bookipedia.Library.Databases.JSONParser;
import com.gcappslab.bookipedia.Library.Databases.LoadBooks;
import com.gcappslab.bookipedia.Library.Home.BookHorizGridAdapter;
import com.gcappslab.bookipedia.Library.Library.LibraryActivity;
import com.gcappslab.bookipedia.Library.Objects.Book;
import com.gcappslab.bookipedia.Library.Objects.BottomBarManager;
import com.gcappslab.bookipedia.Library.Objects.User;
import com.gcappslab.bookipedia.Login.UserFileManager;
import com.gcappslab.bookipedia.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_BOOKID;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_RATING;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_SUCCESS;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_USERID;
import static com.gcappslab.bookipedia.Library.Objects.Constants.URL_API;

public class RatingActivity extends AppCompatActivity {
    private TextView tvAuthorRating;
    private RatingBar ratingBar;
    private Book book;
    private int id;
    private BooksLDH booksLDH;
    private HorizontalGridView gridBooks;
    private BookHorizGridAdapter adapterBooks;
    private float rating;

    private JSONParser jsonParser = new JSONParser();
    private static String url_add_rating = URL_API+"add_rating.php";
    private boolean connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        Intent intent = getIntent();
        id = intent.getIntExtra("bookId", 0);
        booksLDH = new BooksLDH(this);
        Pair<Book, Boolean> pair  = booksLDH.getBookDescriptionById(id);
        book = pair.first;

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        tvAuthorRating = (TextView) findViewById(R.id.tvRatingAuthor);
        String author = tvAuthorRating.getText().toString() + book.getAuthorName();
        tvAuthorRating.setText(author);

        Log.i("rev", Integer.toString(book.getReviews()));

        if (book.getReviews() == 1000000){
            ratingBar.setRating(book.getRating());
        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float r, boolean fromUser) {
                if(rating<1.0f) {
                    ratingBar.setRating(1.0f);
                    r = 1.0f;
                }
                rating = r;
                new AddRating().execute();
            }
        });

        // Populating Books
        String whereBooks = "WHERE authorid = " + Integer.toString(book.getAuthorId()) +
                " AND bookid <> " + Integer.toString(id) + " AND file IS NOT NULL ORDER BY rating*LN(reviews+1)*(RAND()+2) DESC  LIMIT 30";
        gridBooks = (HorizontalGridView) findViewById(R.id.gridBooks);
        new LoadBooks(whereBooks, new LoadBooks.LoadBooksAsyncResponse() {
            @Override
            public void processFinish(List<Book> output, boolean connection) {

                if (connection){
                    adapterBooks = new BookHorizGridAdapter(RatingActivity.this, output, false);
                    gridBooks.setAdapter(adapterBooks);
                } else {
                    Intent intent = new Intent(RatingActivity.this, LibraryActivity.class);
                    startActivity(intent);
                }
            }
        }).execute();

        BottomBarManager.setBar(RatingActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(RatingActivity.this, ReadingActivity.class);
                intent.putExtra("bookId", book.getBookId());
                startActivity(intent);
                break;
        }
        return true;
    }


    /**
     * Background Async Task to set rating in the server database
     * */
    private class AddRating extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            User user = UserFileManager.getInstance(RatingActivity.this).getUser();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_BOOKID, Integer.toString(id)));
            params.add(new BasicNameValuePair(TAG_USERID, Integer.toString(user.getUserId())));
            params.add(new BasicNameValuePair(TAG_RATING, Float.toString(rating)));

            JSONObject json = jsonParser.makeHttpRequest(url_add_rating, "POST", params);
            if(json==null){
                connection = false;
                return null;
            }

            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    connection = true;
                } else {
                    connection = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {

            booksLDH.updateRating(id, rating);
            if(!connection){
                Toast.makeText(RatingActivity.this, "Internet connection not available. Please check your connection.", Toast.LENGTH_LONG).show();

            }
        }
    }
}
