package com.rorasoft.roragame.View;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.Toast;

import com.rorasoft.roragame.R;
import com.rorasoft.roragame.Utils.Helper.FragmentHelper;
import com.rorasoft.roragame.Utils.SupportKeys;
import com.rorasoft.roragame.View.Adapter.AdapterFragmentCallbacks;
import com.rorasoft.roragame.View.GameList.GameListFragment;
import com.rorasoft.roragame.View.Home.HomeFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentHelper fragmentHelper;

    /** LIFECYCLE */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configView();
        initData();
    }

    /** CONFIG */

    private void configView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initData() {
        fragmentHelper = new FragmentHelper(this, R.id.view_holder_main);
    }

    /** ACTIONS */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            getSupportActionBar().setTitle("Home");
            fragmentHelper.changeFragment(HomeFragment.newInstance(), SupportKeys.HOME_TAG_FRAGMENT, 0, 0);

        } else if (id == R.id.nav_list_games) {
//            getSupportActionBar().setTitle("List Games");
//            fragmentHelper.changeFragment(GameListFragment.newInstance(), SupportKeys.GAME_LIST_TAG_FRAGMENT, 0, 0);

            Intent i = new Intent(this, AppView.class);
            i.putExtra(AppView.NAME_EXTRA, "Unknown");
            i.putExtra(AppView.UUID_EXTRA, "69001187-335a-4c69-bc45-1f696a123f00");
            startActivity(i);
        }

//        else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
