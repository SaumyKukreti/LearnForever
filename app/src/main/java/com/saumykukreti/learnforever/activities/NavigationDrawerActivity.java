package com.saumykukreti.learnforever.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.saumykukreti.learnforever.R;
import com.saumykukreti.learnforever.events.InitializationCompleteEvent;
import com.saumykukreti.learnforever.events.LogoutCompleteEvent;
import com.saumykukreti.learnforever.fragments.CategoriesFragment;
import com.saumykukreti.learnforever.fragments.HomeFragment;
import com.saumykukreti.learnforever.fragments.ReviseFragment;
import com.saumykukreti.learnforever.fragments.SettingsFragment;
import com.saumykukreti.learnforever.fragments.TempFragment;
import com.saumykukreti.learnforever.util.Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.InputStream;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnHomeFragmentInteractionListener,
        CategoriesFragment.OnCategoriesFragmentInteractionListener,
        ReviseFragment.OnReviseFragmentInteractionListener,
        SettingsFragment.OnSettingsFragmentInteractionListener {

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
    private final String TAG = NavigationDrawerActivity.class.getSimpleName();
    private HomeFragment mHomeFragment;


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
     *
     * @param fragmentName -  Name of the fragment to be replaced
     */
    private void creteAndLoadFragment(String fragmentName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

        int lengthOfFragmentBackStack = getLengthOfFragmentStack(fragmentManager);

        if (fragmentName.equalsIgnoreCase(FRAGMENT_HOME) && lengthOfFragmentBackStack == 0) {
            mNavigationView.setCheckedItem(R.id.nav_home);
            mCurrentItemId = R.id.nav_home;
            mCurrentFragment = createHomeFragment();
            //Field to store home fragment
            mHomeFragment = (HomeFragment) mCurrentFragment;
            //As the length of fragment back stack is 0, this means no fragment has been added to the back stack till now, hence adding home fragment to the container
            fragmentTransaction.add(R.id.navigation_drawer_fragment_container, mCurrentFragment).commit();
            //Returning as no further action is required
            return;
        }

        //If home fragment is required for the second time
        if (fragmentName.equalsIgnoreCase(FRAGMENT_HOME) && lengthOfFragmentBackStack != 0) {
            //As the home fragment will be added and other fragments will be added over it till 1 level only, hence pop will show home fragment
            mNavigationView.setCheckedItem(R.id.nav_home);
            mCurrentItemId = R.id.nav_home;
            mCurrentFragment = createHomeFragment();
            fragmentManager.popBackStack();
            updateActionBarForHomeFragment();

            //Letting home fragment know if it needs to refresh
            mHomeFragment.refreshLayout();
            return;
        }

        //If some other fragment needs to get added
        if (!fragmentName.equalsIgnoreCase(FRAGMENT_HOME)) {
            //Check which is the current fragment, if it is home fragment then adding the new fragment on the top else first popping the previous fragment and then adding the new fragment
            //Letting the home fragment know it is in background
            mHomeFragment.goneInBackgroung();

            mCurrentFragment = null;
            switch (fragmentName) {
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

            //Checking if some other fragment is already added or not, if so first popping that and then adding a new item
            if (lengthOfFragmentBackStack != 0) {
                fragmentManager.popBackStack();
            }

            fragmentTransaction.add(R.id.navigation_drawer_fragment_container, mCurrentFragment).addToBackStack(null).commit();
        }
    }

    /**
     * Returns length of Fragment stack
     *
     * @param fragmentManager
     * @return
     */
    private int getLengthOfFragmentStack(FragmentManager fragmentManager) {
        return fragmentManager.getBackStackEntryCount();
    }

    private Fragment createHomeFragment() {
        HomeFragment fragment = HomeFragment.newInstance("");
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
     * Thie method sets the toolbar
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            TextView nameTV = mNavigationView.getHeaderView(0).findViewById(R.id.tv_name);
            TextView emailTV = mNavigationView.getHeaderView(0).findViewById(R.id.tv_email);
            final ImageView profileIM = mNavigationView.getHeaderView(0).findViewById(R.id.imageView_profile);

            nameTV.setText(user.getDisplayName());
            emailTV.setText(user.getEmail());

            //Applying saved profile picture (if any till a new one is loaded from internet)
            String str = Utility.getStringFromPreference(this,"profileImagePath");
            if(str.length()>0 && !str.equalsIgnoreCase("unsuccessful")) {
                Bitmap bit = BitmapFactory.decodeFile(str);
                profileIM.setImageBitmap(bit);
            }else{
                //Apply default image
                profileIM.setImageDrawable(getResources().getDrawable(R.drawable.learn_forever));
            }

            if (user.getPhotoUrl() != null && Utility.isNetworkAvailable(this)) {
                new DownloadImageTask(profileIM).execute(user.getPhotoUrl().toString());
            }
        }
    }

private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView profileImage;

    public DownloadImageTask(ImageView bmImage) {
        this.profileImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        //Saving bitmap to file
        profileImage.setImageBitmap(result);
        Utility.saveStringInPreference(NavigationDrawerActivity.this,"profileImagePath",Utility.saveImage(NavigationDrawerActivity.this, result));
    }
}


    /**
     * This method initialises FAB
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
                if (mCurrentFragment instanceof HomeFragment) {
                    ((HomeFragment) mCurrentFragment).onNavigationFabLongClick();
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
            if (mCurrentItemId == R.id.nav_categories ||
                    mCurrentItemId == R.id.nav_revise ||
                    mCurrentItemId == R.id.nav_settings) {

                creteAndLoadFragment(FRAGMENT_HOME);
            } else {
                //This means it should be home_fragment

                if (mCurrentFragment instanceof HomeFragment) {
                    if (!handleBackPressForHomeFragment()) {
                        super.onBackPressed();
                    }
                }
            }
        }
    }

    /**
     * This method handles back press for home fragment
     * <p>
     * Returns true if back press is handled else returns false
     */
    private boolean handleBackPressForHomeFragment() {
        if (((HomeFragment) mCurrentFragment).getSelectionMode()) {
            ((HomeFragment) mCurrentFragment).setSelectionMode(false);
            ((HomeFragment) mCurrentFragment).initialiseNotesAdapter(false);
            return true;
        } else {
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

        switch (id) {
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

        switch (mCurrentItemId) {
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
            case R.id.nav_about:
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_settings_about_me);
                //TODO - REMOVE THIS CODE
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

                        fragmentTransaction.add(R.id.navigation_drawer_fragment_container, new TempFragment()).commit();
                    }
                });
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                dialog.onWindowAttributesChanged(params);
                dialog.show();
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
        if (on) {
            mFab.setVisibility(View.VISIBLE);
        } else {
            mFab.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe
    public void onMessageEvent(LogoutCompleteEvent event) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }

}
