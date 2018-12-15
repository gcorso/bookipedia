package com.gcappslab.bookipedia.Reading;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gcappslab.bookipedia.Library.Book.BookActivity;
import com.gcappslab.bookipedia.Library.Databases.BooksLDH;
import com.gcappslab.bookipedia.Library.Databases.JSONParser;
import com.gcappslab.bookipedia.Library.Library.LibraryActivity;
import com.gcappslab.bookipedia.Library.Objects.Book;
import com.gcappslab.bookipedia.Library.Objects.Constants;
import com.gcappslab.bookipedia.Library.Objects.User;
import com.gcappslab.bookipedia.Login.UserFileManager;
import com.gcappslab.bookipedia.R;
import com.gcappslab.bookipedia.Settings.HelpAndFeedbackActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_BOOKID;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_PROGRESS;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_USERID;
import static com.gcappslab.bookipedia.Library.Objects.Constants.URL_API;

public class ReadingActivity extends AppCompatActivity {

    public static final String CHAPTER_SEP = "<<||chapter||>>";
    private ActionBar myActionBar;
    private Menu menu;

    private RelativeLayout llSuper;
    private TextView tvProgress;

    private LinearLayout llNormal;
    private TextView tvBookNormal;

    private LinearLayout llChapter;
    private TextView tvTitleChapter;
    private TextView tvBookChapter;

    private LinearLayout llFontControl;
    private float dimension;
    private int font;
    private int color;
    private LinearLayout llNotebook;
    private String curtext;
    private int begin;
    private int end;

    private LinearLayout llProgressBar;
    private SeekBar seekBarProgress;
    private boolean onProgressChange;

    private int normalLines;
    private boolean controlOn;
    private boolean fontControlOn;
    private boolean fontControlSetUp;
    private boolean textSelected;
    private boolean menuSet;
    private boolean notebookOn;
    private boolean chapterPage;
    private boolean readingAloud;

    private Book book;
    private int id;
    private String text;
    private NoteList notes;
    private BooksLDH booksLDH;

    private TextToSpeech textToSpeech;
    private User user;

    private static final String url_update_progress = URL_API+"update_progress.php";
    JSONParser jsonParser = new JSONParser();
    private static final int PROGRESS_UPDATE_FREQUENCY = 3;
    private int progress_count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        myActionBar = getSupportActionBar();
        if (myActionBar != null) {
            myActionBar.hide();
        }
        controlOn = false;
        fontControlOn = false;
        fontControlSetUp = false;
        textSelected = false;
        menuSet = false;
        notebookOn = false;
        readingAloud = false;
        progress_count = 0;

        Intent intent = getIntent();
        id = intent.getIntExtra("bookId", 0);
        booksLDH = new BooksLDH(this);
        book = booksLDH.getBookTextById(id);
        text = book.getFile();
        begin = book.getProgress();
        end = begin;
        user = UserFileManager.getInstance(ReadingActivity.this).getUser();
        notes = new NoteList(book.getNotes(), id, user.getUserId(), this);

        llSuper = (RelativeLayout) findViewById(R.id.superLayout);
        tvProgress = (TextView) findViewById(R.id.tvProgress);

        llNormal = (LinearLayout) findViewById(R.id.normalLayout);
        tvBookNormal = (TextView) findViewById(R.id.tvBookNormal);

        llChapter = (LinearLayout) findViewById(R.id.chapterLayout);
        tvTitleChapter = (TextView) findViewById(R.id.tvTitleChapter);
        tvBookChapter = (TextView) findViewById(R.id.tvBookChapter);

        llFontControl = (LinearLayout) findViewById(R.id.llFontControl);
        llNotebook = (LinearLayout) findViewById(R.id.llNotebook);

        llProgressBar = (LinearLayout) findViewById(R.id.control_bottom_bar);
        seekBarProgress = (SeekBar) findViewById(R.id.seekBarProgress);
        onProgressChange = false;

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {}

                        @Override
                        public void onDone(String utteranceId) {
                            // Speaking stopped.
                            new Thread() {
                                public void run() {
                                    ReadingActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            nextPage();
                                        }
                                    });
                                }
                            }.start();
                        }

                        @Override
                        public void onError(String utteranceId) {}
                    });
                }
            }
        });

        retriveStyle();
        setStyle();
        setUpControl();
        highlightListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (readingAloud){
            textToSpeech.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (readingAloud){
            textToSpeech.stop();
        }
    }

    // sets up the menu opened when some text is selected
    private class TextSelectionCallback implements ActionMode.Callback {

        private TextView mTextView;

        public TextSelectionCallback(TextView text) {
            mTextView = text;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_selection, menu);
            textSelected = true;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            menu.removeItem(android.R.id.selectAll);
            menu.removeItem(android.R.id.cut);
            textSelected = true;
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            if (item.getItemId() == R.id.action_highlight){
                int selectionStart = mTextView.getSelectionStart();
                int selectionEnd = mTextView.getSelectionEnd();

                String textSelected = curtext.substring(selectionStart, selectionEnd);
                Note note;
                if (chapterPage){
                    note = new Note(begin + selectionStart + CHAPTER_SEP.length(), textSelected, 'H');
                } else {
                    note = new Note(begin + selectionStart, textSelected, 'H');
                }

                notes.add(note);
                diplayHighlights();
                return true;

            } if (item.getItemId() == R.id.action_note){
                addNote();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            textSelected = false;
        }
    }

    // controls the insertion of a note
    private void addNote(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ReadingActivity.this);
        String dialogTitle = "Note  (location " + String.valueOf(begin) + ")";
        builder.setTitle(dialogTitle);

        LinearLayout ll = new LinearLayout(ReadingActivity.this);
        ll.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(40, 10, 40, 0);

        final EditText input = new EditText(ReadingActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setSingleLine(false);
        input.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        input.setMinHeight(100);

        ll.addView(input, layoutParams);

        builder.setView(ll);

        // Set up the buttons
        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Note note = new Note(begin, input.getText().toString(), 'N');
                notes.add(note);
                Toast.makeText(ReadingActivity.this, "Note saved in the notebook", Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // open the notebook
    private void setUpNotebook(){
        NoteListAdapter notesAdapter = new NoteListAdapter(this, notes, text.length());
        ListView listView = (ListView) findViewById(R.id.listNotes);
        listView.setAdapter(notesAdapter);
    }

    // called when the user touches on a note, moves the book to the position of the note
    public void goToNotebook(int pos){
        llNotebook.setVisibility(View.INVISIBLE);
        notebookOn = false;
        end=pos;
        nextPage();
    }

    // highlights some text
    private void highlightListener(){
        tvBookNormal.setCustomSelectionActionModeCallback( new TextSelectionCallback(tvBookNormal));
        tvBookChapter.setCustomSelectionActionModeCallback( new TextSelectionCallback(tvBookChapter));

        tvBookNormal.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                textSelected = true;
                return false;
            }
        });

        tvBookChapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                textSelected = true;
                return false;
            }
        });

    }

    // sets up the font control
    private void setUpFontControl(){

        //dimension
        ImageView btPlusFont = (ImageView) findViewById(R.id.btPlusDim);
        ImageView btMinusFont = (ImageView) findViewById(R.id.btMinusDim);

        btPlusFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dimension<26){
                    dimension += 1;

                    //save preferences
                    SharedPreferences sharedPref = ReadingActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putFloat(Constants.prefStyleDimensions, dimension);
                    editor.apply();

                    setStyle();
                }

            }
        });

        btMinusFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dimension>7){
                    dimension -= 1;

                    //save preferences
                    SharedPreferences sharedPref = ReadingActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putFloat(Constants.prefStyleDimensions, dimension);
                    editor.apply();

                    setStyle();
                }
            }
        });


        //font
        Spinner spinnerFont = (Spinner) findViewById(R.id.spinnerFonts);
        spinnerFont.setSelection(font);
        spinnerFont.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (font != position){
                    font = position;

                    //save preferences
                    SharedPreferences sharedPref = ReadingActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(Constants.prefStyleFont, font);
                    editor.apply();

                    setStyle();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //color
        Spinner spinnerColor = (Spinner) findViewById(R.id.spinnerColor);
        spinnerColor.setSelection(color);
        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (color != position){
                    color = position;

                    //save preferences
                    SharedPreferences sharedPref = ReadingActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(Constants.prefStyleColor, color);
                    editor.apply();

                    setStyle();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // retrieves the style
    private void retriveStyle(){
        //get preferences for order
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        dimension = sharedPref.getFloat(Constants.prefStyleDimensions, 16.0f);
        font = sharedPref.getInt(Constants.prefStyleFont, 0);
        color = sharedPref.getInt(Constants.prefStyleColor, 0);
    }

    // sets up the style in the layout
    private void setStyle(){

        //dimension
        tvBookNormal.setTextSize(dimension);
        tvBookChapter.setTextSize(dimension);

        //font
        Typeface typeFace;
        typeFace = Typeface.createFromAsset(getAssets(),"fonts/bookerly.ttf");
        switch (font){
            case 1: typeFace = Typeface.createFromAsset(getAssets(),"fonts/caecilia.ttf");
                break;
            case 2: typeFace = Typeface.createFromAsset(getAssets(),"fonts/georgia.ttf");
                break;
            case 3: typeFace = Typeface.createFromAsset(getAssets(),"fonts/palatino.ttf");
                break;
            case 4: typeFace = Typeface.createFromAsset(getAssets(),"fonts/baskerville.ttf");
                break;
            case 5: typeFace = Typeface.createFromAsset(getAssets(),"fonts/helvetica.ttf");
                break;
            case 6: typeFace = Typeface.createFromAsset(getAssets(),"fonts/lucida.ttf");
                break;
        }
        tvBookNormal.setTypeface(typeFace);
        tvBookChapter.setTypeface(typeFace);
        tvTitleChapter.setTypeface(typeFace);

        //color
        switch (color){
            case 0: //white
                llSuper.setBackgroundColor(Color.WHITE);
                tvBookNormal.setTextColor(Color.BLACK);
                tvBookChapter.setTextColor(Color.BLACK);
                tvTitleChapter.setTextColor(getResources().getColor(R.color.whiteTitle));
                tvProgress.setTextColor(getResources().getColor(R.color.whiteProgress));

                //action bar
                if (menuSet){
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.aaw));
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.notew));
                }

                myActionBar.setTitle(Html.fromHtml("<small><font color='#000000' font-style:italic>" + book.getName() + "</font></small>"));

                break;
            case 1: //black
                llSuper.setBackgroundColor(Color.BLACK);
                tvBookNormal.setTextColor(Color.WHITE);
                tvBookChapter.setTextColor(Color.WHITE);
                tvTitleChapter.setTextColor(Color.WHITE);
                tvProgress.setTextColor(Color.WHITE);

                if (menuSet){
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.aab));
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.noteb));
                }

                myActionBar.setTitle(Html.fromHtml("<small><font color='#ffffff' font-style:italic>" + book.getName() + "</font></small>"));


                break;
            case 2: //sepia
                llSuper.setBackgroundColor(getResources().getColor(R.color.sepiaBack));
                tvBookNormal.setTextColor(getResources().getColor(R.color.sepiaText));
                tvBookChapter.setTextColor(getResources().getColor(R.color.sepiaText));
                tvTitleChapter.setTextColor(getResources().getColor(R.color.sepiaText));
                tvProgress.setTextColor(getResources().getColor(R.color.sepiaText));

                if (menuSet){
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.aaw));
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.notew));
                }

                myActionBar.setTitle(Html.fromHtml("<small><font color='#000000' font-style:italic>" + book.getName() + "</font></small>"));

                break;
        }

        end = begin;
        initiatePage();
    }

    // sets up the action bar (control bar)
    private void setUpControl(){
        String nameTranf = "<small>" + book.getName() + "</small>";
        myActionBar.setTitle(Html.fromHtml(nameTranf));
    }

    // shows the action bar, called when the user taps on the middle of the screen
    private void showControl(){
        myActionBar.show();
        controlOn = true;

        llProgressBar.setVisibility(View.VISIBLE);
        tvProgress.setVisibility(View.INVISIBLE);
        final int maxValue= seekBarProgress.getMax();
        seekBarProgress.setProgress(maxValue*begin/text.length());

        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onProgressChange = true;
                end = progress*text.length()/maxValue;
                end += text.substring(end).indexOf("\n")+1;
                nextPage();
                onProgressChange = false;
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }

    // hides the control bar from the screen
    private void removeControl(){
        myActionBar.hide();
        controlOn = false;

        llProgressBar.setVisibility(View.GONE);
        tvProgress.setVisibility(View.VISIBLE);
        seekBarProgress.setOnSeekBarChangeListener(null);
    }

    // initiates the page by calculating how many lines fit in a normal layout (cuts the time taken to change page)
    private void initiatePage(){

        // calculates how many lines are needed to display a normal page
        curtext = text.substring(0, 3000);
        tvBookNormal.setText(curtext);

        final ViewTreeObserver vto = tvBookNormal.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int height = tvBookNormal.getHeight();
                Layout layout = tvBookNormal.getLayout();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    tvBookNormal.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    tvBookNormal.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                int lastVisibleLineNumber = layout.getLineForVertical(height);

                //check is latest line fully visible
                if (tvBookNormal.getHeight() < layout.getLineBottom(lastVisibleLineNumber)) {
                    lastVisibleLineNumber--;
                }
                normalLines = lastVisibleLineNumber+1;

                //display first page
                nextPage();

                LinearLayout llClickLeft = (LinearLayout) findViewById(R.id.llClickLeft);
                llClickLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fontControlOn){
                            llFontControl.setVisibility(View.INVISIBLE);
                            fontControlOn = false;
                        } else if (notebookOn){
                            llNotebook.setVisibility(View.INVISIBLE);
                            notebookOn = false;
                        } else {
                            previousPage();
                        }
                    }
                });
                LinearLayout llClickRight = (LinearLayout) findViewById(R.id.llClickRight);
                llClickRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fontControlOn){
                            llFontControl.setVisibility(View.INVISIBLE);
                            fontControlOn = false;
                        } else if (notebookOn){
                            llNotebook.setVisibility(View.INVISIBLE);
                            notebookOn = false;
                        } else {
                            nextPage();
                        }
                    }
                });

                tvBookChapter.setOnTouchListener(touchControl());
                tvBookNormal.setOnTouchListener(touchControl());
            }
        });

    }

    // calls the different sets up routines or the navigation routines
    // depending on the posiotion of the user tap and the current state
    private View.OnTouchListener touchControl() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int X = (int) (v.getX() + event.getX());
                int Y = (int) (v.getY() + event.getY());

                if (fontControlOn){
                    if ((X<llFontControl.getX())||(X>llFontControl.getX()+llFontControl.getWidth())||
                        (Y<llFontControl.getY())||(Y>llFontControl.getY()+llFontControl.getHeight())){

                        llFontControl.setVisibility(View.INVISIBLE);
                        fontControlOn = false;
                    }
                } else if (notebookOn){
                    if ((X<llNotebook.getX())||(X>llNotebook.getX()+llNotebook.getWidth())||
                            (Y<llNotebook.getY())||(Y>llNotebook.getY()+llNotebook.getHeight())){

                        llNotebook.setVisibility(View.INVISIBLE);
                        notebookOn = false;
                    }
                } else {

                    int eventaction = event.getAction();
                    if ((eventaction == MotionEvent.ACTION_UP)&&(!textSelected)) {
                        if (X < llSuper.getWidth() / 3) {
                            previousPage();
                        } else if (X > llSuper.getWidth() * 2 / 3) {
                            nextPage();
                        } else {
                            if (controlOn) {
                                if (Y<llProgressBar.getY()-150){
                                    removeControl();
                                }
                            } else {
                                showControl();
                            }
                        }
                    }
                }
                return false;
            }
        };
    }

    // retrieves the text to display in the next page
    private void nextPage(){

        if (end >= text.length()-1){
            displayRatingPage();
            return;
        }
        begin = end;
        if (begin+5000<text.length()){
            curtext = text.substring(begin, begin+5000);
        } else {
            curtext = text.substring(begin);
        }
        while (curtext.startsWith(" ")){
            curtext = curtext.substring(1);
            begin++;
        }

        if (curtext.substring(1).contains(CHAPTER_SEP)){
            int last = curtext.indexOf(CHAPTER_SEP, 2)-1;
            curtext = curtext.substring(0, last+1);
        }

        displayNextPage();
        displayProgress();
    }

    // displays the text in the next page and adjusts the limits of the text
    private void displayNextPage(){

        if (curtext.startsWith(CHAPTER_SEP)){
            int sep = curtext.indexOf("\n");

            llNormal.setVisibility(View.INVISIBLE);
            llChapter.setVisibility(View.VISIBLE);
            chapterPage = true;

            tvTitleChapter.setText(curtext.substring(CHAPTER_SEP.length(), sep));
            curtext = curtext.substring(sep+1, curtext.length());
            tvBookChapter.setText(curtext);
            int height = tvBookChapter.getHeight();
            Layout layout = tvBookChapter.getLayout();

            int lastVisibleLineNumber = layout.getLineForVertical(height);
            while (tvBookChapter.getHeight() < layout.getLineBottom(lastVisibleLineNumber)) {
                lastVisibleLineNumber--;
            }
            int tempend = layout.getLineEnd(lastVisibleLineNumber);
            curtext = curtext.substring(0, tempend);
            tvBookChapter.setText(curtext);
            end = begin + tempend + CHAPTER_SEP.length() + tvTitleChapter.getText().length() +1;

        } else {
            llNormal.setVisibility(View.VISIBLE);
            llChapter.setVisibility(View.INVISIBLE);
            tvBookNormal.setText(curtext);
            chapterPage = false;

            if (tvBookNormal.getLayout().getLineCount()>normalLines){
                int tempend = tvBookNormal.getLayout().getLineEnd(normalLines -1);
                curtext = curtext.substring(0, tempend);
                end = begin + tempend;
                tvBookNormal.setText(curtext);
            } else {
                end = begin + curtext.length();
            }
        }

        if (readingAloud){
            speak(curtext);
        }
        diplayHighlights();
    }

    // retrieves the text to display in the previous page
    private void previousPage() {
        if (begin == 0){
            return;
        }

        if (begin>5000){
            curtext = text.substring(begin-5000, begin);
        } else {
            curtext = text.substring(0, begin);
        }

        end = begin;
        tvBookNormal.setText(curtext);
        Layout layout = tvBookNormal.getLayout();

        if (layout.getLineCount()-normalLines-1<0){
            begin = 0;
        } else {
            int tempbegin = tvBookNormal.getLayout().getLineEnd(layout.getLineCount()- normalLines -1);
            curtext = curtext.substring(tempbegin, curtext.length());
            begin = end - curtext.length();
        }

        if (curtext.contains(CHAPTER_SEP)){
            int p = curtext.lastIndexOf(CHAPTER_SEP);
            end = end - curtext.length() + p;
            nextPage();

        } else {

            while (curtext.startsWith(" ")){
                curtext = curtext.substring(1);
                begin++;
            }

            llNormal.setVisibility(View.VISIBLE);
            llChapter.setVisibility(View.INVISIBLE);
            tvBookNormal.setText(curtext);
            chapterPage = false;
            diplayHighlights();

            if (readingAloud){
                speak(curtext);
            }

            displayProgress();
        }
    }

    // highlights some parts of the text if there are any in the current page
    private void diplayHighlights(){
        List<Note> list = notes.highlightsInInterval(begin, end);
        Spannable span = new SpannableString(curtext);

        for (Note highlight: list){
            int hbegin = highlight.getPos() - begin;
            int hend = highlight.getEnd() - begin;
            if (chapterPage){
                hbegin-= CHAPTER_SEP.length();
                hend -= CHAPTER_SEP.length();
            }
            if (hbegin<0){
                hbegin = 0;
            }
            if (hend>curtext.length()){
                hend = curtext.length();
            }

            if (color != 1){
                span.setSpan(new BackgroundColorSpan(Color.YELLOW), hbegin, hend, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                span.setSpan(new BackgroundColorSpan(Color.DKGRAY), hbegin, hend, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        if (chapterPage){
            tvBookChapter.setText(span);
        } else {
            tvBookNormal.setText(span);
        }
    }

    // displays the percentage progress of the user in the current book
    private void displayProgress(){
        int perc = begin*100/text.length();
        String str = String.valueOf(perc) + "%";
        tvProgress.setText(str);

        if (controlOn&&(!onProgressChange)){
            removeControl();
        }

        booksLDH.updateProgress(id, begin);
        progress_count += 1;
        if (progress_count>=PROGRESS_UPDATE_FREQUENCY){
            progress_count = 0;
            new UpdateProgress().execute();
        }
    }

    // moves the app to the rating page
    private void displayRatingPage(){
        Intent intent = new Intent(ReadingActivity.this, RatingActivity.class);
        intent.putExtra("bookId", book.getBookId());
        startActivity(intent);
    }

    // makes the phone read aloud the text of the current page
    private void speak(String text) {
        if(text != null) {
            HashMap<String, String> myHashAlarm = new HashMap<String, String>();
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "_id");
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
        }
    }

    // sets up the menu of the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reading, menu);
        this.menu = menu;
        menuSet = true;

        if (color == 1){   // black
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.aab));
            menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.noteb));
            myActionBar.setTitle(Html.fromHtml("<small><font color='#ffffff' font-style:italic>" + book.getName() + "</font></small>"));

        }

        return true;
    }

    // controls the various clicks on the action bar menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_font) {
            if (!fontControlSetUp){
                setUpFontControl();
                fontControlSetUp = true;
            }

            llFontControl.setVisibility(View.VISIBLE);
            fontControlOn = true;
            removeControl();
            return true;

        } else if (id == R.id.action_note){
            addNote();
            return true;

        } else if (id == R.id.action_notebook) {

            llNotebook.setVisibility(View.VISIBLE);
            notebookOn = true;
            removeControl();
            setUpNotebook();
            return true;

        } else if (id == R.id.action_bookmark) {
            Note note = new Note(begin, " ", 'B');
            notes.add(note);
            Toast.makeText(ReadingActivity.this, "Bookmark saved. You will find it in the notebook.", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.action_about) {
            Intent intent = new Intent(ReadingActivity.this, BookActivity.class);
            intent.putExtra("bookId", book.getBookId());
            startActivity(intent);
        } else if (id == R.id.action_help_and_feedback) {
            Intent intent = new Intent(ReadingActivity.this, HelpAndFeedbackActivity.class);
            startActivity(intent);
        }else if (id == R.id.action_read) {
            if (readingAloud){
                readingAloud = false;
                textToSpeech.stop();
                menu.getItem(4).setTitle("Read Aloud");
            } else {
                readingAloud = true;
                speak(curtext);
                menu.getItem(4).setTitle("Stop reading");
            }

        } else if (id == android.R.id.home){
            if(readingAloud){
                textToSpeech.stop();
            }
            Intent intent = new Intent(ReadingActivity.this, LibraryActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Background Async Task to update progress in the server database
     * */
    private class UpdateProgress extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_BOOKID, Integer.toString(id)));
            params.add(new BasicNameValuePair(TAG_USERID, Integer.toString(user.getUserId())));
            params.add(new BasicNameValuePair(TAG_PROGRESS, Integer.toString(begin)));
            Log.i("progress update", Integer.toString(id) + " " + Integer.toString(user.getUserId()) + " " + Integer.toString(begin));

            jsonParser.makeHttpRequest(url_update_progress, "POST", params);
            return null;
        }

        protected void onPostExecute(String file_url) {}
    }
}