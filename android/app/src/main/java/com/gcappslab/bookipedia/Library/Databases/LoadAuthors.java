package com.gcappslab.bookipedia.Library.Databases;

import android.os.AsyncTask;

import com.gcappslab.bookipedia.Library.Objects.Author;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_AUTHORID;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_AUTHORS;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_NAME;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_SUCCESS;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_WHERE;
import static com.gcappslab.bookipedia.Library.Objects.Constants.URL_API;

/**
 * Background Async Task to load authors by making HTTP Request
 * */
public class LoadAuthors extends AsyncTask<String, String, String> {

    private static String url_all_authors = URL_API+"query_authors.php";
    private JSONParser jParser = new JSONParser();
    private List<Author> listAuthors = new ArrayList<>();
    private String where;
    private boolean conn;

    public interface LoadAuthorsAsyncResponse {
        void processFinish(List<Author> output, boolean connection);
    }

    private LoadAuthorsAsyncResponse delegate = null;

    public LoadAuthors(String where, LoadAuthorsAsyncResponse delegate){
        this.where = where;
        this.delegate = delegate;
    }

    protected String doInBackground(String... args) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(TAG_WHERE, where));

        JSONObject json = jParser.makeHttpRequest(url_all_authors, "GET", params);

        if(json == null){
            conn = false;
            return null;
        }
        conn = true;

        //Log.d("All authors: ", json.toString());

        try {
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
                JSONArray jsonAuthors = json.getJSONArray(TAG_AUTHORS);
                for (int i = 0; i < jsonAuthors.length(); i++) {
                    JSONObject c = jsonAuthors.getJSONObject(i);
                    int id = c.getInt(TAG_AUTHORID);
                    String name = c.getString(TAG_NAME);
                    Author author = new Author(id, name);
                    listAuthors.add(author);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(String file_url) {
        // comunicating that the background process has finished and returning the list of authors to the activity
        delegate.processFinish(listAuthors, conn);
    }
}
