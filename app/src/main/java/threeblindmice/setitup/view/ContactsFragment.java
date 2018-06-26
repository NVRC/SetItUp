package threeblindmice.setitup.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

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
    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mContactsModel = new ContactsModel(getActivity());
        currData = mContactsModel.getContacts();
        mContactAdapter = new ContactAdapter(currData);
        FragmentContactsBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_contacts, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        binding.recyclerView.setAdapter(mContactAdapter);


        return binding.getRoot();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdate(RefreshContactListEvent event){
        Contact contact = event.getContact();
        int pos;
        if(event.getFlag()){
            //  Add condition
                currData.add(contact);
                pos = currData.indexOf(contact);
                mContactAdapter.notifyItemInserted(pos);

        } else {
            // Remove condition
            pos = currData.indexOf(contact);
            if(pos >- 1){
                currData.remove(pos);
                mContactAdapter.notifyItemRemoved(pos);
                mContactAdapter.notifyItemRangeChanged(pos, currData.size());
            }

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
        private List<Contact> mContacts;


        public ContactAdapter(List<Contact> contacts){
            mContacts = contacts;
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
