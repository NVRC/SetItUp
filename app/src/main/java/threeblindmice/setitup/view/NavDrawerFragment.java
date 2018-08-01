package threeblindmice.setitup.view;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import threeblindmice.setitup.R;
import threeblindmice.setitup.events.UpdateFragmentEvent;
import threeblindmice.setitup.events.UpdateTokenEvent;
import threeblindmice.setitup.interfaces.NavInterface;
import threeblindmice.setitup.listeners.OptionClickListener;


public class NavDrawerFragment extends Fragment implements NavInterface {

    //  Constants
    private static final String GOOGLE_ACC_TYPE = "com.google";
    private static final int CONST_OPTIONS_ELEMENT_OFFSET = 4;
    private static final int AUTH_REQUEST = 0;


    //  Dynamic vars
    private String currToken;
    private UpdateTokenEvent tokenEvent;

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
        //  TODO: Do less hacky
        LinearLayout ll0 = (LinearLayout) getView().findViewById(R.id.nav_drawer_option_0);
        LinearLayout ll1 = (LinearLayout) getView().findViewById(R.id.nav_drawer_option_1);
        LinearLayout ll2 = (LinearLayout) getView().findViewById(R.id.nav_drawer_option_2);
        OptionClickListener ocl0 = new OptionClickListener();
        OptionClickListener ocl1 = new OptionClickListener();
        OptionClickListener ocl2 = new OptionClickListener();
        ocl0.setId(getString(R.string.contacts));
        ocl1.setId(getString(R.string.empty));
        ocl2.setId(getString(R.string.sms));
        ocl0.setCallback(this);
        ocl1.setCallback(this);
        ocl2.setCallback(this);
        ll0.setOnClickListener(ocl0);
        ll1.setOnClickListener(ocl1);
        ll2.setOnClickListener(ocl2);


    }
    

    //  Manage fragment transactions with IDs
    //  Notify the main activity to swap fragments
    @Override
    public void onOptionSelected(String id){
        EventBus.getDefault().post(new UpdateFragmentEvent(id));
    }


}


