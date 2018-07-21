package threeblindmice.setitup.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import threeblindmice.setitup.R;
import threeblindmice.setitup.databinding.FragmentContactsBinding;
import threeblindmice.setitup.databinding.ItemContactBinding;
import threeblindmice.setitup.events.QueryEvent;
import threeblindmice.setitup.events.RefreshContactListEvent;
import threeblindmice.setitup.events.UpdateUIComponentEvent;
import threeblindmice.setitup.listeners.DayOnClickListener;
import threeblindmice.setitup.model.Contact;
import threeblindmice.setitup.model.ContactsModel;
import threeblindmice.setitup.util.SalientCalendarContainer;
import threeblindmice.setitup.util.State;
import threeblindmice.setitup.viewmodel.ContactViewModel;

/**
 * Created by Nathaniel Charlebois on 2018-05-08.
 */

public class ContactsFragment extends Fragment {


    //  Class Vars
    private ContactsModel mContactsModel;
    private ContactAdapter mContactAdapter;
    private FragmentContactsBinding binding;

    //  Constants
    private static final int LEFT_SEL_ID = 0;
    private static final int RIGHT_SEL_ID = 1;
    private static final int[] WEEK_IDS = {
            303,
            304,
            305,
            306,
            307,
            308,
            309
    };
    private static final int LEFT_MONTH_TV_ID = 3;
    private static final int RIGHT_MONTH_TV_ID = 4;





    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_contacts, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        binding.recyclerView.setAdapter(mContactAdapter);
        try{
            FastScroller fastScroller = getActivity().findViewById(R.id.fastscroll);
            fastScroller.setRecyclerView(binding.recyclerView);
        } catch (NullPointerException e){
            e.printStackTrace();
        }




        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mContactAdapter = new ContactAdapter();

        mContactsModel = new ContactsModel(getContext());
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //  Prompt the Model to interrupt LocalContactThread
    @Override
    public void onPause(){
        super.onPause();
        mContactsModel.teardown();
    }

    //  Matches a text query to Contact.name using contains
    private List<Contact> filter(List<Contact> contacts, String query){
        List<Contact> filteredModelList = new ArrayList<>();
        for (Contact contact : contacts) {
            String text = contact.getName().toLowerCase();
            if (text.contains(query.toLowerCase())) {
                filteredModelList.add(contact);
            }
        }
        return filteredModelList;
    }



    /*  Asynchronous Event Listeners
    ------------------------------------------------------------------------------------------------
    */

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onUpdate(RefreshContactListEvent event){
        final RefreshContactListEvent eventPointer = event;
        getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run() {

                if(eventPointer.getState() == State.INIT){
                    mContactAdapter.clear();
                    mContactAdapter.addAll(eventPointer.getContacts());
                } else if (eventPointer.getState() == State.SINGLE){
                    int pos;
                    Contact contact = eventPointer.getContact();
                    //  All animations, sorting, etc. handled by SortedListAdapter for RecyclerView
                    if(eventPointer.getFlag()){
                        //  Add condition
                        mContactAdapter.addItem(contact);
                    } else if (!eventPointer.getFlag()) {
                        //  Remove condition
                        mContactAdapter.removeItem(contact);
                    }
                }
            }
        });


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleQueryEvent(QueryEvent event){
        // Implement the filter logic
        List<Contact> filteredModelList = filter(mContactAdapter.getDataList(), event.getQuery());
        if(filteredModelList.size() > 0){
            mContactAdapter.clear();
            mContactAdapter.addAll(filteredModelList);
        }
        binding.recyclerView.scrollToPosition(0);

    }









    /*  Private Classes
    ------------------------------------------------------------------------------------------------

        Private classes within this scope to utilize getActivity()
        The data binding pattern, details in layout xmls, decouples design from development
    */
    private class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemContactBinding mBinding;
        private Context mContext;
        private LinearLayout childrenLayout;
        private SalientCalendarContainer sCC;

        private ContactHolder(ItemContactBinding binding){
            super(binding.getRoot());
            mBinding = binding;
            EventBus.getDefault().register(this);
            sCC = new SalientCalendarContainer(Calendar.getInstance());
            mContext = mBinding.getRoot().getContext();
            childrenLayout = mBinding.getRoot().findViewById(R.id.contact_tile_child_container);
            mBinding.getRoot().findViewById(R.id.contact_tile_container).setOnClickListener(this);
            childrenLayout.setVisibility(View.GONE);

            //  Fixes the duplicated expanded views
            setIsRecyclable(false);


            //  Dimens
            int weekSelectorSize = getResources().getDimensionPixelSize(R.dimen.week_selectors);
            int margin = getResources().getDimensionPixelSize(R.dimen.week_selector_margin);
            int marginSides = margin + getResources().getDimensionPixelSize(R.dimen.contact_tile_img_padding);
            int tileHeight = getResources().getDimensionPixelSize(R.dimen.week_selector_height);
            //  Init week selector header
            LinearLayout navLinearLayout = new LinearLayout(mContext);
            navLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    tileHeight);
            navLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            llParams.setMargins(marginSides,margin,marginSides,margin);
            navLinearLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.primary_light, null));

            //  Init left and right week selectors
            ImageView leftSelector = new ImageView(mContext);
            leftSelector.setId(LEFT_SEL_ID);
            leftSelector.setOnClickListener(this);
            leftSelector.setImageResource(R.drawable.ic_baseline_chevron_left_24px);
            LinearLayout.LayoutParams leftSelParams = new LinearLayout.LayoutParams(
                    weekSelectorSize,
                    weekSelectorSize);
            leftSelParams.gravity = Gravity.CENTER_HORIZONTAL;
            leftSelParams.setMargins(margin,margin,margin,0);
            leftSelector.setLayoutParams(leftSelParams);

            TextView tvLeft = new TextView(mContext);
            String leftString = sCC.getLeftMonth() + " " + sCC.getLeftDay();
            tvLeft.setText(leftString);
            tvLeft.setId(LEFT_MONTH_TV_ID);
            LinearLayout.LayoutParams leftTextParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            leftTextParams.gravity = Gravity.LEFT;
            tvLeft.setLayoutParams(leftTextParams);

            ImageView divider = new ImageView(mContext);
            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                    weekSelectorSize,
                    weekSelectorSize);
            dividerParams.gravity = Gravity.CENTER;
            divider.setImageResource(R.drawable.ic_baseline_remove_24px);
            divider.setLayoutParams(dividerParams);

            TextView tvRight = new TextView(mContext);
            String rightString = sCC.getRightMonth() + " " + sCC.getRightDay();
            tvRight.setText(rightString);
            tvRight.setId(RIGHT_MONTH_TV_ID);
            LinearLayout.LayoutParams rightTextParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rightTextParams.gravity = Gravity.RIGHT;
            tvRight.setLayoutParams(rightTextParams);


            ImageView rightSelector = new ImageView(mContext);
            rightSelector.setId(RIGHT_SEL_ID);
            rightSelector.setOnClickListener(this);
            LinearLayout.LayoutParams rightSelParams = new LinearLayout.LayoutParams(
                    weekSelectorSize,
                    weekSelectorSize);
            rightSelParams.gravity = Gravity.CENTER_HORIZONTAL;
            rightSelector.setImageResource(R.drawable.ic_baseline_chevron_right_24px);
            rightSelector.setLayoutParams(rightSelParams);

            navLinearLayout.addView(leftSelector);
            navLinearLayout.addView(tvLeft);
            navLinearLayout.addView(divider);
            navLinearLayout.addView(tvRight);
            navLinearLayout.addView(rightSelector);
            childrenLayout.addView(navLinearLayout, llParams);

            int i = 0;
            for (String day : sCC.getDayArray()){
                //  TODO: Style textviews
                TextView tv = new TextView(mContext);

                int padding = getResources().getDimensionPixelSize(R.dimen.week_selector_padding);
                tv.setPadding(padding,padding,padding,padding);
                tv.setText(day);
                tv.setId(WEEK_IDS[i]);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        tileHeight);

                layoutParams.setMargins(marginSides,0,marginSides,margin);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.primary_light, null));

                tv.setOnClickListener(new DayOnClickListener(day));
                childrenLayout.addView(tv, layoutParams);
                i++;

            }
            //  Binding MVVM
            mBinding.setViewModel(new ContactViewModel());
        }

        public void bind(Contact contact){
            mBinding.getViewModel().setContact(contact);
            mBinding.executePendingBindings();
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void updateUIComponent(UpdateUIComponentEvent event) {
            final UpdateUIComponentEvent currEvent = event;
            getActivity().runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    Object payload = currEvent.getPayload();
                    View view = mBinding.getRoot().findViewById(currEvent.getView());
                    if (payload instanceof String && view instanceof TextView) {


                        ((TextView) view).setText((String) payload);
                    } else if (payload instanceof Integer) {
                        //  TODO: Use if needed
                    }
                }
            });

        }


        private void updateUI(){
            String[] temp = sCC.getDayArray();
            TextView tv = null;
            for (int i = 0; i < SalientCalendarContainer.NUM_WEEKDAYS; i++){
                try {
                    tv = getActivity().findViewById(WEEK_IDS[i]);
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
                if (tv != null){
                    System.out.println("Setting Text: "+temp[i]);
                    EventBus.getDefault().post(new UpdateUIComponentEvent(WEEK_IDS[i],temp[i]));
                }
            }
            TextView tvL = null;
            tvL = getActivity().findViewById(LEFT_MONTH_TV_ID);
            if ( tvL != null){
                EventBus.getDefault().post(new UpdateUIComponentEvent(LEFT_MONTH_TV_ID,
                        sCC.getLeftMonth() + " " + sCC.getLeftDay()));
            }
            TextView tvR = null;
            tvR = getActivity().findViewById(RIGHT_MONTH_TV_ID);
            if ( tvR != null){
                EventBus.getDefault().post(new UpdateUIComponentEvent(RIGHT_MONTH_TV_ID,
                        sCC.getRightMonth() + " " + sCC.getRightDay()));
            }



        }

        @Override
        public void onClick(View view) {
            System.out.println("Clicking some dumb view: \t\tid "+ view.getId());
            if (view.getId() == R.id.contact_tile_container) {
                if (childrenLayout.getVisibility() == View.VISIBLE) {
                    childrenLayout.setVisibility(View.GONE);
                } else {
                    childrenLayout.setVisibility(View.VISIBLE);
                }
            } else if (view.getId() == LEFT_SEL_ID) {
                System.out.println("LEFT SEL");
                sCC.decrementWeek();
                updateUI();
            } else if (view.getId() == RIGHT_SEL_ID) {
                System.out.println("RIGHT SEL");
                sCC.incrementWeek();
                updateUI();
            }
        }

    }


    private class ContactAdapter extends RecyclerView.Adapter<ContactHolder> implements SectionTitleProvider{
        private SortedList<Contact> mData;
        private ArrayList<Contact> duplicateData;

        public ContactAdapter(){
            //  A List type structure duplicates the SortedList bound to the RecyclerView

            //  This is done as SortedList is not a collection and cannot be mapped to a List
            //  or another data structure compatible with the fast scroll implementation.
            //  I suspect the memory allocation cost isn't too steep but further analysis will be
            //  performed once the overall logic and UI components are in place. From then on,
            //  the work will be optimization.
            duplicateData = new ArrayList<>();

            //  The SortedList data structure takes a Class prototype of the Sorted Element and
            //  a SortedListAdaptedCallback which binds mData to RecyclerView.Adapter who controls
            //  the positioning of elements.
            mData = new SortedList<>(Contact.class, new SortedListAdapterCallback<Contact>(this){
                @Override
                public boolean areContentsTheSame(Contact a1, Contact a2) {
                    if(compare(a1,a2) == 0){
                        return true;
                    }
                    return false;
                }
                @Override
                public boolean areItemsTheSame(Contact a1, Contact a2) {
                    if(a1 instanceof Contact && a2 instanceof Contact){
                        return true;
                    }
                    return false;
                }
                @Override
                public void onInserted(int position, int count){
                    notifyItemChanged(position);
                }

                @Override
                public int compare(Contact item1, Contact item2) {
                    return item1.getName().compareToIgnoreCase(item2.getName());
                }
                @Override
                public void onRemoved(int position, int count) {
                    notifyItemRangeRemoved(position, count);
                }

                @Override
                public void onMoved(int fromPosition, int toPosition) {
                    notifyItemMoved(fromPosition, toPosition);
                }
            });

        }

        //  Helper Functions

        public List<Contact> getDataList(){
            return duplicateData;
        }

        public void addAll(List<Contact> contacts) {
            mData.addAll(contacts);
            duplicateData.addAll(contacts);

        }


        public void clear() {
            mData.beginBatchedUpdates();
            //  Remove terminating items to avoid array reshuffling
            while (mData.size() > 0) {
                mData.removeItemAt(mData.size() - 1);
            }
            mData.endBatchedUpdates();
        }

        public void addItem(Contact c){
            mData.add(c);
            duplicateData.add(c);
        }
        public void removeItem(Contact c){
            mData.remove(c);
            duplicateData.remove(c);
        }

        //  Required for SectionTitleProvider implementation of fast scrolling
        @Override
        public String getSectionTitle(int position) {
            //  Sets the display letter of the fast scroll bubble
            return mData.get(position).getName().substring(0, 1);
        }

        @Override
        public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            ItemContactBinding binding = DataBindingUtil
                    .inflate(inflater, R.layout.item_contact, parent, false);
            return new ContactHolder(binding);
        }

        @Override
        public void onBindViewHolder(ContactHolder holder, int position){
            Contact contact = mData.get(position);
            //  Fixes recyclerView expandable item duplication
            holder.setIsRecyclable(false);
            holder.bind(contact);
        }

        @Override public int getItemCount(){
            if (mData == null){ return 0;}
            return mData.size();
        }
    }
}
