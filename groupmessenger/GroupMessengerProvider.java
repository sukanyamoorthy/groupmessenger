package edu.buffalo.cse.cse486586.groupmessenger1;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.database.AbstractCursor;


public class GroupMessengerProvider extends ContentProvider {

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
       
        String FILENAME = values.get("key").toString();
        Context context=getContext();

        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);

            fos.write(values.get("value").toString().getBytes());
            fos.close();

        } catch (IOException e) {
            Log.v(TAG, "IO exception");
        }
        Log.v("insert", values.toString());
        return uri;
    }

    @Override
    public boolean onCreate() {
        
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        
        return 0;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        String[] columnnames={"key","value"};
        String[] tmp=null;
        Context context=getContext();
        MatrixCursor mc=new  MatrixCursor(columnnames);

       
try {

    byte[] b = new byte[256];
    FileInputStream fis = context.openFileInput(selection);
    BufferedReader bf = new BufferedReader(new InputStreamReader(fis));
    String data =bf.readLine();
    String[] columnvalues={selection,data};
    tmp=columnvalues;
    mc.addRow(columnvalues);
}
catch(IOException io){
    Log.v("exception","IO");
}
        Log.v("query", tmp.toString());
        Log.v("query", mc.toString());
        return mc;
    }
}
