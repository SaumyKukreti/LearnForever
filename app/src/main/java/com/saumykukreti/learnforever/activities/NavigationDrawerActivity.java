package com.saumykukreti.learnforever.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.fragments.CategoriesFragment;
import com.saumykukreti.learnforever.fragments.HomeFragment;
import com.saumykukreti.learnforever.fragments.ReviseFragment;
import com.saumykukreti.learnforever.fragments.SettingsFragment;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnHomeFragmentInteractionListener,
        CategoriesFragment.OnCategoriesFragmentInteractionListener,
        ReviseFragment.OnReviseFragmentInteractionListener,
        SettingsFragment.OnSettingsFragmentInteractionListener{

    private Toolbar mToolbar;

    //Fragments
    private static final String FRAGMENT_HOME = "FRAGMENT_HOME";
    private static final String FRAGMENT_CATEGORIES = "FRAGMENT_CATEGORIES";
    private static final String FRAGMENT_REVISE_ = "FRAGMENT_REVISE";
    private static final String FRAGMENT_SETTINGS = "FRAGMENT_SETTINGS";
    private int mCurrentItemId;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        initialiseToolbar();
        initialiseFab();
        initialiseDrawer();

        //Load home fragment
        creteAndLoadFragment(FRAGMENT_HOME);
    }

    /**
     * Thie method loads up fragment
     * @param fragmentName -  Name of the fragment to be replaced
     */
    private void creteAndLoadFragment(String fragmentName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

        Fragment fragment = null;
        switch(fragmentName){
            case FRAGMENT_HOME:
                fragment = createHomeFragment();
                break;
            case FRAGMENT_CATEGORIES:
                fragment = createCategoriesFragment();
                break;
            case FRAGMENT_REVISE_:
                fragment = createReviseFragment();
                break;
            case FRAGMENT_SETTINGS:
                fragment = createSettingsFragment();
                break;
        }

        //If fragment is null then do not replace else replace
        if(fragment!=null){
            fragmentTransaction.replace(R.id.navigation_drawer_fragment_container,fragment).commit();
        }
    }

    private Fragment createHomeFragment() {
        HomeFragment fragment = HomeFragment.newInstance();
        return fragment;
    }

    private Fragment createCategoriesFragment() {
        CategoriesFragment fragment = CategoriesFragment.newInstance();
        return fragment;
    }

    private Fragment createReviseFragment() {
        ReviseFragment fragment = ReviseFragment.newInstance();
        return fragment;
    }

    private Fragment createSettingsFragment() {
        SettingsFragment fragment = SettingsFragment.newInstance();
        return fragment;
    }

    /**
     *  Thie method sets the toolbar
     */
    private void initialiseToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    /**
     * This method handles the navigation drawer
     */
    private void initialiseDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    /**
     *  This method initialises FAB
     */
    private void initialiseFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NavigationDrawerActivity.this, NoteActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //Check whihc fragment is open, if its a fragment other than home fragment then open home fragment
            if(mCurrentItemId==R.id.nav_categories ||
                    mCurrentItemId==R.id.nav_revise ||
                    mCurrentItemId==R.id.nav_settings){

                mNavigationView.setCheckedItem(R.id.nav_home);
                mCurrentItemId = R.id.nav_home;
                creteAndLoadFragment(FRAGMENT_HOME);
            }
            else {
                super.onBackPressed();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        mCurrentItemId = item.getItemId();

        switch (mCurrentItemId){
            case R.id.nav_home:
                creteAndLoadFragment(FRAGMENT_HOME);
                break;
            case R.id.nav_categories:
                creteAndLoadFragment(FRAGMENT_CATEGORIES);
                break;
            case R.id.nav_revise:
                creteAndLoadFragment(FRAGMENT_REVISE_);
                break;
            case R.id.nav_settings:
                creteAndLoadFragment(FRAGMENT_SETTINGS);
                break;
            case R.id.nav_send:
                break;
            case R.id.nav_share:
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
