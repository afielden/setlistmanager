package com.bigdrum.setlistmanager.test;

import android.support.test.espresso.action.ViewActions;
import android.test.ActivityInstrumentationTestCase2;

import com.bigdrum.setlistmanager.MainActivity2;
import com.bigdrum.setlistmanager.R;

import org.junit.Before;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Created by andrew on 10/09/15.
 */
public class Setlist extends ActivityInstrumentationTestCase2<MainActivity2> {

    private MainActivity2 mActivity;
    private static final String TEST_SETLIST="testSetList";

    public Setlist() {
        super(MainActivity2.class);
    }


    @Before
    public void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
    }


    /**
     * Add a new setlist, then cancel the operation
     */
    public void testAddSetlistCancel() {
        onView(withId(R.id.set_list_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.action_add)).perform(click());
        onView(withId(R.id.action_cancel)).perform(click());
        onView(withId(R.id.set_list_layout)).check(matches(isDisplayed()));
    }


    /**
     * Add a new setlist
     */
    public void testAddSetlist() {
//        onView(withId(R.id.action_add)).perform(click());
//        onView(withId(R.id.set_list_name)).perform(typeText(TEST_SETLIST));
//        onView(withId(R.id.action_ok)).perform(click());
//        onView(withId(R.id.set_list_layout)).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.setlist_view)).atPosition(2).perform(click());
    }
}
