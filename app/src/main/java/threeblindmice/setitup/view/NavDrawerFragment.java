package threeblindmice.setitup.view;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import threeblindmice.setitup.R;
import threeblindmice.setitup.events.UpdateFragmentEvent;
import threeblindmice.setitup.events.UpdateTokenEvent;
import threeblindmice.setitup.interfaces.NavInterface;
import threeblindmice.setitup.listeners.OptionClickListener;

import static android.app.Activity.RESULT_OK;


public class NavDrawerFragment extends Fragment implements NavInterface {

    //  Constants
    private static final String GOOGLE_ACC_TYPE = "com.google";
    private static final int CONST_OPTIONS_ELEMENT_OFFSET = 4;
    private static final int AUTH_REQUEST = 0;

    //  Dynamic vars
    private String currToken;

    public static NavDrawerFragment newInstance(){
        return new NavDrawerFragment();
    }

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nav_drawer, container, false);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        //  Programmatically set Header height according to Material design spec
        final LinearLayout layout = getView().findViewById(R.id.nav_header_container);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        //  Bind inflation, layout listener to the header so that the height set will be preserved
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup.LayoutParams params = layout.getLayoutParams();
                //  Sets the height = width * 9/16
                Double height = layout.getMeasuredWidth() * 0.5725;
                params.height = height.intValue();
                layout.setLayoutParams(params);
                ViewTreeObserver obs = layout.getViewTreeObserver();

                // Unbind the listener as it needs only occur once
                obs.removeOnGlobalLayoutListener(this);

            }
        });


        //  Unbind the start of the account selection process
        // TODO: Update Account Manager UX flow when decided
        TextView email = (TextView) getView().findViewById(R.id.nav_header_email);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountManager am = AccountManager.get(getActivity());
                Account[] accounts = am.getAccountsByType(GOOGLE_ACC_TYPE);
                List<String> accountNames = new ArrayList<>();
                for (Account acc : accounts){
                    accountNames.add(acc.name);
                }
                new MaterialDialog.Builder(getContext())
                        .title(R.string.acc_dialog_title)
                        .items(accountNames)
                        .positiveText(R.string.add_acc)
                        .negativeText(R.string.cancel)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection (MaterialDialog dialog, View view, int which, CharSequence text){
                                AccountManager am = AccountManager.get(getActivity());
                                am.invalidateAuthToken(GOOGLE_ACC_TYPE,text.toString());
                                startGoogleAuth(text);
                                dialog.dismiss();
                            }

                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //  Create an account in AccountManager
                                //
                            }
                        })
                .show();
            }
        });





        // Programmatically handles all options defined in XML
        LinearLayout ll = (LinearLayout) getView().findViewById(R.id.option_container);
        for( int i = 0; i < ll.getChildCount(); i++ ){
            TextView tv = (TextView) ll.getChildAt(i);
            OptionClickListener ocl = new OptionClickListener();
            ocl.setId(tv.getText().toString());
            ocl.setCallback(this);
            tv.setOnClickListener(ocl);
        }
    }

    //  Takes a name referenced by an Account in AccountManager
    private void startGoogleAuth(CharSequence targetName){

        tokenEvent = new UpdateTokenEvent();
        tokenEvent.addEmail(targetName.toString());

        AccountManager am = AccountManager.get(getActivity());
        Bundle options = new Bundle();

        //  TODO: check for other account types e.g. Facebook "com.facebook.auth.login"
        Account[] accounts = am.getAccountsByType(GOOGLE_ACC_TYPE);
        String AUTH_TOKEN_TYPE = "cp";
        for (Account acc : accounts){
            if(acc.name.equals(targetName)){
                //  Get Account object from AccountManager
                //  TODO: Implement error handling
                am.getAuthToken(
                        acc,                            //  Account retrieved using getAccountsByType()
                        AUTH_TOKEN_TYPE,                //  Auth scope
                        options,                        //  Authenticator-specific options
                        getActivity(),                  
                        new OnTokenAcquired(),          // Callback on token success
                        new Handler(new onError()));    // Callback if an error occurred
            }
        }
    }

    //  Manage fragment transactions with IDs
    //  Notify the main activity to swap fragments
    @Override
    public void onOptionSelected(String id){
        EventBus.getDefault().post(new UpdateFragmentEvent(id));
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent intent){

        System.out.println("Entered onActivityResult()");
        System.out.println(requestCode);
        System.out.println(resultCode);

        if (requestCode == AUTH_REQUEST){
            System.out.println("Passed AUTH_REQUEST");
            if (resultCode == RESULT_OK){
                //  New Auth token
                System.out.println("Passed RESULT_OK");
                System.out.println("Google URL attempt");
                URL url = null;
                try {
                    url = new URL("https://www.googleapis.com/tasks/v1/users/@me/lists?key=" + getString(R.string.api_key));
                } catch (MalformedURLException e){
                    e.printStackTrace();
                    //  TODO: Handle error
                    //  call dialog
                }
                URLConnection conn;

                try {
                    conn = (HttpURLConnection) url.openConnection();
                } catch (IOException e){
                    e.printStackTrace();
                    return;
                }
                conn.addRequestProperty("client_id", getString(R.string.google_client_id));
                conn.addRequestProperty("client_secret", getString(R.string.google_client_secret));
                conn.setRequestProperty("Authorization", "OAuth " + currToken);
                try{
                    conn.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    //  TODO: Handle error
                }
            }

        }
        super.onActivityResult(requestCode,resultCode,intent);

    }





    //  Temporary Error handling mechanism for authentication
    private class onError implements Handler.Callback {
        @Override
        public boolean handleMessage(Message message){
            return true;
        }
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            //  Get the result of the operation from the AccountManagerFuture.
            //  TODO: Handle error notification
            Bundle bundle = null;
            try {
                bundle = result.getResult();
            } catch (OperationCanceledException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            } catch (AuthenticatorException e){
                e.printStackTrace();
            }
            // The token is a named value in the bundle. The name of the value
            // is stored in the constant AccountManager.KEY_AUTHTOKEN.
            if (bundle != null) {
                String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                tokenEvent.addToken(token);
                currToken = token;
                EventBus.getDefault().post(tokenEvent);

                Intent launch = null;
                try {
                    launch = (Intent) result.getResult().get(AccountManager.KEY_INTENT);
                } catch (OperationCanceledException e){
                    e.printStackTrace();
                } catch (IOException e){
                    e.printStackTrace();
                } catch (AuthenticatorException e){
                    e.printStackTrace();
                }
                if (launch != null) {


                    startActivityForResult(launch, AUTH_REQUEST);

                } else {
                    //  Authentication Token Already Captured
                    System.out.println("\t\tALT");
                }
            }
        }
    }
}


