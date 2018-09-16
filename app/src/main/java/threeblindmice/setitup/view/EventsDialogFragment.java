package threeblindmice.setitup.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import me.everything.providers.android.calendar.Event;
import threeblindmice.setitup.R;
import threeblindmice.setitup.databinding.ItemContactBinding;
import threeblindmice.setitup.model.Contact;
import threeblindmice.setitup.viewmodel.EventViewModel;

public class EventsDialogFragment extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_events, null));
        return builder.create();
    }

    private class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemContactBinding mBinding;
        private Context mContext;
        private LinearLayout childrenLayout;

        private EventHolder(ItemContactBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

            mContext = mBinding.getRoot().getContext();
            mBinding.getRoot().findViewById(R.id.event_tile_container).setOnClickListener(this);

            mBinding.setViewModel(new EventViewModel());


        }

        public void bind(Event event){
            mBinding.getViewModel().setEvent(event);
            mBinding.executePendingBindings();
        }
        @Override
        public void onClick(View view) {

        }
    }

        private class ContactAdapter extends RecyclerView.Adapter<EventsDialogFragment.EventHolder>{
        private ArrayList<Event> mData;

            @Override
            public EventsDialogFragment.EventHolder onCreateViewHolder(ViewGroup parent, int viewType){
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                ItemContactBinding binding = DataBindingUtil
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

            @Override public int getItemCount(){
                if (mData == null){ return 0;}
                return mData.size();
            }


    }
}
