package com.yapsa.testapplication;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseQueryAdapter;

public class ParseTestDataQueryAdapter extends ParseQueryAdapter<TestData> {
    public static String TAG = ParseTestDataQueryAdapter.class.getName();

    public ParseTestDataQueryAdapter(Context context, ParseQueryAdapter.QueryFactory<TestData> queryFactory) {
        super(context, queryFactory);
        setObjectsPerPage(5);
        setAutoload(true);
    }

    @Override
    public View getItemView(TestData object, View v, ViewGroup parent) {
        if (v == null) {
            Log.d(TAG, "view was created");
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.test_list_item, parent, false);
        }

        TestItemHolder holder = (TestItemHolder) v.getTag();

        if (holder == null) {
            holder = new TestItemHolder(v);
            v.setTag(holder);
        } else
            holder.reuseCount++;

        final TestItemHolder tempHolder = holder;
        tempHolder.itemText.setText(getDescription(object, object.getTestString(), null, tempHolder, TestListAdapter.FROM_CLOUD, null));


        return v;
    }

    public String getDescription(TestData model, String description, TestData data, TestItemHolder holder, int source, Exception e) {
        String buf = "Source: ";

        if (source == TestListAdapter.FROM_LOCAL)
            buf += "Local\r\n";
        else if (source == TestListAdapter.FROM_CLOUD)
            buf += "Cloud\r\n";
        else if (source == TestListAdapter.FROM_ASYNC)
            buf += "Async\r\n";
        else if (source == TestListAdapter.FROM_CACHE)
            buf += "Cache\r\n";

        buf += "Index: ";
        buf += String.valueOf(model.getTestValue());
        buf += "\r\n";

        buf += "Data: ";
        buf += description;
        buf += "\r\n";

        buf += "Row reuse count: ";
        buf += holder.reuseCount;
        buf += "\r\n";

        if (source == TestListAdapter.FROM_CLOUD && data != null) {
            if (e == null)
                buf += "Query completed successfully\r\n";
            else {
                buf += "Query exception: ";
                buf += e.getMessage();
                buf += "\r\n";
            }

        }

        return buf;
    }
}