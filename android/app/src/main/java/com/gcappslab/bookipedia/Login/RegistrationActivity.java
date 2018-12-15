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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gcappslab.bookipedia.Library.Databases.JSONParser;
import com.gcappslab.bookipedia.Library.Home.HomeActivity;
import com.gcappslab.bookipedia.Library.Objects.Constants;
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

import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_EMAIL;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_LASTDEVICE;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_PASSWORD;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_SUCCESS;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_USER;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_USERID;
import static com.gcappslab.bookipedia.Library.Objects.Constants.URL_API;
import static com.gcappslab.bookipedia.Login.LoginActivity.url_login;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPass;
    private EditText etPassR;
    private Spinner favGenre;
    private User user;
    private String password;

    private ProgressDialog pDialog;
    private JSONParser jsonParser = new JSONParser();
    private boolean connection;

    private static String url_register = URL_API+"register_user.php";
    private boolean saved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ActionBar myActionBar = getSupportActionBar();
        if (myActionBar != null) {
            myActionBar.hide();
        }

        saved = false;

        ImageView btRegister = (ImageView) findViewById(R.id.btRegister);
        etEmail = (EditText) findViewById(R.id.emailField);
        etPass = (EditText) findViewById(R.id.passwordField);
        etPassR = (EditText) findViewById(R.id.rpasswordField);
        favGenre = (Spinner) findViewById(R.id.genreField);

        ArrayAdapter<String> genresAdapter = new ArrayAdapter<String>(this,
                R.layout.dropdown_genre, Constants.GenresReg);
        genresAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        favGenre.setAdapter(genresAdapter);

        makeLoginLink();

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                if (!(EmailValidator.emailValidator(email))){
                    Toast.makeText(RegistrationActivity.this, "Email not valid, please check its correctness.", Toast.LENGTH_LONG).show();
                } else {
                    password = etPass.getText().toString();
                    if((password.length()<6)||(password.length()>20)){
                        Toast.makeText(RegistrationActivity.this, "Password not valid, it needs to be between 6 and 20 characters", Toast.LENGTH_LONG).show();

                    } else {
                        String passr = etPassR.getText().toString();
                        if (!(password.equals(passr))){
                            Toast.makeText(RegistrationActivity.this, "Passwords do not match, please check them.", Toast.LENGTH_LONG).show();

                        } else {
                            String genre = favGenre.getSelectedItem().toString();

                            if (genre.startsWith("Fav")){
                                Toast.makeText(RegistrationActivity.this, "Please choose a favorite genre.", Toast.LENGTH_LONG).show();
                            } else {
                                String language = "English";
                                user = new User(email, language, genre);
                                new RegisterUser().execute();
                            }
                        }
                    }
                }
            }
        });
    }

    // creates the link connection with the LoginActivity
    public void makeLoginLink() {
        SpannableString loginPrompt = new SpannableString( getString( R.string.login_prompt ) );

        ClickableSpan clickableSpan = new ClickableSpan()
        {
            @Override
            public void onClick( View widget )
            {
                Intent startLoginActivity = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(startLoginActivity);
                finish();
            }
        };

        String linkText = getString( R.string.login_link );
        int linkStartIndex = loginPrompt.toString().indexOf( linkText );
        int linkEndIndex = linkStartIndex + linkText.length();
        loginPrompt.setSpan( clickableSpan, linkStartIndex, linkEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );

        TextView registerPromptView = (TextView) findViewById( R.id.loginPromptText );
        registerPromptView.setText( loginPrompt );
        registerPromptView.setMovementMethod( LinkMovementMethod.getInstance() );
    }

    /**
     * Background Async Task to register user
     * */
    private class RegisterUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegistrationActivity.this);
            pDialog.setMessage("Register..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", user.getEmail()));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("language", user.getLanguage()));
            params.add(new BasicNameValuePair("favgenres", user.getFavGenres()));

            JSONObject json = jsonParser.makeHttpRequest(url_register, "POST", params);

            if(json==null){
                connection = false;
                return null;
            }
            connection = true;

            //Log.d("Create Response", json.toString());

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    try {

                        Random rand = new Random();
                        int lastDevice = rand.nextInt(500000);

                        List<NameValuePair> param = new ArrayList<NameValuePair>();
                        param.add(new BasicNameValuePair(TAG_EMAIL, user.getEmail()));
                        param.add(new BasicNameValuePair(TAG_PASSWORD, password));
                        param.add(new BasicNameValuePair(TAG_LASTDEVICE, Integer.toString(lastDevice)));

                        json = jsonParser.makeHttpRequest(url_login, "GET", param);

                        if(json==null){
                            connection = false;
                            return null;
                        }
                        connection = true;

                        //Log.d("Single user Details", json.toString());

                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully logged in
                            JSONArray userObj = json.getJSONArray(TAG_USER);
                            JSONObject juser = userObj.getJSONObject(0);

                            int userid = juser.getInt(TAG_USERID);

                            user.setUserId(userid);
                            user.setLastDevice(lastDevice);

                            UserFileManager.getInstance(RegistrationActivity.this).saveUser(RegistrationActivity.this, user);

                            saved = true;
                        }else{
                            saved = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                if (saved){
                    Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                    startActivity(intent);
                    RegistrationActivity.this.finish();
                } else {
                    Toast.makeText(RegistrationActivity.this, "Sorry it was impossible to register, if you already have an account with this email please log in instead.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(RegistrationActivity.this, "Internet connection not available. Please check your connection.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
