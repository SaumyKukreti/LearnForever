package com.saumykukreti.learnforever.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.fragments.HomeFragment;

public class CategoryActivity extends AppCompatActivity implements HomeFragment.OnHomeFragmentInteractionListener {

    public static final String METADATA_CATEGORY = "metadata_category_name";
    private String mCategoryName = null;
    private String TAG = CategoryActivity.class.getSimpleName();
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        getCategoryName();
        initialiseToolbar();

        if(mCategoryName!=null) {
            setHomeFragment();
        }
        else{
            Log.e(TAG, "Category needs to be passed in order for this activity to work");
        }
    }

    /**
     * Thie method sets the toolbar
     */
    private void initialiseToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(mCategoryName);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     *  This method gets category name
     */
    private void getCategoryName() {
        if(getIntent().hasExtra(METADATA_CATEGORY)){
            mCategoryName = getIntent().getStringExtra(METADATA_CATEGORY);
        }
    }

    /**
     * This method sets the home fragment on container
     */
    private void setHomeFragment() {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        HomeFragment fragment = HomeFragment.newInstance(mCategoryName);
        fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void updateActionBarForHomeFragment() {

    }

    @Override
    public void toggleFabVisibility(boolean on) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_for_home_fragment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() ==  android.R.id.home){
            onBackPressed();
            return true;
        }
        return false;
    }
}
