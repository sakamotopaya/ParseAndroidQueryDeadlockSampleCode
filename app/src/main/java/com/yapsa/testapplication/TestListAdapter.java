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

    public static final int FROM_LOCAL = 0;
    public static final int FROM_CACHE = 1;
    public static final int FROM_CLOUD = 2;
    public static final int FROM_ASYNC = 3;

    private ArrayList<TestModel> items;
    private Map<Integer, TestData> cacheData = Collections.synchronizedMap(new HashMap<Integer, TestData>());
    private boolean useDroidAsync = false;

    public TestListAdapter(Context context, int resource, ArrayList<TestModel> items, boolean useDroidAsync) {
        super(context, resource, items);

        this.useDroidAsync = useDroidAsync;
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
        tempHolder.itemText.setText(getDescription(object, object.itemText, null, tempHolder, FROM_LOCAL, null));

        if (!cacheData.containsKey(object.itemId)) {

            if (useDroidAsync) {
                if (tempHolder.asyncTask != null) {
                    tempHolder.query = null;
                    tempHolder.asyncTask.cancel(true);
                    tempHolder.asyncTask = null;
                }
            } else {
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
            }



            if (useDroidAsync) {
                AsyncTask task = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        //try {
                            //Thread.sleep(1000, 0);

                            final TestData model = HttpUtilities.getTestData(object.itemId);

                            //final TestData model = new TestData();
                            //model.setTestString("From async test");
                            //model.setTestValue(object.itemId);

                            cacheData.put(model.getTestValue(), model);

                            tempHolder.itemText.post(new Runnable() {
                                @Override
                                public void run() {
                                    tempHolder.itemText.setText(getDescription(object, model.getTestString(), model, tempHolder, FROM_ASYNC, null));
                                }
                            });
                            tempHolder.asyncTask = null;

                        //} catch (InterruptedException e) {
                        //    e.printStackTrace();
                        //}

                        tempHolder.query = null;
                        tempHolder.asyncTask = null;

                        return null;
                    }
                };

                tempHolder.asyncTask = task;
                task.execute();

            } else {

                // Run a query to get the data
                final ParseQuery<TestData> query = ParseQuery.getQuery(TestData.class);
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
                                tempHolder.itemText.setText(getDescription(object, model.getTestString(), model, tempHolder, FROM_CLOUD, e));
                            }
                        } else
                            Log.e(TAG, e.getMessage(), e);

                        Thread.currentThread().interrupt();

                    }
                });
            }
        } else {
            TestData model = cacheData.get(object.itemId);
            tempHolder.itemText.setText(getDescription(object, model.getTestString(), model, tempHolder, FROM_CACHE, null));
        }

        return v;
    }

    public String getDescription(TestModel model, String description, TestData data, TestItemHolder holder, int source, Exception e) {
        String buf = "Source: ";

        if (source == FROM_LOCAL)
            buf += "Local\r\n";
        else if (source == FROM_CLOUD)
            buf += "Cloud\r\n";
        else if (source == FROM_ASYNC)
            buf += "Async\r\n";
        else if (source == FROM_CACHE)
            buf += "Cache\r\n";

        buf += "Index: ";
        buf += String.valueOf(model.itemId);
        buf += "\r\n";

        buf += "Data: ";
        buf += description;
        buf += "\r\n";

        buf += "Row reuse count: ";
        buf += holder.reuseCount;
        buf += "\r\n";

        if (source == FROM_CLOUD && data != null) {
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

