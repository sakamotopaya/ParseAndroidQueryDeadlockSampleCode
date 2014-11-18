package com.yapsa.testapplication;

import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseQuery;

public class TestItemHolder {
    public AsyncTask asyncTask;
    public int reuseCount = 0;
    public TextView itemText;
    public ParseQuery query;
    TestData data;

    public TestItemHolder(View v) {
        itemText = (TextView) v.findViewById(R.id.item_text);
    }
}
