package com.gcappslab.bookipedia.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gcappslab.bookipedia.Library.Databases.BooksLDH;
import com.gcappslab.bookipedia.Library.Databases.JSONParser;
import com.gcappslab.bookipedia.Library.Home.HomeActivity;
import com.gcappslab.bookipedia.Library.Library.LibraryActivity;
import com.gcappslab.bookipedia.Library.Objects.Book;
import com.gcappslab.bookipedia.Library.Objects.User;
import com.gcappslab.bookipedia.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_AUTHORID;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_AUTHORNAME;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_BOOKID;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_BOOKS;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_DESCRIPTION;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_EMAIL;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_FAVGENRES;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_FILE;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_GENRE;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_LANGUAGE;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_LASTDEVICE;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_NAME;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_NOTES;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_PASSWORD;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_PROGRESS;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_RATING;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_REVIEWS;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_SUCCESS;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_SYNC;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_USER;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_USERID;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_YEAR;
import static com.gcappslab.bookipedia.Library.Objects.Constants.URL_API;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;

    private ProgressDialog pDialog;
    private JSONParser jsonParser = new JSONParser();
    private boolean connection;

    public static String url_login = URL_API+"login.php";
    public static String url_books_user = URL_API+"get_books_user.php";
    public static String url_check_device = URL_API+"check_device.php";
    private boolean logged;
    private boolean tosync;

    private String email;
    private String pass;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // check if a user is already logged in
        user = UserFileManager.getInstance(this).getUser();
        if (user!=null){
            Log.i("userid ", Integer.toString(user.getUserId()));
            new CheckDevice().execute();
        }

        ActionBar myActionBar = getSupportActionBar();
        if (myActionBar != null) {
            myActionBar.hide();
        }

        logged = false;
        etEmail = (EditText) findViewById(R.id.emailField);
        etPassword = (EditText) findViewById(R.id.passwordField);
        ImageView btLogin = (ImageView) findViewById(R.id.loginButton);

        makeRegistrationLink();

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString();
                if (!(EmailValidator.emailValidator(email))){
                    Toast.makeText(LoginActivity.this, "Email not valid, please check its correctness.", Toast.LENGTH_LONG).show();
                } else {
                    pass = etPassword.getText().toString();
                    if ((pass.length() < 6) || (pass.length() > 20)) {
                        Toast.makeText(LoginActivity.this, "Password not valid, it must to be between 6 and 20 characters", Toast.LENGTH_LONG).show();
                    } else {
                        new Login().execute();
                    }
                }
            }
        });
    }

    // creates the link connection with the RegistrationActivity
    public void makeRegistrationLink() {
        SpannableString registrationPrompt = new SpannableString( getString( R.string.register_prompt ) );

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick( View widget ) {
                Intent startRegistrationActivity = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(startRegistrationActivity);
            }
        };

        String linkText = getString( R.string.register_link );
        int linkStartIndex = registrationPrompt.toString().indexOf( linkText );
        int linkEndIndex = linkStartIndex + linkText.length();
        registrationPrompt.setSpan( clickableSpan, linkStartIndex, linkEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );

        TextView registerPromptView = (TextView) findViewById( R.id.registerPromptText );
        registerPromptView.setText( registrationPrompt );
        registerPromptView.setMovementMethod( LinkMovementMethod.getInstance() );
    }


    /**
     * Background Async Task to login
     * */
    private class Login extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Login in. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... params) {
            int success;
            try {

                Random rand = new Random();
                int lastDevice = rand.nextInt(500000);

                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair(TAG_EMAIL, email));
                param.add(new BasicNameValuePair(TAG_PASSWORD, pass));
                param.add(new BasicNameValuePair(TAG_LASTDEVICE, Integer.toString(lastDevice)));

                JSONObject json = jsonParser.makeHttpRequest(url_login, "GET", param);

                if(json==null){
                    connection = false;
                    return null;
                }
                connection = true;

                //Log.d("Single user Details", json.toString());

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    JSONArray userObj = json.getJSONArray(TAG_USER);
                    JSONObject juser = userObj.getJSONObject(0);

                    int userid = juser.getInt(TAG_USERID);
                    String language = juser.getString(TAG_LANGUAGE);
                    String favgenres = juser.getString(TAG_FAVGENRES);

                    user  = new User(userid, email, language, favgenres, lastDevice );
                    logged = true;

                    UserFileManager.getInstance(LoginActivity.this).saveUser(LoginActivity.this, user);
                }else{
                    logged = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            if(connection){
                if (logged){
                    new Synchronization().execute();
                } else {
                    Toast.makeText(LoginActivity.this, "Email or password not correct. Please check them.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Internet connection not available. Please check your connection.", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Background Async Task to check lastdevice of a user
     * */
    private class CheckDevice extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Synchronizing your books. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... params) {
            int success;
            try {
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair(TAG_USERID, Integer.toString(user.getUserId())));
                param.add(new BasicNameValuePair(TAG_LASTDEVICE, Integer.toString(user.getLastDevice())));

                JSONObject json = jsonParser.makeHttpRequest(url_check_device, "GET", param);

                if(json==null){
                    connection = false;
                    return null;
                }
                connection = true;

                //Log.d("Check device details " + Integer.toString(user.getUserId()), json.toString() );

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    int sync = json.getInt(TAG_SYNC);

                    if(sync == 1){
                        tosync = true;
                    } else {
                        tosync = false;
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if(connection){
                if (tosync){
                    new Synchronization().execute();
                } else {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                Intent intent = new Intent(LoginActivity.this, LibraryActivity.class);
                Toast.makeText(LoginActivity.this, "Internet connection not available. Please check your connection.", Toast.LENGTH_LONG).show();
                startActivity(intent);
                finish();
            }

        }
    }


    /**
     * Background Async Task to synchronize the books of a user
     * */
    private class Synchronization extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Synchronizing your books. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... params) {
            int success;
            try {
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair(TAG_USERID, Integer.toString(user.getUserId())));
                JSONObject json = jsonParser.makeHttpRequest(url_books_user, "GET", param);

                if(json==null){
                    connection = false;
                    return null;
                }
                connection = true;

                //Log.d("Books user details " + Integer.toString(user.getUserId()), json.toString() );

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    JSONArray jsonBooks = json.getJSONArray(TAG_BOOKS);
                    BooksLDH booksLDH = new BooksLDH(LoginActivity.this);
                    booksLDH.deleteEverything();

                    for (int i = 0; i < jsonBooks.length(); i++) {
                        JSONObject c = jsonBooks.getJSONObject(i);
                        Book book = new Book();

                        book.setBookId(c.getInt(TAG_BOOKID));
                        book.setName(c.getString(TAG_NAME));
                        book.setAuthorId(c.getInt(TAG_AUTHORID));
                        book.setAuthorName(c.getString(TAG_AUTHORNAME));
                        book.setYear(c.getInt(TAG_YEAR));
                        book.setDescription(c.getString(TAG_DESCRIPTION));
                        book.setFile(c.getString(TAG_FILE));
                        book.setGenre(c.getString(TAG_GENRE));
                        book.setLanguage(c.getString(TAG_LANGUAGE));
                        book.setRating((float) c.getDouble(TAG_RATING));
                        book.setReviews(c.getInt(TAG_REVIEWS));
                        book.setProgress(c.getInt(TAG_PROGRESS));
                        book.setNotes(c.getString(TAG_NOTES));
                        booksLDH.saveBook(book);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (connection){
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            } else {
                Toast.makeText(LoginActivity.this, "Internet connection not available. Please check your connection.", Toast.LENGTH_LONG).show();

            }

        }
    }
}
