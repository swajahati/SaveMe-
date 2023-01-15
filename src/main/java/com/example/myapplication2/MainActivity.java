package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.Intent;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.database.Cursor;
import android.net.Uri;


public class MainActivity extends AppCompatActivity {
    public Button button;
    public ImageView imageView;
    public EditText phoneNumber;
    public EditText fullname;
    public static final String TEXT = "text";
    private static final int RESULT_PICK_CONTACT =1;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONENUMBER = "phonenumber";
    private String text;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String contactNumber= null;
        fullname = findViewById(R.id.etName);
        phoneNumber= findViewById(R.id.phoneNumber);
        button = (Button) findViewById(R.id.save);
        imageView = (ImageView) findViewById(R.id.imageView);
        sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
            String storedDetails = sharedPreferences.getString(KEY_PHONENUMBER,null);

        if (storedDetails !=null)
        {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        }


        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent in = new Intent (Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    startActivityForResult(in, RESULT_PICK_CONTACT );
            }
        });


        Spinner relationshipSpinner = (Spinner) findViewById(R.id.relationship);
        ArrayAdapter<String> relationshipAdapter= new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.relationships));
        relationshipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        relationshipSpinner.setAdapter(relationshipAdapter);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                   saveData();
                   Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                   startActivity(intent);
               }

        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, videoPlayer.class);
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if(resultCode==RESULT_OK)
        {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked (data);
                    break;
            }
        }
        else
        {
            Toast.makeText (this, "Failed To pick contact", Toast.LENGTH_SHORT).show ();
        }
    }
    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null;
            String contactName = null;
            Uri uri = data.getData ();
            cursor = getContentResolver ().query (uri, null, null,null,null);
            cursor.moveToFirst ();
            int phoneIndex = cursor.getColumnIndex (ContactsContract.CommonDataKinds.Phone.NUMBER);
            int contactNames= cursor.getColumnIndex (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString (phoneIndex);
            contactName = cursor.getString(contactNames).toString();
            fullname.setText(contactName);
            phoneNumber.setText (phoneNo);

        } catch (Exception e) {
            e.printStackTrace ();
        }
    }



    public void saveData()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME,fullname.getText().toString());
        editor.putString(KEY_PHONENUMBER,phoneNumber.getText().toString());
        editor.apply();
        Toast.makeText(this, "Emergency Contact Saved", Toast.LENGTH_SHORT).show();
    }
}