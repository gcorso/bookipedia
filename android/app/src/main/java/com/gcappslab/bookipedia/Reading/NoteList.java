package com.gcappslab.bookipedia.Reading;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.gcappslab.bookipedia.Library.Databases.BooksLDH;
import com.gcappslab.bookipedia.Library.Databases.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_BOOKID;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_NOTES;
import static com.gcappslab.bookipedia.Library.Objects.Constants.TAG_USERID;
import static com.gcappslab.bookipedia.Library.Objects.Constants.URL_API;

/**
 * Class that defines a List of Notes
 */

public class NoteList {

    private ArrayList<Note> general = new ArrayList<Note>();
    private ArrayList<Note> highlights = new ArrayList<Note>();
    private int bookid;
    private int userid;
    private String notesstr;
    private BooksLDH booksLDH;
    private static final String NOTES_SEPARATOR = "<<-->>";
    private static final String PARTS_SEPARATOR = "<->";
    private static final String url_update_notes = URL_API+"update_notes.php";
    JSONParser jsonParser = new JSONParser();

    public NoteList(String notes, int bookid, int userid, Context context) {
        booksLDH = new BooksLDH(context);
        this.bookid = bookid;
        this.userid = userid;
        if (notes.length()>0){
            List<String> strlist = Arrays.asList(notes.split(NOTES_SEPARATOR));
            for (String s : strlist){
                Log.i("Note", s);
                String[] parts = s.split(PARTS_SEPARATOR);
                if(parts.length>=3){
                    Note note = new Note(Integer.valueOf(parts[0]), parts[1], parts[2].charAt(0));
                    general.add(note);
                    if (note.getType()=='H') {
                        highlights.add(note);
                    }
                }
            }
            Collections.sort(general, null);
            Collections.sort(highlights, null);
        }
    }

    // inserts a node
    public void add(Note note) {
        general.add(note);
        Collections.sort(general, null);

        if (note.getType()=='H'){
            highlights.add(note);
            Collections.sort(highlights, null);
        }
        update();
    }

    // returns the note in the specified position
    public Note get(int i) {
        return general.get(i);
    }

    // removes a note
    public void remove(int i){
        Note note = general.get(i);
        if (note.getType()=='H'){
            highlights.remove(note);
        }
        general.remove(i);
        update();
    }

    public int size() {
        return general.size();
    }

    // returns all the highights in a determined interval (current page)
    public List<Note> highlightsInInterval(int begin, int end){
        int first;

        //Binary search on first
        int b = 0;
        int e = highlights.size()-1;
        while (b<=e){
            int current = (b+e)/2;
            if (highlights.get(current).getEnd() >= begin){
                e = current - 1;
            } else {
                b = current + 1;
            }
        }
        first = e+1;

        int last;

        //Binary search on last
        b = 0;
        e = highlights.size()-1;
        while (b<=e){
            int current = (b+e)/2;
            if (highlights.get(current).getPos() <= end){
                b = current + 1;
            } else {
                e = current-1;
            }
        }
        last = b-1;

        return highlights.subList(first, last+1);
    }

    // updates the node field both on the local and server database
    private void update(){
        String notes = "";
        for (Note note : general){
            String single = Integer.toString(note.getPos()) + PARTS_SEPARATOR + note.getText()
                    + PARTS_SEPARATOR + Character.toString(note.getType()) + NOTES_SEPARATOR;
            notes += single;
        }
        notesstr = notes;
        booksLDH.updateNotes(bookid, notes);
        new UpdateNotes().execute();
    }

    /**
     * Background Async Task to update notes in the server database
     * */
    private class UpdateNotes extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_BOOKID, Integer.toString(bookid)));
            params.add(new BasicNameValuePair(TAG_USERID, Integer.toString(userid)));
            params.add(new BasicNameValuePair(TAG_NOTES, notesstr));

            jsonParser.makeHttpRequest(url_update_notes, "POST", params);
            return null;
        }
        protected void onPostExecute(String file_url) {}
    }
}