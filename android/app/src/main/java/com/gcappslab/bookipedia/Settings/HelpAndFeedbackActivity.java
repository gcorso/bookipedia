package com.gcappslab.bookipedia.Settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gcappslab.bookipedia.Library.Databases.BooksLDH;
import com.gcappslab.bookipedia.Login.LoginActivity;
import com.gcappslab.bookipedia.Login.UserFileManager;
import com.gcappslab.bookipedia.R;

public class HelpAndFeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_feedback);

        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_titletext_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Help & Feedback");
        if (abar != null) {
            abar.setCustomView(viewActionBar, params);
            abar.setDisplayShowCustomEnabled(true);
            abar.setDisplayShowTitleEnabled(false);
        }

        String[] settings=new String[]{"Log Out","Suggest a book", "Feedback", "Share with friends", "Rate this app"};
        ArrayAdapter<String> adapterSettings=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                settings);
        ListView lvHelp;
        lvHelp = (ListView) findViewById(R.id.lvHelp);
        if (lvHelp != null){
            lvHelp.setAdapter(adapterSettings);
            lvHelp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    if (position==0) {
                        // log out
                        new AlertDialog.Builder(HelpAndFeedbackActivity.this)
                                .setTitle("Log Out")
                                .setMessage("Are you sure you want to log out?")
                                .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        UserFileManager userFileManager = UserFileManager.getInstance(HelpAndFeedbackActivity.this);
                                        userFileManager.deleteEverything(HelpAndFeedbackActivity.this);
                                        BooksLDH booksLDH = new BooksLDH(HelpAndFeedbackActivity.this);
                                        booksLDH.deleteEverything();

                                        Intent intent = new Intent(HelpAndFeedbackActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .show();



                    } else if (position == 1){
                        // suggest a book
                        Intent intent = new Intent(HelpAndFeedbackActivity.this, SuggestionActivity.class);
                        startActivity(intent);

                    } else if (position==2) {
                        // send feedback
                        String[] TO = {"contact@bookipedia.gcappslab.com"};
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setData(Uri.parse("mailto:"));
                        emailIntent.setType("text/plain");


                        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bookipedia Feedback");

                        try {
                            startActivity(Intent.createChooser(emailIntent, "Send mail with..."));
                            finish();
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(HelpAndFeedbackActivity.this,
                                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
                        }

                    }  else if (position==3) {
                        // share the app
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setData(Uri.parse("mailto:"));
                        shareIntent.setType("text/plain");

                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Bookipedia");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Try Bookipedia, the new open mobile library, on the Play Store at https://play.google.com/store/apps/details?id=com.gcappslab.bookipedia");

                        try {
                            startActivity(Intent.createChooser(shareIntent, "Share with..."));
                            finish();
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(HelpAndFeedbackActivity.this, "There is no sharing client installed.", Toast.LENGTH_SHORT).show();
                        }
                    } else if (position == 4){
                        // rate the app
                        Intent openBookmark = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.gcappslab.bookipedia"));
                        startActivity(openBookmark);
                    }
                }
            });

        }
    }
}
