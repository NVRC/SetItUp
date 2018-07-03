package threeblindmice.setitup.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import threeblindmice.setitup.R;
import threeblindmice.setitup.databinding.FragmentContactsBinding;
import threeblindmice.setitup.databinding.ItemContactBinding;
import threeblindmice.setitup.events.QueryEvent;
import threeblindmice.setitup.events.RefreshContactListEvent;
import threeblindmice.setitup.model.Contact;
import threeblindmice.setitup.model.ContactsModel;
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
        FastScroller fastScroller = getActivity().findViewById(R.id.fastscroll);
        fastScroller.setRecyclerView(binding.recyclerView);

        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mContactAdapter = new ContactAdapter();
        mContactsModel = new ContactsModel(getActivity());
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
        State tempState = event.getState();
        if(tempState == State.INIT){
            mContactAdapter.clear();
            mContactAdapter.addAll(event.getContacts());
        } else if (tempState == State.SINGLE){
            int pos;
            Contact contact = event.getContact();
            //  All animations, sorting, etc. handled by SortedListAdapter for RecyclerView
            if(event.getFlag()){
                //  Add condition
                mContactAdapter.addItem(contact);
            } else if (!event.getFlag()) {
                //  Remove condition
                mContactAdapter.removeItem(contact);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleQueryEvent(QueryEvent event){
        // Here is where we are going to implement the filter logic
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
    private class ContactHolder extends RecyclerView.ViewHolder {
        private ItemContactBinding mBinding;

        private ContactHolder(ItemContactBinding binding){
            super(binding.getRoot());
            mBinding = binding;
            mBinding.setViewModel(new ContactViewModel(getActivity()));

        }

        public void bind(Contact contact){
            mBinding.getViewModel().setContact(contact);
            mBinding.executePendingBindings();
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
            holder.bind(contact);
        }

        @Override public int getItemCount(){
            if (mData == null){ return 0;}
            return mData.size();
        }
    }
}
