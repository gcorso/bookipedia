package com.gcappslab.bookipedia.Library.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;
import android.util.Pair;

import com.gcappslab.bookipedia.Library.Objects.Book;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * BooksLDH objects are used to create and manage connection with the local database
 *
 * Sometimes it uses ContentValues instead of a normal SQL statement to avoid errors due to the use
 * of characters not allowed in SQL statements (for example in notes and books the type of characters
 * cannot be controlled.
 */

public class BooksLDH {
    private static final int DATABASE_VERSION = 26;
    private static final String DATABASE_NAME = "books.db";
    private static final String TABLE_NAME_BOOKS = "books";
    private static final String BOOK_COLUMN_ID = "_id";
    private static final String BOOK_COLUMN_NAME = "Name";
    private static final String BOOK_COLUMN_AUTHORID = "AuthorId";
    private static final String BOOK_COLUMN_AUTHORNAME = "AuthorName";
    private static final String BOOK_COLUMN_YEAR = "Year";
    private static final String BOOK_COLUMN_DESCRIPTION = "Description";
    private static final String BOOK_COLUMN_FILE = "File";
    private static final String BOOK_COLUMN_GENRE = "Genre";
    private static final String BOOK_COLUMN_LANGUAGE = "Language";
    private static final String BOOK_COLUMN_OPENED = "LastVisit";
    private static final String BOOK_COLUMN_RATING = "Rating";
    private static final String BOOK_COLUMN_REVIEWS = "Reviews";
    private static final String BOOK_COLUMN_PROGRESS = "Progress";
    private static final String BOOK_COLUMN_NOTES = "Notes";
    private static final String BOOK_COLUMN_LENGTH = "Length";

    private static final String SQL_CREATE_TABLE_BOOK = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_BOOKS + "( "
            + BOOK_COLUMN_ID + " INTEGER PRIMARY KEY, "
            + BOOK_COLUMN_NAME + " VARCHAR(50), "
            + BOOK_COLUMN_AUTHORID + " INTEGER, "
            + BOOK_COLUMN_AUTHORNAME + " VARCHAR(30), "
            + BOOK_COLUMN_YEAR + " INTEGER, "
            + BOOK_COLUMN_DESCRIPTION + " TEXT, "
            + BOOK_COLUMN_FILE + " LONGTEXT, "
            + BOOK_COLUMN_OPENED + " INTEGER, "
            + BOOK_COLUMN_RATING + " DECIMAL(4,3), "
            + BOOK_COLUMN_REVIEWS + " INTEGER, "
            + BOOK_COLUMN_PROGRESS + " INTEGER, "
            + BOOK_COLUMN_LENGTH + " INTEGER, "
            + BOOK_COLUMN_NOTES + " LONGTEXT, "
            + BOOK_COLUMN_GENRE + " VARCHAR(20), "
            + BOOK_COLUMN_LANGUAGE + " VARCHAR(20) )";

    public static final int SORT_OPEN = 0;
    public static final int SORT_TITLE = 1;
    public static final int SORT_AUTHOR = 2;
    public static final int SORT_YEAR = 3;

    private BooksListOpenHelper openHelper;
    private SQLiteDatabase database;

    public BooksLDH(Context context){
        openHelper = new BooksListOpenHelper(context);
        database = openHelper.getWritableDatabase();
        database.execSQL(SQL_CREATE_TABLE_BOOK);
    }

    public BooksListOpenHelper getOpenHelper (Context context){
        openHelper = new BooksListOpenHelper(context);
        return openHelper;
    }

    public SQLiteDatabase getWritableDatabase (Context context){
        openHelper = new BooksListOpenHelper(context);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

    // saves a book in the database
    public void saveBook(Book book){
        ContentValues contentValues= new ContentValues();

        contentValues.put(BOOK_COLUMN_ID, book.getBookId());
        contentValues.put(BOOK_COLUMN_NAME, book.getName());
        contentValues.put(BOOK_COLUMN_AUTHORID, book.getAuthorId());
        contentValues.put(BOOK_COLUMN_AUTHORNAME, book.getAuthorName());
        contentValues.put(BOOK_COLUMN_YEAR, book.getYear());
        contentValues.put(BOOK_COLUMN_DESCRIPTION, book.getDescription());
        contentValues.put(BOOK_COLUMN_FILE, book.getFile());
        contentValues.put(BOOK_COLUMN_GENRE, book.getGenre());
        contentValues.put(BOOK_COLUMN_LANGUAGE, book.getLanguage());
        contentValues.put(BOOK_COLUMN_OPENED, Calendar.getInstance().getTime().toString());
        contentValues.put(BOOK_COLUMN_RATING, book.getRating());
        contentValues.put(BOOK_COLUMN_REVIEWS, book.getReviews());
        contentValues.put(BOOK_COLUMN_PROGRESS, book.getProgress());
        contentValues.put(BOOK_COLUMN_NOTES, book.getNotes());
        contentValues.put(BOOK_COLUMN_LENGTH, book.getFile().length());

        Time time = new Time();
        time.setToNow();
        contentValues.put(BOOK_COLUMN_OPENED, (int) time.toMillis(false));

        database.insert(TABLE_NAME_BOOKS, null, contentValues);
    }

    // returns a cursor of the database containing all the raws in the local book table
    public Cursor getBooksDisplayCursor(int orderID){
        openHelper.getWritableDatabase();
        String order = "";
        if (orderID == SORT_OPEN){
            order = BOOK_COLUMN_OPENED + " DESC";
        } else if (orderID == SORT_TITLE){
            order = BOOK_COLUMN_NAME;
        } else if (orderID == SORT_AUTHOR){
            order = BOOK_COLUMN_AUTHORNAME;
        } else if (orderID == SORT_YEAR){
            order = BOOK_COLUMN_YEAR;
        }
        return  database.rawQuery(
                "SELECT " + BOOK_COLUMN_ID + ", " + BOOK_COLUMN_NAME + ", " + BOOK_COLUMN_AUTHORNAME +
                        ", " + BOOK_COLUMN_PROGRESS + ", " + BOOK_COLUMN_LENGTH +
                        " FROM " + TABLE_NAME_BOOKS +
                        " ORDER BY " + order,
                null
        );
    }

    // returns a list with all the books (with just id, name and authorid) in the database
    public List<Book> getBooksDisplay(){
        openHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT " + BOOK_COLUMN_ID + ", " + BOOK_COLUMN_NAME + ", " + BOOK_COLUMN_AUTHORNAME
                        + " FROM " + TABLE_NAME_BOOKS + " ORDER BY " + BOOK_COLUMN_OPENED + " DESC",
                null
        );

        List<Book> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Book book = new Book(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            list.add(book);
            cursor.moveToNext();
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return list;
    }

    // returns a book item complete without the file for the BookActivity
    public Pair<Book, Boolean> getBookDescriptionById(int id){
        openHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(

                "SELECT " + BOOK_COLUMN_ID + ", " + BOOK_COLUMN_NAME + ", " + BOOK_COLUMN_AUTHORID + " , "
                        + BOOK_COLUMN_AUTHORNAME + ", " + BOOK_COLUMN_YEAR + ", "
                        + BOOK_COLUMN_GENRE + ", " + BOOK_COLUMN_LANGUAGE + ", "
                        + BOOK_COLUMN_DESCRIPTION + " , "
                        + BOOK_COLUMN_RATING + ", " + BOOK_COLUMN_REVIEWS
                        + " FROM " + TABLE_NAME_BOOKS
                        + " WHERE " + BOOK_COLUMN_ID + " = " + Integer.toString(id),
                null
        );
        cursor.moveToFirst();
        if (!(cursor.isAfterLast())) {
            Book book = new Book(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                    cursor.getString(3), cursor.getInt(4), cursor.getString(7),
                    cursor.getString(5), cursor.getString(6), cursor.getFloat(8), cursor.getInt(9));
            return new Pair<>(book, true);
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return new Pair<>(null, false);
    }

    // returns a book item complete for the ReadingActivity
    public Book getBookTextById(int id){
        openHelper.getWritableDatabase();
        String sql = "SELECT " + BOOK_COLUMN_ID + ", " + BOOK_COLUMN_NAME + ", " + BOOK_COLUMN_AUTHORID + " , "
                + BOOK_COLUMN_AUTHORNAME + ", " + BOOK_COLUMN_YEAR + ", "
                + BOOK_COLUMN_FILE + ", " + BOOK_COLUMN_PROGRESS + ", "
                + BOOK_COLUMN_NOTES
                + " FROM " + TABLE_NAME_BOOKS
                + " WHERE " + BOOK_COLUMN_ID + " = " + Integer.toString(id);

        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();

        Book book = new Book(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                cursor.getString(3), cursor.getInt(4), cursor.getString(5),
                cursor.getInt(6), cursor.getString(7));

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        Time time = new Time();
        time.setToNow();
        sql = "UPDATE " + TABLE_NAME_BOOKS
                + " SET " + BOOK_COLUMN_OPENED + " = " + Long.toString(time.toMillis(false))
                + " WHERE " + BOOK_COLUMN_ID + " = " + Integer.toString(id);
        database.execSQL(sql);

        return book;
    }

    // updates the notes of the book the user is reading
    public void updateNotes(int bookId, String notes){

        ContentValues contentValues= new ContentValues();
        contentValues.put(BOOK_COLUMN_NOTES, notes);
        database.update(TABLE_NAME_BOOKS, contentValues, BOOK_COLUMN_ID + " = " + Integer.toString(bookId), null);
    }

    // update the progress of the book the user is reading
    public void updateProgress(int bookId, int progress){

        String sql = "UPDATE " + TABLE_NAME_BOOKS
                + " SET " + BOOK_COLUMN_PROGRESS + " = " + Integer.toString(progress)
                + " WHERE " + BOOK_COLUMN_ID + " = " + Integer.toString(bookId);
        database.execSQL(sql);
    }

    // remove a book from the local database
    public  void removeBook(int bookId){
        String sql = "DELETE FROM " + TABLE_NAME_BOOKS
                + " WHERE " + BOOK_COLUMN_ID + " = " + Integer.toString(bookId);
        database.execSQL(sql);
    }

    // updates the rating of a book with the one the user just gave
    public void updateRating(int bookId, float rating){

        String sql = "UPDATE " + TABLE_NAME_BOOKS
                + " SET " + BOOK_COLUMN_RATING + " = " + Float.toString(rating) + ", "
                + BOOK_COLUMN_REVIEWS + " = " + Integer.toString(1000000)
                + " WHERE " + BOOK_COLUMN_ID + " = " + Integer.toString(bookId);
        database.execSQL(sql);
    }

    public void deleteEverything(){
        database.execSQL("DELETE FROM " + TABLE_NAME_BOOKS);
    }

    // class that interfaces the connection between the LDH and the database
    private class BooksListOpenHelper extends SQLiteOpenHelper {

        public BooksListOpenHelper(Context context) {
          super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {


            db.execSQL(SQL_CREATE_TABLE_BOOK);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
          db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_BOOKS);
          onCreate(db);
        }
    }
}
