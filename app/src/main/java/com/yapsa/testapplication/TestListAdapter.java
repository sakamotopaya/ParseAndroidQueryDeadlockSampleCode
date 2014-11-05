package com.yapsa.testapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestListAdapter extends ArrayAdapter<TestModel> {
    private static final String TAG = TestListAdapter.class.getSimpleName();

    private ArrayList<TestModel> items;
    private Map<Integer, TestData> cacheData = Collections.synchronizedMap(new HashMap<Integer, TestData>());

    public TestListAdapter(Context context, int resource, ArrayList<TestModel> items) {
        super(context, resource, items);
        init(items);
    }

    private void init(ArrayList<TestModel> items) {

        this.items = items;

    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public TestModel getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        long index = getItemId(position);
        final TestModel object = getItem(position);


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
        tempHolder.itemText.setText(getDescription(object, object.itemText, null, tempHolder, false, null));

        if (!cacheData.containsKey(object.itemId)) {

            if (tempHolder.query != null) {
                final ParseQuery tempQuery = tempHolder.query;
                tempHolder.query = null;
                Log.d(TAG, "cancelling query");

                AsyncTask task = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        tempQuery.cancel();
                        Log.d(TAG, "query canceled");
                        return null;
                    }
                };

                task.execute();

            }

            // Run a query to get the data
            ParseQuery<TestData> query = ParseQuery.getQuery(TestData.class);
            query.whereEqualTo(TestData.Field_TestValue, object.itemId);
            tempHolder.query = query;

            query.findInBackground(new FindCallback<TestData>() {
                @Override
                public void done(List<TestData> testDatas, ParseException e) {
                    tempHolder.query = null;
                    if (e == null) {
                        if (testDatas.size() > 0) {
                            TestData model = testDatas.get(0);
                            cacheData.put(model.getTestValue(), model);
                            tempHolder.itemText.setText(getDescription(object, model.getTestString(), model, tempHolder, false, e));
                        }
                    } else
                        Log.e(TAG, e.getMessage(), e);
                }
            });
        } else {
            TestData model = cacheData.get(object.itemId);
            tempHolder.itemText.setText(getDescription(object, model.getTestString(), model, tempHolder, true, null));
        }

        return v;
    }

    private String getDescription(TestModel model, String description, TestData data, TestItemHolder holder, boolean fromCache, Exception e) {
        String buf = "Source: ";

        if (data == null)
            buf += "Local\r\n";
        else
            buf += "Cloud\r\n";

        buf += "Index: ";
        buf += String.valueOf(model.itemId);
        buf += "\r\n";

        buf += "Data: ";
        buf += description;
        buf += "\r\n";

        buf += "Form cache: ";
        buf += fromCache ? "true" : "false";
        buf += "\r\n";

        buf += "Row reuse count: ";
        buf += holder.reuseCount;
        buf += "\r\n";

        if (!fromCache && data != null){
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

    class TestItemHolder {
        public int reuseCount = 0;
        public TextView itemText;
        public ParseQuery query;
        TestData data;

        public TestItemHolder(View v) {
            itemText = (TextView) v.findViewById(R.id.item_text);
        }
    }
}
