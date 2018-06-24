package threeblindmice.setitup.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import threeblindmice.setitup.events.AddContactEvent;

public class LocalContactThread extends Thread {

    private static final int UPDATE_PERIOD = 10000; // milliseconds
    private Context mContext;

    public LocalContactThread(Context context){
        mContext = context;
    }

    @Override
    public void run(){
        while(true) {
            //TODO
            // Periodically check local contacts for updates
            try {
                /*
                List<Contact> contacts = queryAllContacts();
                for (Iterator<Contact> i = contacts.iterator(); i.hasNext(); ) {
                    Contact item = i.next();
                    System.out.println(item);
                }
                */
                queryAllContacts();
                Thread.sleep(UPDATE_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
                // Implement thread.interrupt() behavior here
            }
        }

    }

    private List<Contact> queryAllContacts(){
        List<Contact> contacts = new ArrayList<>();
        ContentResolver cr = mContext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null,
                null,null, null,null);
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
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
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

        return contacts;
    }
}
