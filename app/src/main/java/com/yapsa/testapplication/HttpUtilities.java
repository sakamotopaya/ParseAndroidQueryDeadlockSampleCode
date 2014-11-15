package com.yapsa.testapplication;

import android.util.Log;

import com.google.gson.Gson;
import com.parse.ParseUser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class HttpUtilities {

    private class TempTestData {
        public int testValue;
        public String testString;
    }

    private class DataWrapper {
        public ArrayList<TempTestData> results;
    }

    public static final String LOG_TAG = "http";

    private static String parseQueryUrl = "https://api.parse.com/1/classes/TestData";

    public static TestData getTestData(int index) {

        try {
            String url = parseQueryUrl + "?where=" + URLEncoder.encode("{\"testValue\": " + String.valueOf(index) + "}", "UTF-8");
            String result = get(url);

            if (result != null) {
                Gson gson = new Gson();
                try {
                    DataWrapper wrapper = gson.fromJson(result, DataWrapper.class);

                    if (wrapper != null && wrapper.results != null && wrapper.results.size() > 0) {
                        TestData data = new TestData();
                        data.setTestString(wrapper.results.get(0).testString);
                        data.setTestValue(wrapper.results.get(0).testValue);

                        return data;
                    }
                } catch (Exception e) {
                  Log.e(LOG_TAG, e.toString());
                }
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, e.toString());
        }

        return null;
    }

    public static String get(String url) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        httpGet.addHeader("X-Parse-Application-Id", "Y0rR34DK4FGfbahx8X0k2Wrvsw3Gsfi5tec1e1By");
        httpGet.addHeader("X-Parse-REST-API-Key", "CGpQmXtJSZVPx2lqaK7XK0Lwmrp1BTpNV2eBMBm2");
        httpGet.addHeader("X-Parse-Session-Token", ParseUser.getCurrentUser().getSessionToken());

        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            if (entity != null) {

                InputStream stream = entity.getContent();
                String result = convertStreamToString(stream);
                stream.close();

                return result;
            }


        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }

        return null;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
