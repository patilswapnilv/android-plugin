package com.example.myandroid;

import android.app.Activity;
import android.os.Bundle;
import novoda.lib.httpservice.HttpService;

public class MyAndroidAppActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(novoda.rest.R.layout.main);
        getString( novoda.lib.sqliteprovider.R.string.hello_world);
    }
}
