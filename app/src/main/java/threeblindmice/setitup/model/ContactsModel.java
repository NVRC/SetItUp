package threeblindmice.setitup.model;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import threeblindmice.setitup.events.AddContactEvent;
import threeblindmice.setitup.events.RefreshContactListEvent;
import threeblindmice.setitup.events.RemoveContactEvent;
import threeblindmice.setitup.util.State;

/**
 * Created by Slate on 2018-05-06.
 */



public class ContactsModel {
    //  Thinking about the trade off using a hashmap vs treeset
    //  both concurrent of course
    private ConcurrentHashMap synchronizedContacts;
    private ArrayList<Contact> contactList;
    private Context mContext;
    private State state = State.INIT;
    private static int CONTACT_DEBOUNCE = 3;
    private int debounce;

    LocalContactThread lct;


    public ContactsModel(Context context) {
        this.mContext = context;

        synchronizedContacts = new ConcurrentHashMap<String, Contact>();
        EventBus.getDefault().register(this);
        //  Pass an empty list to disable testing
        List<Contact> testLoad = Collections.emptyList();
        this.lct = new LocalContactThread(mContext);

        this.lct.start();

    }

    public void teardown() {
        lct.interrupt();
    }


    public List<Contact> getAlphaSortedList() {
        List<Contact> temp;
        //  TODO: Purge
        if (contactList != null) {
            temp = new ArrayList<Contact>(contactList);
            Collections.sort(temp, new Comparator<Contact>() {
                @Override
                public int compare(Contact c1, Contact c2) {
                    return c1.compareTo(c2.getName());

                }
            });
        } else {
            temp = new ArrayList<>();
        }
        return temp;
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void updateContact(AddContactEvent newEvent) {
        boolean listFlag = newEvent.getListFlag();
        if (state == threeblindmice.setitup.util.State.INIT && listFlag) {
            System.out.println(newEvent.getContacts());
            EventBus.getDefault().post(new RefreshContactListEvent(newEvent.getContacts(),
                    threeblindmice.setitup.util.State.INIT));
            System.out.println("Post to UI");
            state = threeblindmice.setitup.util.State.SINGLE;
        } else if(state ==threeblindmice.setitup.util.State.SINGLE && !listFlag){
            Contact c = newEvent.getContact();
            String cHash = c.getHash();
            // Key is a hashed digest of the contact to avoid collisions
            Object putResult = synchronizedContacts.put(cHash, c);
            if (putResult instanceof Contact) {
                //  Merge both phone number Sets
                c.addPhoneNumberSet(((Contact) putResult).getNumbers());
                synchronizedContacts.put(cHash, c);
            } else if (putResult == null) {
                //  New Contact Added
                publishContactToView(c, true);
            }
        }

    }





    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void removeContact(RemoveContactEvent newEvent){

        Contact c = newEvent.getContact();
        String cHash = c.getHash();
        synchronizedContacts.remove(cHash);
        publishContactToView(c,false);
    }

    public List<Contact> getContacts(){
        Collection<Contact> values = synchronizedContacts.values();
        contactList = new ArrayList<>(values);
        return contactList;
    }

    private void publishContactToView(Contact c, boolean addFlag){
        EventBus.getDefault().post(new RefreshContactListEvent( c, addFlag));
    }
}
