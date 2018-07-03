package com.gcappslab.bookipedia.Login;

import android.content.Context;
import android.util.Log;

import com.gcappslab.bookipedia.Library.Objects.User;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Singleton class to read and save the user on the data text file
 */

public class UserFileManager {

    private static final String filename = "userdata.txt";
    private User user;
    private static UserFileManager instance;
    private Gson gson;

    private UserFileManager (Context context){
        gson = new Gson();
        readFromFile(context);
    }

    public static UserFileManager getInstance(Context context){
        if (instance == null){
            instance = new UserFileManager(context);
        }
        return instance;
    }

    private void writeToFile(Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(gson.toJson(user));
            outputStreamWriter.close();
            //Log.i("Write", "File written");
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void readFromFile(Context context) {

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String json = bufferedReader.readLine();
                if((json==null)||(json.isEmpty())){
                    user = null;
                } else {
                    user = gson.fromJson(json, User.class);
                }

                inputStream.close();
                //Log.i("Read", "File read");
            }
        } catch (FileNotFoundException e) {
            Log.e("file manager", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("file manager", "Can not read file: " + e.toString());
        }
    }

    public void saveUser (Context context, User user){
        this.user = user;
        writeToFile(context);
    }

    public User getUser (){
        return user;
    }

    public void deleteEverything (Context context){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write("");
            outputStreamWriter.close();
            user = null;
            //Log.i("Delete", "File empty");
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
