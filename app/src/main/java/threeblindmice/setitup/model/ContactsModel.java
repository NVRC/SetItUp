package threeblindmice.setitup.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import threeblindmice.setitup.R;
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
    private LocalContactThread lct;


    public ContactsModel(Context context) {
        //  Init Vars
        this.mContext = context;
        this.synchronizedContacts = new ConcurrentHashMap<String, Contact>();

        EventBus.getDefault().register(this);

        this.lct = new LocalContactThread(mContext);
        this.lct.start();
    }

    public void teardown() {
        lct.interrupt();
    }


    //  Generic HashMap wrapper to return a List usable for a `RecyclerView` or other android res
    public List<Contact> getContacts(){
        Collection<Contact> values = synchronizedContacts.values();
        contactList = new ArrayList<>(values);
        return contactList;
    }

    //
    private void publishContactToView(Contact c, boolean addFlag){

    }

    /*  Asynchronous Event Listeners
    ------------------------------------------------------------------------------------------------

        Event published by `LocalContactThread`

        Two cases:

            1)  INIT
                    The App has just been launched and all contacts need to be added to this Model
                    bound to the ViewModel, to the View and thus displayed.

            2)  SINGLE
                    A new contact has been found in `ContactsContract.Contacts` and is added to
                        Model -> ViewModel -> View

    */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void updateContact(AddContactEvent newEvent) {
        boolean listFlag = newEvent.getListFlag();
        //  Assert
        //      Model State vs enum INIT flag
        //      listFlag: event contains a List
        if (state == State.INIT && listFlag) {
            //  Build a temporary test load of Contacts
            //  TODO: Remove test Contacts on deployment
            char[] alpha = "abcdefghijklmnopqrstuvwxyz".toCharArray();
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wallpaper_icon);
            List<Contact> load = new ArrayList<>();
            for(int i = 0; i < alpha.length; i++ ){
                //  Awful one liner to create beefier names for testing
                Contact c =  new Contact(new String(new char[12]).replace("\0", Character.toString(alpha[i])));
                c.setPhoto(bitmap);
                load.add(c);
            }
            load.addAll(newEvent.getContacts());
            EventBus.getDefault().post(new RefreshContactListEvent(load,
                    State.INIT));
            state = State.SINGLE;
        }
        //  Assert
        //      Model State vs enum SINGLE flag
        //  Adds the contact to a synchronized hash set where contacts are unique, and identified
        //  by a hashed contact information digest
        else if(state == State.SINGLE && !listFlag){
            Contact c = newEvent.getContact();
            String cHash = c.getHash();
            //  Key is a hashed digest of the contact to avoid collisions
            //  putResult returns a Contact if that contact already exists
            Object putResult = synchronizedContacts.put(cHash, c);
            //  Update the existing contact's phone number set
            if (putResult instanceof Contact) {
                //  Merge both phone number Sets
                c.addPhoneNumberSet(((Contact) putResult).getNumbers());
                synchronizedContacts.put(cHash, c);
            } else if (putResult == null) {
                //  New Contact added
                //  Post Contact to ViewModel Adapter
                EventBus.getDefault().post(new RefreshContactListEvent( c, true));
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void removeContact(RemoveContactEvent newEvent){
        Contact c = newEvent.getContact();
        String cHash = c.getHash();
        synchronizedContacts.remove(cHash);
        EventBus.getDefault().post(new RefreshContactListEvent( c, false));
    }
}
