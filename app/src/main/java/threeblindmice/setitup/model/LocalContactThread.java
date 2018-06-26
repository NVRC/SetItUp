package threeblindmice.setitup.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import threeblindmice.setitup.events.AddContactEvent;
import threeblindmice.setitup.events.RemoveContactEvent;

public class LocalContactThread extends Thread {

    private static final int UPDATE_PERIOD = 10000; // milliseconds
    private Context mContext;
    private List<Contact> mTestLoad;
    private boolean testBool;

    public LocalContactThread(Context context, List<Contact> testLoad){
        mContext = context;
        mTestLoad = testLoad;
        if (testLoad.isEmpty()){
            testBool = true;
        }
    }

    @Override
    public void run(){
        Set<Contact> baseSet = new HashSet<Contact>(queryAllContacts());
        while(true) {
            try {

                //  Contacts added during the sleep period are handled

                Thread.sleep(UPDATE_PERIOD);
                Set<Contact> newSet = new HashSet<Contact>(queryAllContacts());
                /*
                Collection newContactsToAdd = CollectionUtils.disjunction(baseSet, newSet);
                for (Iterator<Contact> itr = newContactsToAdd.iterator(); itr.hasNext();){
                    Contact tempContact = itr.next();
                    // TODO: Check if this just creates pointers to one contact
                    EventBus.getDefault().post(new AddContactEvent(tempContact));
                }
                */
                Set<Contact> tempBase = new HashSet<>(baseSet);
                baseSet.removeAll(newSet);
                Set<Contact> contactsToRemove = baseSet;
                newSet.removeAll(tempBase);
                Set<Contact> contactsToAdd = newSet;

                //  Only preforming operations on new or updating Contacts saves RecyclerView
                //  transactions and supports add() and remove() animations

                for (Iterator<Contact> i = contactsToAdd.iterator(); i.hasNext(); ) {
                    Contact item = i.next();
                    EventBus.getDefault().post(new AddContactEvent(item));
                }
                for (Iterator<Contact> i = contactsToRemove.iterator(); i.hasNext(); ) {
                    Contact item = i.next();
                    EventBus.getDefault().post(new RemoveContactEvent(item));
                }

                //  Reset T1
                baseSet = new HashSet<Contact>(queryAllContacts());
            } catch (InterruptedException e) {
                e.printStackTrace();
                // Implement thread.interrupt() behavior here
            }
        }

    }

    private List<Contact> queryAllContacts(){
        List<Contact> contacts = new ArrayList<>(mTestLoad);
        if(!testBool) {
            ContentResolver cr = mContext.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null,
                    null, null, null, null);
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Contact tempContact = new Contact(name);

                    if (Integer.parseInt(cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            tempContact.addPhoneNumber(phoneNo);
                        }
                        pCur.close();
                    }
                    contacts.add(tempContact);
                    // TODO: Check if this just creates pointers to one contact
                    EventBus.getDefault().post(new AddContactEvent(tempContact));
                }
            }
        } else{
            //  TODO: Add query testing features
        }

        return contacts;
    }
}
