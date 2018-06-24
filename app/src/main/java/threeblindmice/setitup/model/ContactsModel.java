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

/**
 * Created by Slate on 2018-05-06.
 */

public class ContactsModel {
    private ContactAggregator cAgg;
    private ConcurrentHashMap synchronizedContacts;
    private ArrayList<Contact> contactList;



    public ContactsModel(Context context){
        cAgg = new ContactAggregator(context);
        synchronizedContacts = new ConcurrentHashMap<String,Contact>();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void updateContact(AddContactEvent newEvent){
        Contact c = newEvent.getContact();
        String cHash = c.getHash();
        // Key is a hashed digest of the contact to avoid collisions
        Object putResult = synchronizedContacts.put(cHash,c);

        if (putResult instanceof Contact){
            //  TODO:
            //  Check if phone # have been updated (add functionality to Contact.class)


        } else {
            System.out.println(contactList.toString());
            EventBus.getDefault().post(new RefreshContactListEvent(new ArrayList<>(this.getContacts())));
        }

    }

    public List<Contact> getContacts(){
        Collection<Contact> values = synchronizedContacts.values();
        contactList = new ArrayList<>(values);
        return contactList;
    }
}
