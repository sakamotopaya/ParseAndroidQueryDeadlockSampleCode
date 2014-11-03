package com.yapsa.testapplication;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName(TestData.TEST_DATA)
public class TestData extends ParseObject {
    public static final String TEST_DATA = "TestData";

    public static final String Field_TestValue = "testValue";

    public int getTestValue() { return getInt(Field_TestValue); }
    public void setTestValue(int value) { put(Field_TestValue, value); }
}
