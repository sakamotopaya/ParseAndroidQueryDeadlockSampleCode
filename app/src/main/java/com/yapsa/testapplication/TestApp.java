package com.yapsa.testapplication;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by sakamoto on 11/2/14.
 */
public class TestApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(TestData.class);

        //Parse.initialize(this, "YOUR_APP_ID_HERE", "YOUR_CLIENT_KEY_HERE");
        Parse.initialize(this, "Y0rR34DK4FGfbahx8X0k2Wrvsw3Gsfi5tec1e1By", "0LTrsxyuISXUAUBIWLLrPRgKwSXLRw2KuJAZQgN2");

        ParseUser.enableAutomaticUser();
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
    }
}
