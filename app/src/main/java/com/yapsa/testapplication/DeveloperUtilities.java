package com.yapsa.testapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeveloperUtilities {

    public static final String LOG_TAG = "DEV";
    private ILog logger;

    public DeveloperUtilities(ILog logger){
        this.logger = logger;
    }

    private static int TestObjectCount = 200;
    private static int QueryCount = 5;

    public void createTestObjects() {

        for (int i = 0; i < 200; i++){
            TestData data = new TestData();
            data.setTestValue(i + 1);
            data.setTestString("This is a test string " + String.valueOf(1+1));
            try {
                logger.log("Creating test object " + String.valueOf(i));
                data.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private ParseQuery<TestData> getQuery(Integer queryCount) {
        ParseQuery<TestData> query = ParseQuery.getQuery(TestData.class);
        //query.whereLessThan(TestData.Field_TestValue, queryCount + 10);
        //query.whereGreaterThan(TestData.Field_TestValue, 0);
        return query;
    }

    public void runAsyncTest() {

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {

                final List<ParseQuery<TestData>> queryIndex = Collections.synchronizedList(new ArrayList<ParseQuery<TestData>>());

                try {
                    logger.log("Running 100 queries");

                    // We are going to run 100 queries
                    for (int i = 0; i < 100; i++) {

                        // If there are more that 5 outstanding queries, kill the oldest one
                        if (queryIndex.size() >= 5) {
                            logger.log("Removing and canceling a query...");
                            ParseQuery<TestData> tempQuery = queryIndex.remove(0);
                            tempQuery.cancel();
                        }

                        final ParseQuery<TestData> query = getQuery(i + 50);
                        queryIndex.add(query);

                        final int queryNumber = i;
                        logger.log("Running query " + String.valueOf(queryNumber));
                        query.findInBackground(new FindCallback<TestData>() {
                            @Override
                            public void done(List<TestData> testData, ParseException e) {

                                if (e == null) {
                                    logger.log("Query " + String.valueOf(queryNumber) + "completed successfully.");
                                    queryIndex.remove(query);
                                } else
                                    logger.log(e.toString());
                            }
                        });

                        try {
                            Thread.sleep(100, 0);
                        } catch (InterruptedException e) {
                            logger.log(e.toString());
                        }
                    }

                    logger.log("Query run complete");
                } catch (Exception e) {
                    logger.log(e.toString());
                    logger.log("Exception caused test to stop");
                }
                return null;
            }
        };

        task.execute();
    }




}

