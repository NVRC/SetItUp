package threeblindmice.setitup.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
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
    private threeblindmice.setitup.util.State state = threeblindmice.setitup.util.State.INIT;
    private boolean init = true;


    public LocalContactThread(Context context){
        mContext = context;
    }

    @Override
    public void run(){
        //  Fetch the initial collection of Contacts (t0)
        Set<Contact> baseSet = new HashSet<>(queryAllContacts());
        while(!this.isInterrupted()) {
            try {
                //  Wait some amount of time before fetching the new collection (t1)
                Thread.sleep(UPDATE_PERIOD);
                Set<Contact> newSet = new HashSet<>(queryAllContacts());

                //  Create a temp collection to find  elements found in (t0) and !(t1)
                Set<Contact> tempBase = new HashSet<>(baseSet);
                baseSet.removeAll(newSet);
                Set<Contact> contactsToRemove = baseSet;

                //  Determine which elements are found in (t1) and !(t0)
                newSet.removeAll(tempBase);
                Set<Contact> contactsToAdd = newSet;

                //  Only preforming operations on new or updating Contacts saves RecyclerViewer
                //  transactions and supports add() and remove() animations
                for (Iterator<Contact> i = contactsToAdd.iterator(); i.hasNext(); ) {
                    Contact item = i.next();
                    //  Notify ViewModel adapter to add a contact from the RecyclerView
                    EventBus.getDefault().post(new AddContactEvent(item));
                }
                for (Iterator<Contact> i = contactsToRemove.iterator(); i.hasNext(); ) {
                    Contact item = i.next();
                    //  Notify ViewModel adapter to remove a contact from the RecyclerView
                    EventBus.getDefault().post(new RemoveContactEvent(item));
                }

                //  Fetch new base set (t0)
                baseSet = new HashSet<>(queryAllContacts());
            } catch (InterruptedException e) {
                // Implement thread.interrupt() behavior here
                return;
            }
        }
    }

    private List<Contact> queryAllContacts(){
        List<Contact> contacts = new ArrayList<>();
        //
        ContentResolver cr = mContext.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null,
                    null, null, null, null);
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    int idLong = cur.getColumnIndex(ContactsContract.Contacts._ID);
                    String id = cur.getString(idLong);
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Bitmap photo = null;

                    try {
                        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(cr,
                                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, idLong));

                        if (inputStream != null) {
                            photo = BitmapFactory.decodeStream(inputStream);
                            inputStream.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    Contact tempContact = new Contact(name);
                    tempContact.setPhoto(photo);
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
                }
            }
            if(init){
                EventBus.getDefault().post(new AddContactEvent(contacts));
            }
        return contacts;
    }
}
