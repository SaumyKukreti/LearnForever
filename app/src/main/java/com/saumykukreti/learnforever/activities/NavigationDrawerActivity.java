package com.saumykukreti.learnforever.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

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

    final int MENU_FOR_HOME_FRAGMENT = R.menu.menu_for_home_fragment;
    final int MENU_FOR_CATEGORIES_FRAGMENT = R.menu.menu_for_categories_fragment;
    final int MENU_FOR_REVISE_FRAGMENT = R.menu.menu_for_revise_fragment;
    final int MENU_FOR_SETTINGS_FRAGMENT = R.menu.menu_for_settings_fragment;


    //Fragments
    private static final String FRAGMENT_HOME = "FRAGMENT_HOME";
    private static final String FRAGMENT_CATEGORIES = "FRAGMENT_CATEGORIES";
    private static final String FRAGMENT_REVISE_ = "FRAGMENT_REVISE";
    private static final String FRAGMENT_SETTINGS = "FRAGMENT_SETTINGS";
    private int mCurrentItemId;
    private NavigationView mNavigationView;
    private int mCurrentMenu;
    private FloatingActionButton mFab;
    private Fragment mCurrentFragment;


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

        mCurrentFragment = null;
        switch(fragmentName){
            case FRAGMENT_HOME:
                mNavigationView.setCheckedItem(R.id.nav_home);
                mCurrentItemId = R.id.nav_home;
                mCurrentFragment = createHomeFragment();
                break;
            case FRAGMENT_CATEGORIES:
                mNavigationView.setCheckedItem(R.id.nav_categories);
                mCurrentItemId = R.id.nav_categories;
                mCurrentFragment = createCategoriesFragment();
                break;
            case FRAGMENT_REVISE_:
                mNavigationView.setCheckedItem(R.id.nav_revise);
                mCurrentItemId = R.id.nav_revise;
                mCurrentFragment = createReviseFragment();
                break;
            case FRAGMENT_SETTINGS:
                mNavigationView.setCheckedItem(R.id.nav_settings);
                mCurrentItemId = R.id.nav_settings;
                mCurrentFragment = createSettingsFragment();
                break;
        }

        //If fragment is null then do not replace else replace
        if(mCurrentFragment !=null){
            fragmentTransaction.replace(R.id.navigation_drawer_fragment_container, mCurrentFragment).commit();
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
        mToolbar.setTitle("Learn Forever");
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
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NavigationDrawerActivity.this, NoteActivity.class));
            }
        });

        mFab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(mCurrentFragment instanceof HomeFragment){
                    ((HomeFragment)mCurrentFragment).onNavigationFabLongClick();
                }
                return true;
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

                creteAndLoadFragment(FRAGMENT_HOME);
            }
            else {
                //This means it should be home_fragment
                
                if(mCurrentFragment instanceof HomeFragment){
                    if(!handleBackPressForHomeFragment()){
                        super.onBackPressed();
                    }
                }
            }
        }
    }

    /**
     *  This method handles back press for home fragment
     *
     *  Returns true if back press is handled else returns false
     */
    private boolean handleBackPressForHomeFragment() {
        if(((HomeFragment)mCurrentFragment).getSelectionMode()){
            ((HomeFragment)mCurrentFragment).setSelectionMode(false);
            ((HomeFragment)mCurrentFragment).initialiseNotesAdapter(false);
            return true;
        }
        else{
            return false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(mCurrentMenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                creteAndLoadFragment(FRAGMENT_SETTINGS);
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

    @Override
    public void updateActionBarForHomeFragment() {
        mToolbar.setTitle("Learn Forever");
        mCurrentMenu = MENU_FOR_HOME_FRAGMENT;
        invalidateOptionsMenu();
    }

    @Override
    public void updateActionBarForCategoriesFragment() {
        mToolbar.setTitle("Categories");
        mCurrentMenu = MENU_FOR_CATEGORIES_FRAGMENT;
        invalidateOptionsMenu();
    }


    @Override
    public void updateActionBarForReviseFragment() {
        mToolbar.setTitle("Revise");
        mCurrentMenu = MENU_FOR_REVISE_FRAGMENT;
        invalidateOptionsMenu();
    }


    @Override
    public void updateActionBarForSettingsFragment() {
        mToolbar.setTitle("Settings");
        mCurrentMenu = MENU_FOR_SETTINGS_FRAGMENT;
        invalidateOptionsMenu();
    }

    @Override
    public void toggleFabVisibility(boolean on) {
        if(on){
            mFab.setVisibility(View.VISIBLE);
        }
        else{
            mFab.setVisibility(View.GONE);
        }
    }
}
