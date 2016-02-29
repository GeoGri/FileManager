package com.example.grzegorzwojda.filemanager;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManagerMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DialogListOptionsFragment.NoticeDialogListFragmentListener,
        Folder_ListFragment.NoticeListFragmentManagerListener,
        Folder_EmptyFragment.NoticeEmptyFragmentListener,
        StartFragment.NoticeStartFragment{

    boolean doubleBackPressed = false;

    final private String COPY_TAG = "copy";
    final private String CUT_TAG = "cut";
    private String fabTag = "";
    private String FRAGMENT_TAG = "";

    Menu menu;
    List<File> selectedFiles = new ArrayList<>();

    Toolbar toolbar;
    MenuItem menuItem;
    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    FragmentHelper fragmentHelper= new FragmentHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (fabTag) {
                    case COPY_TAG:
                        copyAndGetResult("copy");
                        break;
                    case CUT_TAG:
                        copyAndGetResult("cut");
                        break;
                }
            }
        });
        fab.setImageDrawable(getDrawable(android.R.drawable.ic_menu_add));
        fab.hide();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void copyAndGetResult(String command) {
        DialogCopyCatFragment progress
                = new DialogCopyCatFragment()
                .newInstance(selectedFiles, FRAGMENT_TAG, FRAGMENT_TAG, command);
        progress.show(getFragmentManager(), "seekBar");
        fab.hide();
    }

    @Override
    public void onBackPressed() {

        FragmentManager fm = getFragmentManager();


        int countBackStack = fm.getBackStackEntryCount();
        // back title
        if(countBackStack == 1)toolbar.setTitle(R.string.app_name);

        // if fab is visibility and go to start frag
        if (fab.isShown() && countBackStack == 1) {
            toolbar.setTitle(R.string.app_name);
            fab.hide();
            selectedFiles.clear();
            fm.popBackStack();
        } else {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) { // zamknij drawer
                drawerLayout.closeDrawers();
                doubleBackPressed = false;
            } else {
                if (fm.getBackStackEntryCount() > 0) {
                    doubleBackPressed = false;
                    fm.popBackStack();
                } else {
                    if (doubleBackPressed)
                        super.onBackPressed();
                    else {
                        doubleBackPressed = true;
                        Toast.makeText(getBaseContext(), R.string.double_back_press, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.file_manager_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            addDialog();
        } else if (id == R.id.action_cancel){
            fab.hide();
            selectedFiles.clear();
            menuItem.setEnabled(false);
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(FileManagerMain.this);
        final EditText titleName = new EditText(getBaseContext());
        titleName.setTextColor(Color.BLACK);
        builder
                .setView(titleName)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fragmentHelper.createNewDirectory(
                                FileManagerMain.this
                                , getFragmentManager()
                                , FRAGMENT_TAG
                                , titleName.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .setTitle(getString(R.string.new_folder))
                .create()
                .show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        /*if (item.isChecked()) item.setChecked(false);
        else item.setChecked(true);*/
        drawerLayout.closeDrawers();

        int id = item.getItemId();

        if (id == R.id.photos) {
            openNewFragment(System.getenv("EXTERNAL_STORAGE") + "/DCIM");
            toolbar.setTitle(getString(R.string.photos));
        }
        else if (id == R.id.music) {
            openNewFragment(System.getenv("EXTERNAL_STORAGE") + "/Music");
            toolbar.setTitle(getString(R.string.music));
        }
        else if (id == R.id.movie) {
            openNewFragment(System.getenv("EXTERNAL_STORAGE") + "/Movies");
            toolbar.setTitle(getString(R.string.movie));
        }
        else if (id == R.id.download) {
            openNewFragment(System.getenv("EXTERNAL_STORAGE") + "/Download");
            toolbar.setTitle(getString(R.string.download));
        }
        else if (id == R.id.root) {
            openNewFragment("/");
            toolbar.setTitle(getString(R.string.root_directory));
        }
        else if (id == R.id.internal) {
            openNewFragment(System.getenv("EXTERNAL_STORAGE"));
            toolbar.setTitle(getString(R.string.internal_storage));
        }
        else if (id == R.id.external) {
            openNewFragment(System.getenv("SECONDARY_STORAGE"));
            toolbar.setTitle(getString(R.string.external_storage));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openNewFragment(String path) {
        fragmentHelper.clearBackStack(getFragmentManager());
        fragmentHelper.openFile(path, getFragmentManager(), true);
    }

    private void setEnabledCancelItem() {
        menuItem = menu.findItem(R.id.action_cancel);
        menuItem.setEnabled(true);
    }

    @Override
    public void copyListener(List<File> filesToCopy) {
        selectedFiles = new ArrayList<>(filesToCopy);
        setEnabledCancelItem();
        fabTag = COPY_TAG;
        fab.setImageDrawable(getDrawable(android.R.drawable.ic_menu_add));
        fab.show();
    }

    @Override
    public void cutListener(List<File> filesToCut) {
        selectedFiles = new ArrayList<>(filesToCut);
        setEnabledCancelItem();
        fabTag = CUT_TAG;
        fab.setImageDrawable(getDrawable(android.R.drawable.ic_menu_add));
        fab.show();
    }

    @Override
    public void onChangeFragmentTag(String tag) {
        FRAGMENT_TAG = tag;
    }

    @Override
    public void onChangeFragmentTagFromEmptyFrag(String tag) {
        FRAGMENT_TAG = tag;
    }

    @Override
    public void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }
}
