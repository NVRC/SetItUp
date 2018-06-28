package threeblindmice.setitup.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import threeblindmice.setitup.R;
import threeblindmice.setitup.events.UpdateFragmentEvent;


/**
 * Created by Slate on 2018-05-08.
 */

public class ContactsActivity extends AppCompatActivity {

    //  Constants
    //  TODO: Setup auto-configuring fragment switching
    private static final String TAG_EMPTY_FRAGMENT = "Empty";
    private static final String TAG_CONTACTS_FRAGMENT = "Contacts";
    private static final String TAG_NAV_FRAGMENT = "Nav";


    // Defines the id of the loader for later reference
    public static final int CONTACT_LOADER_ID = 78;
    // From docs: A unique identifier for this loader. Can be whatever you want.

    // Identifier for the permission request
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

    //  Persistent Objects
    private DrawerLayout drawerLayout;
    private ContactsFragment cf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        EventBus.getDefault().register(this);

        //  INIT toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //  INIT nav drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_fragment_container);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return true;
            }
        });

        //  INIT menu icon
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //  TODO: Update menu icon
            actionBar.setHomeAsUpIndicator(R.drawable.menu_icon);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getPermissionToReadUserContacts();
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Check for compatible layout versions
        if (findViewById(R.id.fragment_container) != null){
            // If a previous state is being restored, return

            if (savedInstanceState != null){
                return;
            }

            cf = new ContactsFragment();
            // If an intent provides additional run-time params
            //cf.setArguments(getIntent().getExtras());

            fragmentManager.beginTransaction()
                        .add(R.id.fragment_container, cf, TAG_CONTACTS_FRAGMENT).commit();

        }
        // Check for compatible layout versions
        if (findViewById(R.id.nav_fragment_container) != null){
            // If a previous state is being restored, return

            if (savedInstanceState != null){
                return;
            }

            NavDrawerFragment ndf = new NavDrawerFragment();
            // If an intent provides additional run-time params
            //ndf.setArguments(getIntent().getExtras());

            fragmentManager.beginTransaction()
                    .add(R.id.nav_fragment_container, ndf, TAG_NAV_FRAGMENT).commit();

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onFragmentUpdate(UpdateFragmentEvent event){
        drawerLayout.closeDrawers();
        String tag = event.getTag();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (tag.equals(TAG_CONTACTS_FRAGMENT)){
            //  TODO: Refactor to use the same ContactsFragment

            Fragment frag = fragmentManager.findFragmentByTag(TAG_EMPTY_FRAGMENT);
            if(frag != null && frag.isVisible()){
                fragmentManager.beginTransaction().replace(R.id.fragment_container, cf)
                        .commit();
            }

        } else if (tag.equals(TAG_EMPTY_FRAGMENT)){
            //  Other options... for now switch to empty fragment
            // Insert the fragment by replacing any existing fragment
            Fragment frag = fragmentManager.findFragmentByTag(TAG_CONTACTS_FRAGMENT);
            if(frag != null && frag.isVisible()){
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new EmptyFragment())
                        .commit();
            }
        }
    }





    // TODO: Handle Api lvls < 23 with conditional execution
    @TargetApi(23)
    // Called when the user is performing an action which requires the app to read the
    // user's contacts
    public void getPermissionToReadUserContacts() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_CONTACTS)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    // TODO: Handle Api lvls < 23 with conditional execution
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                boolean showRationale = shouldShowRequestPermissionRationale( Manifest.permission.READ_CONTACTS);

                if (showRationale) {
                    // do something here to handle degraded mode
                } else {
                    Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
