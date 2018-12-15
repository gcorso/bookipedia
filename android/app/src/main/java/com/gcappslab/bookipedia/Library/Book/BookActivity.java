package com.gcappslab.bookipedia.Library.Book;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gcappslab.bookipedia.Library.Author.AuthorActivity;
import com.gcappslab.bookipedia.Library.Databases.BooksLDH;
import com.gcappslab.bookipedia.Library.Databases.JSONParser;
import com.gcappslab.bookipedia.Library.Library.LibraryActivity;
import com.gcappslab.bookipedia.Library.Objects.Book;
import com.gcappslab.bookipedia.Library.Objects.User;
import com.gcappslab.bookipedia.Login.UserFileManager;
import com.gcappslab.bookipedia.R;
import com.gcappslab.bookipedia.Reading.ReadingActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_AUTHORID;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_AUTHORNAME;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_BOOK;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_BOOKID;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_DESCRIPTION;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_FILE;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_GENRE;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_LANGUAGE;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_NAME;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_NOTES;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_PROGRESS;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_RATING;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_REVIEWS;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_SUCCESS;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_USERID;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_YEAR;
import static com.gcappslab.bookipedia.Library.Objects.Constants.URL_API;

public class BookActivity extends AppCompatActivity {
    private int id;
    private Book book;
    private boolean local;

    private BooksLDH booksLDH;

    private ProgressDialog pDialog;
    private JSONParser jsonParser = new JSONParser();
    private boolean connection;

    private static final String url_book_details = URL_API+"get_book_details.php";
    private static final String url_book_file = URL_API+"get_book_file.php";
    private static final String url_create_reading = URL_API+"create_reading.php";

    private static final int LENGTH_MAX = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        Intent intent = getIntent();
        id = intent.getIntExtra("bookId", 0);

        booksLDH = new BooksLDH(this);
        Pair<Book, Boolean> pair = booksLDH.getBookDescriptionById(id);

        if (pair.second){
            local = true;
            book = pair.first;
            setUp();
        } else {
            // Getting complete product details in background thread
            local = false;
            new GetBookDetails().execute();
        }

        ActionBar myActionBar = getSupportActionBar();
        if (myActionBar != null) {
            myActionBar.hide();
        }

        ImageView btExit = (ImageView) findViewById(R.id.btExit);
        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // setting up the details of the author
    private void setUp(){
        TextView tvBookTitle = (TextView) findViewById(R.id.tvBookTitle);
        tvBookTitle.setText(book.getName());

        TextView tvBookAuthor = (TextView) findViewById(R.id.tvBookAuthor);
        String author = "by " + book.getAuthorName() + " (" + Integer.toString(book.getYear()) + ")";
        tvBookAuthor.setText(author);
        tvBookAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookActivity.this, AuthorActivity.class);
                intent.putExtra("authorId", book.getAuthorId());
                startActivity(intent);
            }
        });

        TextView tvBookGenre = (TextView) findViewById(R.id.tvBookGenre);
        tvBookGenre.setText(book.getGenre());

        TextView tvBookDescription = (TextView) findViewById(R.id.tvBookDescription);
        tvBookDescription.setText(book.getDescription());

        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setRating(book.getRating());

        Button btStartReading = (Button) findViewById(R.id.startReadingBt);
        if (local){
            btStartReading.setText("CONTINUE READING");
        }
        btStartReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!local){
                    new GetBookFile().execute();
                } else {
                    Intent intent = new Intent(BookActivity.this, ReadingActivity.class);
                    intent.putExtra("bookId", book.getBookId());
                    startActivity(intent);
                }
            }
        });
    }


    /**
     * Background Async Task to get book details
     * */
    private class GetBookDetails extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BookActivity.this);
            pDialog.setMessage("Loading book details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... params) {
            int success;
            try {
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair(TAG_BOOKID, Integer.toString(id)));
                JSONObject json = jsonParser.makeHttpRequest(url_book_details, "GET", param);

                if(json==null) {
                    connection = false;
                    return null;
                }
                connection = true;

                //Log.d("Single Book Details", json.toString());

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    JSONArray productObj = json.getJSONArray(TAG_BOOK);
                    JSONObject jbook = productObj.getJSONObject(0);

                    String name = jbook.getString(TAG_NAME);
                    int authorid = jbook.getInt(TAG_AUTHORID);
                    String authorname = jbook.getString(TAG_AUTHORNAME);
                    int year = jbook.getInt(TAG_YEAR);
                    String description = jbook.getString(TAG_DESCRIPTION);
                    String genre = jbook.getString(TAG_GENRE);
                    String language = jbook.getString(TAG_LANGUAGE);
                    float rating = (float) jbook.getDouble(TAG_RATING);
                    int reviews = jbook.getInt(TAG_REVIEWS);

                    book = new Book(id, name, authorid, authorname, year, description, genre, language, rating, reviews );
                }else{
                    connection = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            if (connection){
                setUp();
            } else {
                Intent intent = new Intent(BookActivity.this, LibraryActivity.class);
                Toast.makeText(BookActivity.this, "Internet connection not available. Please check your connection.", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }

            pDialog.dismiss();
        }
    }

    /**
     * Background Async Task to get the book file, save the book in the local database and save a reading entry in the server database
     * */
    private class GetBookFile extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BookActivity.this);
            pDialog.setMessage("Downloading book. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            Log.i("Retrieve file id  ", String.valueOf(id));
        }

        protected Boolean doInBackground(String... params) {

            boolean suc = false;
            int success;
            try {
                // Get file field from book table
                List<NameValuePair> parametri = new ArrayList<NameValuePair>();
                parametri.add(new BasicNameValuePair(TAG_BOOKID, Integer.toString(id)));
                JSONObject json = jsonParser.makeHttpRequest(
                        url_book_file, "GET", parametri);

                if(json==null) {
                    connection = false;
                    return null;
                }
                connection = true;

                //Log.d("Single Book pezzo file", json.toString());

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    JSONArray productObj = json
                            .getJSONArray(TAG_BOOK);
                    JSONObject jbook = productObj.getJSONObject(0);

                    String file = jbook.getString(TAG_FILE);
                    book.setFile(file);

                    User user = UserFileManager.getInstance(BookActivity.this).getUser();

                    //Log.d("Creation of reading ", "bookid = " + Integer.toString(id) + "  userid = " + Integer.toString(user.getUserId()));

                    // Save the reading entry in reading table
                    parametri = new ArrayList<NameValuePair>();
                    parametri.add(new BasicNameValuePair(TAG_BOOKID, Integer.toString(id)));
                    parametri.add(new BasicNameValuePair(TAG_USERID, Integer.toString(user.getUserId())));

                    json = jsonParser.makeHttpRequest(url_create_reading, "GET", parametri);

                    //Log.d("Creation of reading ", json.toString());

                    try {
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            productObj = json.getJSONArray(TAG_BOOK);
                            jbook = productObj.getJSONObject(0);
                            book.setNotes(jbook.getString(TAG_NOTES));
                            book.setProgress(jbook.getInt(TAG_PROGRESS));
                            suc = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return suc;
        }

        protected void onPostExecute(Boolean success) {
            if (connection && success){
                booksLDH.saveBook(book);
                pDialog.dismiss();
                Intent intent = new Intent(BookActivity.this, ReadingActivity.class);
                intent.putExtra("bookId", book.getBookId());
                startActivity(intent);
            } else {
                pDialog.dismiss();
                Toast.makeText(BookActivity.this, "Internet connection not available. Please check your connection.", Toast.LENGTH_LONG).show();

            }
        }
    }
}
