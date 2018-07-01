package threeblindmice.setitup.view;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Build;
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
import java.util.ArrayList;
import java.util.List;

import threeblindmice.setitup.R;
import threeblindmice.setitup.events.UpdateFragmentEvent;
import threeblindmice.setitup.interfaces.NavInterface;
import threeblindmice.setitup.listeners.OptionClickListener;


public class NavDrawerFragment extends Fragment implements NavInterface {

    public NavInterface callbacksInterface;

    //  Constants
    //  Fragment IDs
    private static final String TAG_EMPTY_FRAGMENT = "TAG_EMPTY_FRAGMENT";
    private static final String TAG_CONTACT_FRAGMENT = "TAG_CONTACT_FRAGMENT";
    private static final String GOOGLE_ACC_TYPE = "com.google";
    private static final int CONST_OPTIONS_ELEMENT_OFFSET = 4;

    private static final int AUTH_REQUEST = 0;




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

        //  Set nav drawer header height
        final LinearLayout layout = getView().findViewById(R.id.nav_header_container);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                ViewGroup.LayoutParams params = layout.getLayoutParams();
                //  Sets the height to be width*9/16 as recommended in Material specs
                Double height = layout.getMeasuredWidth() * 0.5725;
                params.height = height.intValue();
                layout.setLayoutParams(params);

                ViewTreeObserver obs = layout.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                }
            }
        });


        //  Starts the account selection process
        //  Precursor to OAuth2
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
                            }
                        })
                .show();
            }
        });





        // Programmatically handles all options defined in XML
        LinearLayout ll = (LinearLayout) getView().findViewById(R.id.option_container);

        for( int i = 0; i < ll.getChildCount(); i++ ){
            //  View instead of TextView to handle polymorphism
            //  TODO: Update XML dependant View calls
            TextView tv = (TextView) ll.getChildAt(i);
            OptionClickListener ocl = new OptionClickListener();
            ocl.setId(tv.getText().toString());
            ocl.setCallback(this);
            tv.setOnClickListener(ocl);
        }
    }

    private void startGoogleAuth(CharSequence targetName){

        AccountManager am = AccountManager.get(getActivity());
        Bundle options = new Bundle();
        //  Currently just selects the first account for testing
        Account[] accounts = am.getAccountsByType(GOOGLE_ACC_TYPE);
        for (Account acc : accounts){
            if(acc.name.equals(targetName)){
                //  TODO: Implement error handling
                am.getAuthToken(
                        acc,                     // Account retrieved using getAccountsByType()
                        "Schedule To Meet Up",            // Auth scope
                        options,                        // Authenticator-specific options
                        getActivity(),                           // Your activity
                        new OnTokenAcquired(),          // Callback called when a token is successfully acquired
                        new Handler(new onError()));    // Callback called if an error occurs
            }
        }






    }

    //  Manage fragment transactions with IDs
    @Override
    public void onOptionSelected(String id){
        EventBus.getDefault().post(new UpdateFragmentEvent(id));
    }


    //  Temporary Error handling mechanism for authentication
    private class onError implements Handler.Callback {
        @Override
        public boolean handleMessage(Message message){
            System.out.println(Message.obtain());
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
                    return;
                }
            }
        }
    }
}


