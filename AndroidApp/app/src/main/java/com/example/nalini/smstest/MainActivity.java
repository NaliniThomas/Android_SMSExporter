package com.example.nalini.smstest;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private DBHelper mydb ;
    EditText receiver_id,sender_id,password;
    Button save_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshSmsInbox();

        Button save_btn=(Button)findViewById(R.id.id_save);
        receiver_id =(EditText)findViewById(R.id.id_receiver);
        sender_id =(EditText)findViewById(R.id.id_sender);
        password = (EditText)findViewById(R.id.id_password);

        Button cancel_btn=(Button)findViewById(R.id.id_cancel);
        save_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String sender_email = sender_id.getText().toString();
                String receiver_email = receiver_id.getText().toString();
                String pass = password.getText().toString();

                //Toast.makeText(MainActivity.this,sender_email+receiver_email+pass,Toast.LENGTH_LONG).show();
                mydb = new DBHelper(MainActivity.this);

                if(mydb.insertContact(sender_email,pass,receiver_email)){
                    Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        do {
            String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
        } while (smsInboxCursor.moveToNext());
    }
}
