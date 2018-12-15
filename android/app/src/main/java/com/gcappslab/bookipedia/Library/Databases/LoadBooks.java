package com.gcappslab.bookipedia.Library.Databases;

import android.os.AsyncTask;
import android.util.Log;

import com.gcappslab.bookipedia.Library.Objects.Book;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_AUTHORNAME;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_BOOKID;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_BOOKS;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_NAME;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_SUCCESS;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_WHERE;
import static com.gcappslab.bookipedia.Library.Objects.Constants.URL_API;

/**
 * Background Async Task to load books by making HTTP Request
 * */
public class LoadBooks extends AsyncTask<String, String, String> {

    private static String url_all_books = URL_API+"query_books.php";
    private JSONParser jParser = new JSONParser();
    private List<Book> listBooks = new ArrayList<>();
    private String where;
    private boolean connection;

    public interface LoadBooksAsyncResponse {
        void processFinish(List<Book> output, boolean conn);
    }

    public LoadBooksAsyncResponse delegate = null;


    public LoadBooks(String where, LoadBooksAsyncResponse delegate){
        this.where = where;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected String doInBackground(String... args) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(TAG_WHERE, where));
        JSONObject json = jParser.makeHttpRequest(url_all_books, "GET", params);

        if (json==null){
            connection = false;
            return null;
        }

        connection = true;

        Log.d("All books: ", json.toString());

        try {
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
                JSONArray jsonBooks = json.getJSONArray(TAG_BOOKS);
                for (int i = 0; i < jsonBooks.length(); i++) {
                    JSONObject c = jsonBooks.getJSONObject(i);
                    int id = c.getInt(TAG_BOOKID);
                    String name = c.getString(TAG_NAME);
                    String authorname = c.getString(TAG_AUTHORNAME);
                    Book book = new Book(id, name, authorname);
                    listBooks.add(book);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(String file_url) {
        // comunicating that the background process has finished and returning the list of books to the activity
        delegate.processFinish(listBooks, connection);
    }

}