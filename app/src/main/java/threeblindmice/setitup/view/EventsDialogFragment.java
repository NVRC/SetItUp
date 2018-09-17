package threeblindmice.setitup.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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

import me.everything.providers.android.calendar.Event;
import threeblindmice.setitup.R;
import threeblindmice.setitup.databinding.DialogEventsBinding;
import threeblindmice.setitup.databinding.ItemEventBinding;
import threeblindmice.setitup.viewmodel.EventViewModel;

public class EventsDialogFragment extends DialogFragment {

    private RecyclerView mRecyclerView;
    private DialogEventsBinding binding;
    private EventAdapter mEventAdapter;



    public static EventsDialogFragment newInstance(Date date) {
        EventsDialogFragment edf = new EventsDialogFragment();
        Bundle args = new Bundle();
        args.putLong("date",date.getTime());
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

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setView(binding.getRoot());
        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle dateBundle = this.getArguments();
        Date date = new Date();
        date.setTime(dateBundle.getLong("date"));
        mEventAdapter = new EventAdapter(fetchDailyEvents(date));

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



    public ArrayList<Event> fetchDailyEvents(Date date){

        ArrayList<Event> tempList = new ArrayList<>();
        Event tempE = new Event();
        tempE.dTStart = 0;
        tempE.dTend = 100;
        tempE.title = "Value";
        tempList.add(tempE);

        return tempList;
    }

    private class EventAdapter extends RecyclerView.Adapter<EventsDialogFragment.EventHolder>{
        private ArrayList<Event> mData;

        public EventAdapter(ArrayList<Event> events){
            mData = events;
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
