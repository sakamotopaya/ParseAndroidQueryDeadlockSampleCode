package com.yapsa.testapplication;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName(TestData.TEST_DATA)
public class TestData extends ParseObject {
    public static final String TEST_DATA = "TestData";

    public static final String Field_TestValue = "testValue";
    public static final String Field_TestString = "testString";

    public int getTestValue() { return getInt(Field_TestValue); }
    public void setTestValue(int value) { put(Field_TestValue, value); }

    public String getTestString() { return getString(Field_TestString); }
    public void setTestString(String value) { put(Field_TestString, value); }
}
