package threeblindmice.setitup.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Slate on 2018-05-06.
 */

public class ContactsModel {
    private ContactAggregator cAgg;

    List<Contact> contactList = new ArrayList<Contact>();
    public ContactsModel(Context context){
        cAgg = new ContactAggregator();

        testPopulate();
    }

    private void testPopulate(){
        for(int i = 0; i<20;i++){
            contactList.add(new Contact(Integer.toString(i)));
        }



    }

    public List<Contact> getContacts(){
        return contactList;
    }
}
