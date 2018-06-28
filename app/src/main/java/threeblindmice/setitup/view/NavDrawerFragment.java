package threeblindmice.setitup.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

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




    public static NavDrawerFragment newInstance(){
        return new NavDrawerFragment();
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

        LinearLayout ll = (LinearLayout) getView().findViewById(R.id.option_container);

        // Programmatically handles all options defined in XML
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

    //  TODO: Manage fragment transactions with IDs
    //      -- Doable when UI/UX is decided
    @Override
    public void onOptionSelected(String id){
        EventBus.getDefault().post(new UpdateFragmentEvent(id));
    }


}
