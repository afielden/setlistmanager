package com.bigdrum.setlistmanager.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import com.bigdrum.setlistmanager.MainActivity2;
import com.bigdrum.setlistmanager.R;

import org.junit.Before;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

/**
 * Created by andrew on 07/08/15.
 */

@LargeTest
public class NavigationTests extends ActivityInstrumentationTestCase2<MainActivity2> {

    private MainActivity2 mActivity;

    public NavigationTests() {
        super(MainActivity2.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
    }


    public void testViewAllTabs() {
        onView(withId(R.id.set_list_layout)).check(matches(isDisplayed()));
        swipeLeft();
        swipeLeft();
        swipeLeft();
        swipeRight();
        swipeRight();
        swipeRight();
    }
}