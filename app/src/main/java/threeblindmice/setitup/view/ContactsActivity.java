package threeblindmice.setitup.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;

import threeblindmice.setitup.R;
import threeblindmice.setitup.events.QueryEvent;
import threeblindmice.setitup.events.UpdateFragmentEvent;
import threeblindmice.setitup.events.UpdateUIComponentEvent;


/**
 * Created by Slate on 2018-05-08.
 */

public class ContactsActivity extends AppCompatActivity {

    //  Constants
    //  TODO: Setup auto-configuring fragment switching
    private String TAG_EMPTY_FRAGMENT;
    private String TAG_CONTACTS_FRAGMENT;
    private String TAG_SMS_FRAGMENT;
    private String TAG_CALENDAR_FRAGMENT;
    private String TAG_DATE_DIALOG;


    private static final String TAG_NAV_FRAGMENT = "Nav";
    private static final int RC_SIGN_IN = 0;
    private static final int SIGNED_IN = 1;
    private static final int STATE_SIGNING_IN = 2;
    private static final int STATE_IN_PROGRESS = 3;

    private GoogleSignInAccount account;



    private GoogleSignInClient mGoogleSignInClient;


    private static final String GOOGLE_ACC_TYPE = "com.google";

    // Defines the id of the loader for later reference
    public static final int CONTACT_LOADER_ID = 78;
    // From docs: A unique identifier for this loader. Can be whatever you want.

    // Identifier for the permission request
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    private static final int SMS_PERMISSIONS_REQUEST = 123;



    //  Persistent Objects
    private DrawerLayout drawerLayout;
    private ContactsFragment cf;
    private String currToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        EventBus.getDefault().register(this);

        TAG_EMPTY_FRAGMENT = getString(R.string.empty);
        TAG_CONTACTS_FRAGMENT = getString(R.string.contacts);
        TAG_SMS_FRAGMENT = getString(R.string.sms);
        TAG_CALENDAR_FRAGMENT = getString(R.string.calendar);
        TAG_DATE_DIALOG = getString(R.string.date);

        //  INIT toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //  INIT nav drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawerLayout != null){

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
        }
        getPermissionToReadUserContacts();

        // Check for compatible layout versions
        if (findViewById(R.id.fragment_container) != null){
            // If a previous state is being restored, return

            if (savedInstanceState != null){
                return;
            }

            SmsFragment sf = new SmsFragment();
            // If an intent provides additional run-time params
            //cf.setArguments(getIntent().getExtras());
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.addToBackStack(TAG_SMS_FRAGMENT);
            trans.replace(R.id.fragment_container, sf, TAG_SMS_FRAGMENT);

            trans.commit();

        }

        // Check for compatible layout versions
        if (findViewById(R.id.fragment_container) != null){
            // If a previous state is being restored, return

            if (savedInstanceState != null){
                return;
            }

            CalendarFragment cf = new CalendarFragment();
            // If an intent provides additional run-time params
            //cf.setArguments(getIntent().getExtras());
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.addToBackStack(TAG_CALENDAR_FRAGMENT);
            trans.replace(R.id.fragment_container, cf, TAG_CALENDAR_FRAGMENT);

            trans.commit();

        }

        // Check for compatible layout versions
        if (findViewById(R.id.fragment_container) != null){
            // If a previous state is being restored, return

            if (savedInstanceState != null){
                return;
            }

            cf = new ContactsFragment();
            // If an intent provides additional run-time params
            //cf.setArguments(getIntent().getExtras());
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.addToBackStack(TAG_CONTACTS_FRAGMENT);
            trans.replace(R.id.fragment_container, cf, TAG_CONTACTS_FRAGMENT);

            trans.commit();

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
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.nav_fragment_container, ndf, TAG_NAV_FRAGMENT);
            trans.commit();

        }


    }


    @Override
    public void onStart(){
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){
            System.out.println("tester");
            System.out.print("\t ACCOUNT: " + account.getEmail());
            updateUI();

        } else {
            //  Google SignIn
            //  State managed Global in this activity

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.google_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


            // Set the dimensions of the sign-in button.
            Button signInButton = findViewById(R.id.sign_in_button);
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });

        }


    }

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent intent){
        super.onActivityResult(requestCode,resultCode,intent);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            handleSignInResult(task);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            updateUI();


        } catch (ApiException e) {
            //  Sign-in Failure
        }
    }

    private void updateUI(){
        EventBus.getDefault().post(new UpdateUIComponentEvent(R.id.nav_header_container_signin,null));
        EventBus.getDefault().post(new UpdateUIComponentEvent(R.id.nav_header_email,account.getEmail()));
        EventBus.getDefault().post(new UpdateUIComponentEvent(R.id.nav_header_name,account.getDisplayName()));
        EventBus.getDefault().post(new UpdateUIComponentEvent(R.id.nav_header_img,account.getPhotoUrl().toString()));


    }



    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem search = menu.findItem(R.id.search);

        //  Setup searchable icon and style
        SearchView searchView = (SearchView) search.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());

        searchView.setSearchableInfo(searchableInfo);
        searchView.setIconifiedByDefault(true);
        searchView.setQueryHint(getString(R.string.search_hint));



        EditText searchEditText = (EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        ImageView searchCloseButton = (ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        ImageView searchInner = (ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);

        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        theTextArea.setTextColor(ResourcesCompat.getColor(getResources(), R.color.primary_light, null));
        searchEditText.setTextColor(ResourcesCompat.getColor(getResources(), R.color.primary_light, null));
        searchEditText.setHintTextColor(ResourcesCompat.getColor(getResources(), R.color.primary_light, null));
        searchCloseButton.setImageResource(R.drawable.close_icon);
        searchInner.setImageResource(R.drawable.search_icon);

        ImageView searchMagIcon = (ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        searchMagIcon.setImageResource(R.drawable.search_icon);


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        //
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //  Notify RecyclerView.Adapter of query
            @Override
            public boolean onQueryTextChange(String query) {
                EventBus.getDefault().post(new QueryEvent(query));
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                onQueryTextChange(query);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
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

    //  Updates the fragment container
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFragmentUpdate(UpdateFragmentEvent event){
        if(drawerLayout != null) {
            drawerLayout.closeDrawers();
        }
        String tag = event.getTag();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (tag.equals(TAG_CONTACTS_FRAGMENT)){

            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.addToBackStack(TAG_CONTACTS_FRAGMENT);
            trans.replace(R.id.fragment_container,cf,TAG_CONTACTS_FRAGMENT);
            trans.commit();
            findViewById(R.id.fastscroll).setVisibility(View.VISIBLE);

        } else {
            findViewById(R.id.fastscroll).setVisibility(View.GONE);
            if (tag.equals(TAG_EMPTY_FRAGMENT)){
                //  Other options... for now switch to empty fragment
                // Insert the fragment by replacing any existing fragment
                Fragment frag = fragmentManager.findFragmentByTag(TAG_CONTACTS_FRAGMENT);
                if(frag != null && frag.isVisible()){
                    FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                    trans.replace(R.id.fragment_container, new EmptyFragment(),TAG_EMPTY_FRAGMENT);
                    trans.commit();
                }
            } else if (tag.equals(TAG_SMS_FRAGMENT)){

                //  Other options... for now switch to empty fragment
                // Insert the fragment by replacing any existing fragment
                Fragment frag = fragmentManager.findFragmentByTag(TAG_CONTACTS_FRAGMENT);
                if (frag == null){
                    frag = fragmentManager.findFragmentByTag(TAG_EMPTY_FRAGMENT);
                }
                if(frag != null && frag.isVisible()){
                    System.out.println("\t Entered SMS frag");
                    FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                    trans.replace(R.id.fragment_container, new SmsFragment(),TAG_SMS_FRAGMENT);
                    trans.commit();
                }
            } else if (tag.equals(TAG_CALENDAR_FRAGMENT)){
                //  Other options... for now switch to empty fragment
                //  Insert the fragment by replacing any existing fragment
                Fragment frag = fragmentManager.findFragmentByTag(TAG_CONTACTS_FRAGMENT);
                if(frag != null && frag.isVisible()){
                    System.out.println("\t Entered Calendar frag");


                    CaldroidFragment caldroidFragment = new CaldroidFragment();
                    Bundle args = new Bundle();
                    Calendar cal = Calendar.getInstance();
                    args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
                    args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));

                    caldroidFragment.setArguments(args);
                    caldroidFragment.setCaldroidListener(new CaldroidListener() {
                        @Override
                        public void onSelectDate(Date date, View view) {
                            EventsDialogFragment dateDialog = EventsDialogFragment.newInstance(date, account.getEmail());
                            dateDialog.show(getFragmentManager(),TAG_DATE_DIALOG);
                        }
                    });

                    FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                    t.replace(R.id.fragment_container, caldroidFragment, TAG_CALENDAR_FRAGMENT);
                    t.commit();
                }
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
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            System.out.println("No permission sms");
            // Permission not yet granted. Use requestPermissions().
            // MY_PERMISSIONS_REQUEST_SEND_SMS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSIONS_REQUEST);
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
        } else if (requestCode == SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                boolean showRationale = shouldShowRequestPermissionRationale( Manifest.permission.READ_CONTACTS);

                if (showRationale) {
                    // do something here to handle degraded mode
                } else {
                    Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
