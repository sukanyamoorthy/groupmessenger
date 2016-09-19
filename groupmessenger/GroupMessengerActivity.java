package edu.buffalo.cse.cse486586.groupmessenger1;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.widget.TextView;
import android.database.Cursor;


import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;


import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;


/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {
    static final String TAG = GroupMessengerActivity.class.getSimpleName();
    static int count=0;
    static final String[] REMOTE_PORT={"11108","11112","11116","11120","11124"};
    static final int SERVER_PORT = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));
        Log.e(TAG, "port" + myPort);

        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
             new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }


        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);


        tv.setMovementMethod(new ScrollingMovementMethod());
        final EditText editText = (EditText) findViewById(R.id.editText1);
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString() + "\n";
                editText.setText(""); // This is one way to reset the input box.

                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);

            }
        });



        
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }



/***
 * ServerTask is an AsyncTask that should handle incoming messages. It is created by
 * ServerTask.executeOnExecutor() call in SimpleMessengerActivity.
 *
 * Please make sure you understand how AsyncTask works by reading
 * http://developer.android.com/reference/android/os/AsyncTask.html
 *
 * @author stevko
 *
 */
private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

    @Override
    protected Void doInBackground(ServerSocket... sockets) {

        try {
            String inputLine;
        while(true) {


            ServerSocket serverSocket = sockets[0];
            Socket socket = serverSocket.accept();


            InputStreamReader in = new InputStreamReader
                    (socket.getInputStream());


            BufferedReader out = new BufferedReader(in);
            while ((inputLine = out.readLine()) != null) {


                Log.e(TAG, "server i/p" + inputLine);

                publishProgress(inputLine);
            }
        }
           // out.close();

            //in.close();
            //socket.close();
            //serverSocket.close();
        }





            /*
             * TODO: Fill in your server code that receives messages and passes them
             * to onProgressUpdate
             * .
             *
             */


             catch (Exception e) {
                Log.e(TAG, "server side socket exception");

            }


            return null;


    }
    protected void onProgressUpdate(String...strings) {

            /*
             * The following code displays what is received in doInBackground().
             */
        String strReceived = strings[0].trim();
        TextView remoteTextView = (TextView) findViewById(R.id.textView1);
        remoteTextView.append(strReceived + "\t\n");

        ContentValues keyValueToInsert = new ContentValues();
        Uri uri = providerUri("edu.buffalo.cse.cse486586.groupmessenger1.provider");
// inserting <”key-to-insert”, “value-to-insert”>
        keyValueToInsert.put("key", count++);
        keyValueToInsert.put("value", strReceived);

        Uri newUri = getContentResolver().insert(
                uri,    // assume we already created a Uri object with our provider URI
                keyValueToInsert
        );



        /*String key=keyValueToInsert.get("key").toString();

                   Cursor resultCursor = getContentResolver().query(
                            uri,    // assume we already created a Uri object with our provider URI
                            null,                // no need to support the projection parameter
                            key,  // we provide the key directly as the selection parameter
                            null,                // no need to support the selectionArgs parameter
                            null                 // no need to support the sortOrder parameter
                    );*/
      //  Log.v("op",resultCursor.getBlob(1).toString());

            /*
             * The following code creates a file in the AVD's internal storage and stores a file.
             *
             * For more information on file I/O on Android, please take a look at
             * http://developer.android.com/training/basics/data-storage/files.html
             */

        /*String filename = "SimpleMessengerOutput";
        String string = strReceived + "\n";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "File write failed");
        }*/

        return;
    }
}

    private Uri providerUri(String path){
        Uri.Builder builder = new Uri.Builder();
        builder.authority(path);
        builder.scheme("content");

        return builder.build();

    }
/***
 * ClientTask is an AsyncTask that should send a string over the network.
 * It is created by ClientTask.executeOnExecutor() call whenever OnKeyListener.onKey() detects
 * an enter key press event.
 *
 * @author stevko
 *
 */
private class ClientTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... msgs) {
        try {
            //  Log.e(TAG,"msg "+msgs[0]);
            int i=0;
            int len = REMOTE_PORT.length;
            while(i<len) {
               /* String remotePort = REMOTE_PORT0;
                if (msgs[1].equals(REMOTE_PORT0))
                    remotePort = REMOTE_PORT1;*/

                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        Integer.parseInt(REMOTE_PORT[i]));

                PrintStream out =
                        new PrintStream(socket.getOutputStream(), true);

                DataInputStream in =
                        new DataInputStream(
                                (socket.getInputStream()));


                String msgToSend = msgs[0];


                Log.e(TAG, "client " + msgToSend);


                out.print(msgToSend);

                /*
                 * TODO: Fill in your client code that sends out a message.
                 */

                out.close();
                in.close();

                socket.close();
                i++;
            }
        } catch (UnknownHostException e) {
            Log.e(TAG, "ClientTask UnknownHostException");
        } catch (IOException e) {
            Log.e(TAG, "ClientTask socket IOException"+e);
        }


        return null;
    }
}
}
