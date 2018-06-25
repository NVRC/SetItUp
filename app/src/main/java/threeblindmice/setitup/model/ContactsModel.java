package threeblindmice.setitup.model;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import threeblindmice.setitup.events.AddContactEvent;
import threeblindmice.setitup.events.RefreshContactListEvent;
import threeblindmice.setitup.events.RemoveContactEvent;

/**
 * Created by Slate on 2018-05-06.
 */

public class ContactsModel {

    private ConcurrentHashMap synchronizedContacts;
    private ArrayList<Contact> contactList;
    private Context mContext;


    public ContactsModel(Context context){
        mContext = context;

        synchronizedContacts = new ConcurrentHashMap<String,Contact>();
        EventBus.getDefault().register(this);

        LocalContactThread lct = new LocalContactThread(mContext);
        lct.start();
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void updateContact(AddContactEvent newEvent){
        Contact c = newEvent.getContact();
        String cHash = c.getHash();
        // Key is a hashed digest of the contact to avoid collisions
        Object putResult = synchronizedContacts.put(cHash,c);
        if (putResult instanceof Contact){
            //  Merge both phone number Sets
            c.addPhoneNumberSet(((Contact) putResult).getNumbers());
            synchronizedContacts.put(cHash,c);
        } else if (putResult == null){
            //  New Contact Added
            publishContactsToView();
        }

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void updateContact(RemoveContactEvent newEvent){
        Contact c = newEvent.getContact();
        String cHash = c.getHash();
        synchronizedContacts.remove(cHash);
        publishContactsToView();
    }

    public List<Contact> getContacts(){
        Collection<Contact> values = synchronizedContacts.values();
        contactList = new ArrayList<>(values);
        return contactList;
    }

    private void publishContactsToView(){
        EventBus.getDefault().post(new RefreshContactListEvent(new ArrayList<>(this.getContacts())));
    }
}
