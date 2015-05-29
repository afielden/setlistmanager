package com.bigdrum.metronomemate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bigdrum.metronomemate.gig.GigManagementFragment;
import com.bigdrum.metronomemate.info.AboutFragment;
import com.bigdrum.metronomemate.prefs.Prefs;

import com.bigdrum.metronomemate.slidingtabview.SlidingTabLayout;
import com.bigdrum.metronomemate.slidingtabview.SlidingTabsBasicFragment;
import com.bigdrum.metronomemate.ui.setlistmanagement.MetronomeFragment;
import com.bigdrum.metronomemate.venue.VenueManagementFragment;

import java.util.Locale;

public class MainActivity2 extends FragmentActivity {

    private MyPagerAdapter adapterViewPager;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        // Give the SlidingTabLayout the ViewPager
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        // Center the tabs in the layout
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(vpPager);
        slidingTabLayout.setOnPageChangeListener(adapterViewPager);
//        slidingTabLayout.setSelectedIndicatorColors(android.R.color.holo_blue_light);
    }


    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }


    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_settings:
                Intent i = new Intent(this, Prefs.class);
                startActivity(i);
                return true;

        }

        return false;
    }



    class MyPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        private MetronomeFragment metronomeFragment;

        /**
         *
         * @param fm
         */
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return 4;
        }



        // BEGIN_INCLUDE (pageradapter_getpagetitle)
        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {

            Locale l = Locale.getDefault();

            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
            }
            return null;
        }


        /**
         *
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            Bundle args = new Bundle();

            switch(position) {
                case 0:
                    metronomeFragment = new MetronomeFragment();

                    metronomeFragment.setArguments(args);
                    return metronomeFragment;
                case 1:
                    fragment = new GigManagementFragment();
                    fragment.setArguments(args);
                    return fragment;
                case 2:
                    fragment = new VenueManagementFragment();
                    fragment.setArguments(args);
                    return fragment;
                case 3:
                    fragment = new AboutFragment();
                    fragment.setArguments(args);
                    return fragment;
                default:
                    return null;
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                metronomeFragment.setEditMode(false);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
