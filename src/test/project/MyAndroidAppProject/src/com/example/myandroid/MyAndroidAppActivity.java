package com.example.myandroid;

import android.app.Activity;
import android.os.Bundle;
import novoda.lib.httpservice.HttpService;
import android.widget.Toast;


public class MyAndroidAppActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(novoda.rest.R.layout.main);
	Toast.makeText(this, getString( novoda.lib.sqliteprovider.R.string.hello_world), 1000*30).show();
    }
}
