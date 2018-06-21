package threeblindmice.setitup.model;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Slate on 2018-05-06.
 */



public class ContactAggregator {

    private ConcurrentHashMap synchronizedContacts;

    public ContactAggregator(){
        synchronizedContacts = new ConcurrentHashMap<String,Contact>();

    }

    public void updateContact(Contact newContact){
        // Key is a hashed digest of the contact to avoid collisions
        synchronizedContacts.put(newContact.getHash(),newContact);

    }


}
