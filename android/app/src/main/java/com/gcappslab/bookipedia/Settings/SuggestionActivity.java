package com.gcappslab.bookipedia.Settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gcappslab.bookipedia.Library.Databases.JSONParser;
import com.gcappslab.bookipedia.Library.Home.HomeActivity;
import com.gcappslab.bookipedia.Library.Objects.User;
import com.gcappslab.bookipedia.Login.UserFileManager;
import com.gcappslab.bookipedia.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_SUCCESS;
import static com.gcappslab.bookipedia.Library.Objects.Constants.URL_API;

public class SuggestionActivity extends AppCompatActivity {

    private EditText bookField;
    private EditText authorField;
    private CheckBox cbSendEmail;
    private String book;
    private String author;
    private String email;
    private boolean sent;
    private User user;

    private ProgressDialog pDialog;
    private JSONParser jsonParser = new JSONParser();
    private static String url_create_suggestion = URL_API+"create_suggestion.php";
    private boolean connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_titletext_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Suggest a Book");
        if (abar != null) {
            abar.setCustomView(viewActionBar, params);
            abar.setDisplayShowCustomEnabled(true);
            abar.setDisplayShowTitleEnabled(false);
        }

        user = UserFileManager.getInstance(SuggestionActivity.this).getUser();
        bookField = (EditText) findViewById(R.id.nameField);
        authorField = (EditText) findViewById(R.id.authorField);
        cbSendEmail = (CheckBox) findViewById(R.id.cbSendEmail);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_suggestion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_send) {

            if (bookField.getText().length()>0){
                book = bookField.getText().toString();
                author = authorField.getText().toString();
                if(cbSendEmail.isChecked()){
                    email = user.getEmail();
                } else {
                    email = "";
                }
                new SendSuggestion().execute();
            } else {
                Toast.makeText(SuggestionActivity.this, "Please insert the name of the book.", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Background Async Task to send suggestion to the server database
     * */
    private class SendSuggestion extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SuggestionActivity.this);
            pDialog.setMessage("Sending Suggestion..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            sent = false;

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userid", Integer.toString(user.getUserId())));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("book", book));
            params.add(new BasicNameValuePair("author", author));

            JSONObject json = jsonParser.makeHttpRequest(url_create_suggestion, "POST", params);

            if(json==null){
                connection = false;
                return null;
            }
            connection = true;

            //Log.d("Create Response", json.toString());

            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    sent = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            if (connection && sent){
                Intent intent = new Intent(SuggestionActivity.this, HomeActivity.class);
                Toast.makeText(SuggestionActivity.this, "Suggestion sent. Thank you for the help.", Toast.LENGTH_LONG).show();
                startActivity(intent);
            } else {
                Toast.makeText(SuggestionActivity.this, "Sorry it was impossible to send the suggestion, check the internet connection.", Toast.LENGTH_LONG).show();
            }
        }

    }
}
