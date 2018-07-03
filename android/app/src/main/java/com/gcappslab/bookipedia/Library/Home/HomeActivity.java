package com.gcappslab.bookipedia.Library.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gcappslab.bookipedia.Library.Databases.BooksLDH;
import com.gcappslab.bookipedia.Library.Databases.LoadBooks;
import com.gcappslab.bookipedia.Library.Library.LibraryActivity;
import com.gcappslab.bookipedia.Library.Objects.Book;
import com.gcappslab.bookipedia.Library.Objects.BottomBarManager;
import com.gcappslab.bookipedia.Library.Objects.User;
import com.gcappslab.bookipedia.Login.UserFileManager;
import com.gcappslab.bookipedia.R;

import java.util.List;

public class HomeActivity extends AppCompatActivity{

    private HorizontalGridView gridJumpBack;
    private BookHorizGridAdapter adapterJumpBack;
    private BooksLDH booksLDH;

    private HorizontalGridView gridPopular;
    private BookHorizGridAdapter adapterPopular;
    private static final String wherePopular = " WHERE file IS NOT NULL ORDER BY downloads*(RAND()+2) DESC  LIMIT 30";

    private HorizontalGridView gridRating;
    private BookHorizGridAdapter adapterRating;
    private static final String whereRating = " WHERE file IS NOT NULL ORDER BY rating*LN(reviews+1)*(RAND()+2) DESC  LIMIT 30";

    private HorizontalGridView gridGenre;
    private BookHorizGridAdapter adapterGenre;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_titletext_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Home");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);

        user = UserFileManager.getInstance(this).getUser();
        BottomBarManager.setBar(HomeActivity.this);

        // Populating Jump Back In
        gridJumpBack = (HorizontalGridView) findViewById(R.id.gridJumpBack);
        booksLDH = new BooksLDH(this);
        adapterJumpBack = new BookHorizGridAdapter(this, booksLDH.getBooksDisplay(), true);
        if(adapterJumpBack.getItemCount()==0){
            gridJumpBack.setVisibility(View.GONE);
            TextView tvJump = (TextView) findViewById(R.id.tvJump);
            tvJump.setVisibility(View.GONE);
        }
        gridJumpBack.setAdapter(adapterJumpBack);

        // Populating Popular Books
        gridPopular = (HorizontalGridView) findViewById(R.id.gridPopular);
        new LoadBooks(wherePopular, new LoadBooks.LoadBooksAsyncResponse() {
            @Override
            public void processFinish(List<Book> output, boolean connection) {
                if (connection){
                    adapterPopular = new BookHorizGridAdapter(HomeActivity.this, output, false);
                    gridPopular.setAdapter(adapterPopular);
                } else {
                    Intent intent = new Intent(HomeActivity.this, LibraryActivity.class);
                    Toast.makeText(HomeActivity.this, "Internet connection not available. Please check your connection.", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }

            }
        }).execute();

        // Populating Rating Books
        gridRating = (HorizontalGridView) findViewById(R.id.gridRating);
        new LoadBooks(whereRating, new LoadBooks.LoadBooksAsyncResponse() {
            @Override
            public void processFinish(List<Book> output, boolean connection) {
                if (connection){
                    adapterRating = new BookHorizGridAdapter(HomeActivity.this, output, false);
                    gridRating.setAdapter(adapterRating);
                } else {
                    Intent intent = new Intent(HomeActivity.this, LibraryActivity.class);
                    Toast.makeText(HomeActivity.this, "Internet connection not available. Please check your connection.", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }

            }
        }).execute();

        // Populating Genre Books
        String genre = user.getFavGenres();
        String whereGenre = "WHERE genre LIKE '%" + genre + "%' AND file IS NOT NULL ORDER BY rating*LN(reviews+1)*(RAND()+2) DESC  LIMIT 20";
        gridGenre = (HorizontalGridView) findViewById(R.id.gridGenre);
        new LoadBooks(whereGenre, new LoadBooks.LoadBooksAsyncResponse() {
            @Override
            public void processFinish(List<Book> output, boolean connection) {

                if (connection){
                    adapterGenre = new BookHorizGridAdapter(HomeActivity.this, output, false);
                    gridGenre.setAdapter(adapterGenre);
                } else {
                    Intent intent = new Intent(HomeActivity.this, LibraryActivity.class);
                    Toast.makeText(HomeActivity.this, "Internet connection not available. Please check your connection.", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
            }
        }).execute();
        TextView tvGenre = (TextView) findViewById(R.id.tvGenre);
        tvGenre.setText("Popular In "+ genre);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapterJumpBack = new BookHorizGridAdapter(this, booksLDH.getBooksDisplay(), true);
        if(adapterJumpBack.getItemCount()==0){
            gridJumpBack.setVisibility(View.GONE);
            TextView tvJump = (TextView) findViewById(R.id.tvJump);
            tvJump.setVisibility(View.GONE);
        }
        gridJumpBack.setAdapter(adapterJumpBack);
    }
}
