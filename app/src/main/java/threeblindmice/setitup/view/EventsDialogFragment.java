package threeblindmice.setitup.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.everything.providers.android.calendar.Event;
import threeblindmice.setitup.R;
import threeblindmice.setitup.databinding.DialogEventsBinding;
import threeblindmice.setitup.databinding.ItemEventBinding;
import threeblindmice.setitup.viewmodel.EventViewModel;

public class EventsDialogFragment extends DialogFragment {

    private RecyclerView mRecyclerView;
    private DialogEventsBinding binding;
    private EventAdapter mEventAdapter;


    //  Calendar query handlers
    //  Indexed projection array (avoiding dynamic lookups improves performance)
    public static final String[] CAL_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };
    //  Manually iterative to avoid introducing bugs with an untested table lookup lib
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    //  Event query handlers
    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.TITLE
    };
    private static final int PROJECTION_EVENT_START_INDEX = 0;
    private static final int PROJECTION_EVENT_END_INDEX = 1;
    private static final int PROJECTION_EVENT_TITLE_INDEX = 2;

    private static final String DATE_TAG = "date";
    private static final String ID_TAG = "email";


    //  Dynamic Vars
    private String accountId;
    private Date selDate = null;



    public static EventsDialogFragment newInstance(Date date, String accountEmail ) {
        EventsDialogFragment edf = new EventsDialogFragment();
        Bundle args = new Bundle();
        args.putLong(DATE_TAG,date.getTime());
        args.putString(ID_TAG, accountEmail);
        edf.setArguments(args);
        return edf;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        binding = DataBindingUtil
                .inflate(inflater, R.layout.dialog_events, null, false);
        binding.recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getActivity()));

        binding.recyclerViewEvents.setAdapter(mEventAdapter);

        //  Inflate and set the dialog layout to the bound, inflated xml
        //  setView as the base binding
        builder.setView(binding.getRoot());
        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle dataBundle = this.getArguments();
        selDate = new Date();
        selDate.setTime(dataBundle.getLong(DATE_TAG));
        accountId = dataBundle.getString(ID_TAG);
        mEventAdapter = new EventAdapter();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return binding.getRoot();
    }


    @Override
    public void onStart(){
        super.onStart();
    }

    private void fetchCalendars(String email){
        Cursor cur = null;
        ContentResolver cr = getContext().getContentResolver();

        AsyncQueryHandler calQueryHandler = new AsyncQueryHandler(cr) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                if(cursor == null){

                } else if(cursor.getCount() < 1){
                    //  No results
                    //  Display no events to user
                } else {
                    while (cursor.moveToNext()) {
                        String calId = null;
                        String displayName = null;
                        String accountName = null;
                        String ownerName = null;

                        //  Fetch field values
                        calId = cursor.getString(PROJECTION_ID_INDEX);
                        displayName = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
                        accountName = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                        ownerName = cursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

                        //  Retrieve events associated to each calendar related to
                        //      the authenticated Account
                        fetchEvents(calId);
                    }
                }
            }
        };




        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[] {email, email};



        Uri uri = CalendarContract.Calendars.CONTENT_URI;


        calQueryHandler.startQuery(
                1, null,
                uri,
                CAL_PROJECTION,
                selection,
                selectionArgs,
                CalendarContract.Calendars.DEFAULT_SORT_ORDER);
    }

    private void fetchEvents(String calId){
        Cursor cur = null;
        ContentResolver cr = getContext().getContentResolver();

        AsyncQueryHandler eventQueryHandler = new AsyncQueryHandler(cr) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                if(cursor == null){

                } else if(cursor.getCount() < 1){
                    //  No results
                    //  Display no events to user
                    //  Do more than that, notify them via container text
                } else {
                    List<Event> eventsToAdd = new ArrayList<Event>();
                    while (cursor.moveToNext()) {
                        Date start = new Date();
                        Date end = new Date();
                        String title = null;

                        //  Utilizing Date() to take advantage of comparators
                        long startLong = cursor.getLong(PROJECTION_EVENT_START_INDEX);
                        start.setTime(startLong);
                        long endLong = cursor.getLong(PROJECTION_EVENT_END_INDEX);
                        end.setTime(endLong);
                        title = cursor.getString(PROJECTION_EVENT_TITLE_INDEX);

                        //  Establishing bounds
                        //  |   selDate 00:00 -- start -- end -- selDate 23:59  |
                        //  TODO: Investigate libs to do this, also refactor to use Content URI
                        //  Handle full-day or multi-day events
                        if (start.after(selDate) && end.after(selDate)){
                            Event tempEvent = new Event();
                            tempEvent.title = title;
                            tempEvent.dTStart = startLong;
                            tempEvent.dTend = endLong;
                            eventsToAdd.add(tempEvent);
                        }
                    }
                    mEventAdapter.addAll(eventsToAdd);
                }
            }
        };

        String selection = "(" + CalendarContract.Events.CALENDAR_ID+ " = ?)";
        String[] selectionArgs = new String[] {calId};
        Uri uri = CalendarContract.Events.CONTENT_URI;

        eventQueryHandler.startQuery(
                1, null,
                uri,
                EVENT_PROJECTION,
                selection,
                selectionArgs, null);
    }


    private class EventAdapter extends RecyclerView.Adapter<EventsDialogFragment.EventHolder>{
        private ArrayList<Event> mData;

        public EventAdapter(){
            mData = new ArrayList<>();
        }

        @NonNull
        @Override
        public EventsDialogFragment.EventHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            ItemEventBinding binding = DataBindingUtil
                    .inflate(inflater, R.layout.item_event, parent, false);
            return new EventsDialogFragment.EventHolder(binding);
        }

        @Override
        public void onBindViewHolder(EventsDialogFragment.EventHolder holder, int position){
            Event event = mData.get(position);
            //  Fixes recyclerView expandable item duplication
            holder.setIsRecyclable(false);
            holder.bind(event);
        }

        @Override
        public int getItemCount(){
            if (mData == null){ return 0;}
            return mData.size();
        }

        public void addAll(List<Event> events) {
            mData.addAll(events);
        }
    }

    private class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemEventBinding mBinding;
        private Context mContext;
        private LinearLayout childrenLayout;

        private EventHolder(ItemEventBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

            mContext = mBinding.getRoot().getContext();
            mBinding.getRoot().findViewById(R.id.event_tile_container).setOnClickListener(this);

            mBinding.setViewModelEvent(new EventViewModel());


        }


        public void bind(Event event){
            mBinding.getViewModelEvent().setEvent(event);
            mBinding.executePendingBindings();
        }
        @Override
        public void onClick(View view) {

        }
    }


}
