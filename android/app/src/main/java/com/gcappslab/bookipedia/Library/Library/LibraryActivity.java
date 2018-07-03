package com.gcappslab.bookipedia.Library.Library;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gcappslab.bookipedia.Library.Databases.BooksLDH;
import com.gcappslab.bookipedia.Library.Objects.BottomBarManager;
import com.gcappslab.bookipedia.Library.Objects.Constants;
import com.gcappslab.bookipedia.R;

public class LibraryActivity extends AppCompatActivity {

    private BookTableGridCursorAdapter adapter;
    private BooksLDH databaseHelper;
    private int sort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_titletext_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Library");
        if (abar != null) {
            abar.setCustomView(viewActionBar, params);
            abar.setDisplayShowCustomEnabled(true);
            abar.setDisplayShowTitleEnabled(false);
        }

        BottomBarManager.setBar(LibraryActivity.this);

        //get preferences for order
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        sort = sharedPref.getInt(Constants.prefLocalOrder, 0);

        databaseHelper = new BooksLDH(this);
        adapter = new BookTableGridCursorAdapter(this, databaseHelper.getBooksDisplayCursor(sort));
        GridView gridView = (GridView) findViewById(R.id.bookTable);
        gridView.setAdapter(adapter);

        if(adapter.getCount()==0){
            LinearLayout noBooksLL = (LinearLayout) findViewById(R.id.noBooksLL);
            noBooksLL.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_library, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort) {

            final Dialog dialog = new Dialog(LibraryActivity.this);
            dialog.setContentView(R.layout.sort_dialog_local);

            RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupSort);
            if (sort == BooksLDH.SORT_OPEN){
                radioGroup.check(R.id.radioOpen);
            } else if (sort == BooksLDH.SORT_TITLE){
                radioGroup.check(R.id.radioTitle);
            } else if (sort == BooksLDH.SORT_AUTHOR){
                radioGroup.check(R.id.radioAuthor);
            } else if (sort == BooksLDH.SORT_YEAR){
                radioGroup.check(R.id.radioYear);
            }
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    if (checkedId == R.id.radioOpen){
                        sort = BooksLDH.SORT_OPEN;
                    } else if (checkedId == R.id.radioTitle){
                        sort = BooksLDH.SORT_TITLE;
                    } else if (checkedId == R.id.radioAuthor){
                        sort = BooksLDH.SORT_AUTHOR;
                    } else if (checkedId == R.id.radioYear){
                        sort = BooksLDH.SORT_YEAR;
                    }
                    adapter.changeCursor(databaseHelper.getBooksDisplayCursor(sort));
                    adapter.notifyDataSetChanged();

                    //save preferences
                    SharedPreferences sharedPref = LibraryActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(Constants.prefLocalOrder, sort);
                    editor.apply();

                    dialog.dismiss();

                }
            });

            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
