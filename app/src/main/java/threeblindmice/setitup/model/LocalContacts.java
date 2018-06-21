package threeblindmice.setitup.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.List;

public class LocalContacts {

    public List<Contact> contacts;
    private Context mContext;


    public LocalContacts(Context context){
        mContext = context;
    }

    public List<Contact> getContacts(){
        ContentResolver cr = mContext.getContentResolver();

        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null);

        return null;
    }
}
