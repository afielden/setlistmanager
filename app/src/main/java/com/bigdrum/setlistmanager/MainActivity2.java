package com.bigdrum.setlistmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.bigdrum.setlistmanager.database.Constants;
import com.bigdrum.setlistmanager.gig.GigManagementFragment;
import com.bigdrum.setlistmanager.info.AboutFragment;
import com.bigdrum.setlistmanager.prefs.Prefs;

import com.bigdrum.setlistmanager.prefs.PrefsFragment;
import com.bigdrum.setlistmanager.slidingtabview.SlidingTabLayout;
import com.bigdrum.setlistmanager.ui.setlistmanagement.HelpDialogFragment;
import com.bigdrum.setlistmanager.ui.setlistmanagement.MetronomeFragment;
import com.bigdrum.setlistmanager.venue.VenueManagementFragment;

import java.util.Locale;

public class MainActivity2 extends FragmentActivity {

    private MyPagerAdapter adapterViewPager;
    private SlidingTabLayout slidingTabLayout;
    private final HelpDialogFragment helpDialogFragment = new HelpDialogFragment();

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
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
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


    /**
     *
     * @return
     */
    public HelpDialogFragment getHelpDialogFragment() {
        return helpDialogFragment;
    }


    /**
     *
     */
    class MyPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        private MetronomeFragment metronomeFragment;
        private GigManagementFragment gigFragment;
        private VenueManagementFragment venueFragment;

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
                    if (metronomeFragment != null && !metronomeFragment.isSetlistMode()) {
                        return getString(R.string.title_songs).toUpperCase(l);
                    }
                    else if (metronomeFragment != null && metronomeFragment.isSetlistMode()){
                        return getString(R.string.title_setlists).toUpperCase(l);
                    }
                    else {
                        return getString(R.string.title_setlists).toUpperCase(l);
                    }
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
                    gigFragment = new GigManagementFragment();
                    gigFragment.setArguments(args);
                    return gigFragment;
                case 2:
                    venueFragment = new VenueManagementFragment();
                    venueFragment.setArguments(args);
                    return venueFragment;
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
            switch (position) {
                case Constants.SONG_SETLIST_TAB_INDEX:
                    if (metronomeFragment != null) {
                        metronomeFragment.setEditMode(false);
                        metronomeFragment.setlistMode(true);
                    }

                    helpDialogFragment.setCurrentlyDisplayedTab(Constants.SONG_SETLIST_TAB_INDEX);
                    slidingTabLayout.setTabTitle(0, getResources().getString(R.string.title_setlists).toUpperCase());
                    break;

                case Constants.GIG_TAB_INDEX:
                    if (gigFragment != null) {
                        gigFragment.unSelectGig();
                    }

                    helpDialogFragment.setCurrentlyDisplayedTab(Constants.GIG_TAB_INDEX);
                    break;

                case Constants.VENUE_TAB_INDEX:
                    if (venueFragment != null) {
                        venueFragment.unselectVenue();
                    }

                    helpDialogFragment.setCurrentlyDisplayedTab(Constants.VENUE_TAB_INDEX);
                    break;

                case Constants.ABOUT_TAB_INDEX:
                    helpDialogFragment.setCurrentlyDisplayedTab(Constants.ABOUT_TAB_INDEX);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
