package threeblindmice.setitup.model;

import android.content.Context;

/**
 * Created by Slate on 2018-05-06.
 */



public class ContactAggregator {


    private Context mContext;

    public ContactAggregator(Context context){
        mContext = context;



        LocalContactThread lct = new LocalContactThread(mContext);
        lct.start();
    }








}
