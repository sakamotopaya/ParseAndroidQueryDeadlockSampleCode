package com.yapsa.testapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;


public class MainActivity extends Activity implements ILog {

    public static String USEDROIDASYNC = "USEDROIDASYNC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        log = (TextView) findViewById(R.id.dev_log);
        log.setMovementMethod(new ScrollingMovementMethod());

        Button createTestData = (Button) findViewById(R.id.dev_create_test_data);
        createTestData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.setText("");
                DeveloperUtilities utility = new DeveloperUtilities(MainActivity.this);
                utility.createTestObjects();
            }
        });

        Button asyncTest = (Button) findViewById(R.id.dev_test_async);
        asyncTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.setText("");
                DeveloperUtilities utility = new DeveloperUtilities(MainActivity.this);
                utility.runAsyncTest();
            }
        });

        Button testList = (Button) findViewById(R.id.dev_test_list);
        testList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.setText("");
                Intent intent = new Intent(MainActivity.this, TestListActivity.class);
                intent.putExtra(USEDROIDASYNC, false);
                startActivity(intent);
            }
        });

        Button droidAyncTestList = (Button) findViewById(R.id.dev_test_droidasync);
        droidAyncTestList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.setText("");
                Bundle b = new Bundle();
                Intent intent = new Intent(MainActivity.this, TestListActivity.class);
                intent.putExtra(USEDROIDASYNC, true);
                startActivity(intent);
            }
        });

        Button parseAdapterTest = (Button) findViewById(R.id.dev_parse_adapter);
        parseAdapterTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.setText("");
                Intent intent = new Intent(MainActivity.this, TestParseQueryAdapterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String json = "{\"Messages\":[],\"ResponseEntity\":[{\"Messages\":[],\"UserId\":1}]}";
        Gson gson = new Gson();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    TextView log;

    @Override
    public void log(final String text) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log.append(text);
                log.append("\r\n");
            }
        });
    }
}
