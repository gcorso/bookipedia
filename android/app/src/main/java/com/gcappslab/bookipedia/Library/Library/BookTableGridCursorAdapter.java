package com.gcappslab.bookipedia.Library.Library;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.gcappslab.bookipedia.Library.Book.BookActivity;
import com.gcappslab.bookipedia.Library.Databases.BooksLDH;
import com.gcappslab.bookipedia.Library.Databases.JSONParser;
import com.gcappslab.bookipedia.Login.UserFileManager;
import com.gcappslab.bookipedia.R;
import com.gcappslab.bookipedia.Reading.ReadingActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import static com.gcappslab.bookipedia.Library.Objects.Constants.Colors;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_BOOKID;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_USERID;

/**
 * Adapter to manage and display the grid view used for books in the Library activity
 */

public class BookTableGridCursorAdapter extends CursorAdapter{

    private Context context;
    private int idcanc;
    private int userid;

    public BookTableGridCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
        userid = UserFileManager.getInstance(context).getUser().getUserId();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(R.layout.book_table_grid, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView tvBookTitle = (TextView) view.findViewById(R.id.tvBookTitle);
        TextView tvBookAuthor = (TextView) view.findViewById(R.id.tvBookAuthor);
        final String title = cursor.getString(1);
        tvBookTitle.setText(title);
        tvBookAuthor.setText(cursor.getString(2));
        int back = cursor.getInt(0)%Colors.length;
        view.setBackgroundColor(Color.parseColor(Colors[back]));

        TextView tvProgress = (TextView) view.findViewById(R.id.tvProgress);
        tvProgress.setVisibility(View.VISIBLE);
        int perc = (int)(100*cursor.getInt(3)/cursor.getInt(4));
        String p = Integer.toString(perc) + "%";
        tvProgress.setText(p);

        final int id = cursor.getInt(0);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReadingActivity.class);
                intent.putExtra("bookId", id);
                context.startActivity(intent);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //Log.i("Long click", "in");

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title);
                builder.setItems(R.array.long_library, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) { //open the book
                            Intent intent = new Intent(context, ReadingActivity.class);
                            intent.putExtra("bookId", id);
                            context.startActivity(intent);
                        } else if (which == 1){
                            Intent intent = new Intent(context, BookActivity.class);
                            intent.putExtra("bookId", id);
                            context.startActivity(intent);
                        } else if (which == 2){
                            new AlertDialog.Builder(context)
                                    .setTitle(title)
                                    .setMessage("Are you sure you want to remove this book from the library?")
                                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            BooksLDH databaseHelper = new BooksLDH(context);
                                            databaseHelper.removeBook(id);
                                            idcanc = id;
                                            new DeactivateBook().execute();
                                            Intent intent = new Intent(context, LibraryActivity.class);
                                            context.startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }
                    }
                });
                builder.show();
                return false;
            }
        });
    }

    /**
     * Background Async Task to deactivate a book
     * */
    private class DeactivateBook extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

            String url_deactivate_book = "http://api.gcappslab.com/deactivate_book.php";
            JSONParser jsonParser = new JSONParser();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_BOOKID, Integer.toString(idcanc)));
            params.add(new BasicNameValuePair(TAG_USERID, Integer.toString(userid)));
            //Log.i("deactivate ", Integer.toString(idcanc) + " " + Integer.toString(userid));

            jsonParser.makeHttpRequest(url_deactivate_book, "POST", params);
            return null;
        }

        protected void onPostExecute(String file_url) {
            ((LibraryActivity) context).finish();
        }
    }

}