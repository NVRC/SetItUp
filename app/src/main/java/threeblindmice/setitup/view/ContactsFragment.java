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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.TreeSet;

import threeblindmice.setitup.R;
import threeblindmice.setitup.databinding.FragmentContactsBinding;
import threeblindmice.setitup.databinding.ItemContactBinding;
import threeblindmice.setitup.events.RefreshContactListEvent;
import threeblindmice.setitup.model.Contact;
import threeblindmice.setitup.model.ContactsModel;
import threeblindmice.setitup.viewmodel.ContactViewModel;

/**
 * Created by Nathaniel Charlebois on 2018-05-08.
 */

public class ContactsFragment extends Fragment {
    private ContactsModel mContactsModel;
    private ContactAdapter mContactAdapter;
    private List<Contact> currData;
    private TreeSet<Contact> currSortedSet;
    private SortedList<Contact> sortedList;
    private boolean likelyUnsorted = false;
    private SortedList.BatchedCallback<Contact> batchedCallback;



    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentContactsBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_contacts, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        binding.recyclerView.setAdapter(mContactAdapter);


        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mContactsModel = new ContactsModel(getActivity());
        mContactAdapter = new ContactAdapter();

        batchedCallback = new SortedList.BatchedCallback<>(
                new SortedListAdapterCallback<Contact>(mContactAdapter){
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
                        super.onInserted(position, count);
                    }

                    @Override
                    public int compare(Contact item1, Contact item2) {
                        return item1.getName().compareTo(item2.getName());
                    }
                });
        sortedList = new SortedList<>(Contact.class, batchedCallback);
        mContactAdapter.setList(sortedList);

    }


    //  Save states
    @Override
    public void onPause(){
        super.onPause();
        mContactsModel.teardown();
    }
    @Override
    public void onDestroy() {


        super.onDestroy();
    }

    private void syncSorted(){

    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onUpdate(RefreshContactListEvent event){

        Contact contact = event.getContact();



        int pos;
        if(event.getFlag()){
            //  Add condition
            sortedList.add(contact);
            batchedCallback.dispatchLastEvent();

            System.out.println("\t\t Add Event Occured"+contact.getName());
            System.out.println("\t\t sortedList:"+sortedList.toString()
            );
            /*
            if(currSortedSet.add(contact)){
                currData = new ArrayList<Contact>(currSortedSet);
                pos = currData.indexOf(contact);
                mContactAdapter.notifyItemInserted(pos);
            }
            */

        } else {
            //  Remove condition
            sortedList.remove(contact);
            batchedCallback.dispatchLastEvent();
            System.out.println("\t\t Rm" +
                    " Event Occured"+contact.getName());
            /*
            pos = currData.indexOf(contact);
            if(pos >- 1){
                if(currSortedSet.remove(contact)) {
                    currData = new ArrayList<Contact>(currSortedSet);
                    mContactAdapter.notifyItemRemoved(pos);
                    mContactAdapter.notifyItemRangeChanged(pos, currData.size());
                }
            }
            */

        }
    }


    // Private classes within this scope to utilize getActivity()
    // The data binding pattern, details in layout xmls, decouples design from development
    // TODO: Style xml
    private class ContactHolder extends RecyclerView.ViewHolder {
        private ItemContactBinding mBinding;

        private ContactHolder(ItemContactBinding binding){
            super(binding.getRoot());
            mBinding = binding;
            mBinding.setViewModel(new ContactViewModel());
        }

        public void bind(Contact contact){
            mBinding.getViewModel().setContact(contact);
            mBinding.executePendingBindings();
        }
    }

    private class ContactAdapter extends RecyclerView.Adapter<ContactHolder>{
        private SortedList<Contact> mContacts;


        public ContactAdapter(){

        }

        public void setList(SortedList<Contact> contacts){
            this.mContacts = contacts;
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
            Contact contact = mContacts.get(position);
            holder.bind(contact);
        }

        @Override public int getItemCount(){
            if(mContacts == null){return 0;}
            return mContacts.size();
        }
    }
}
