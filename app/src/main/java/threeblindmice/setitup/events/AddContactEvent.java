package threeblindmice.setitup.events;

import java.util.List;

import threeblindmice.setitup.model.Contact;

/**
 * Created by Slate on 2018-05-08.
 */

public class AddContactEvent {
    private Contact contact;
    private List<Contact> contacts;
    private boolean flag;
    public AddContactEvent(Contact obj){

            this.contact = obj;
            this.flag = false;

    }
    public AddContactEvent(List<Contact> list){
        this.contacts = list;
        this.flag = true;
    }
    public boolean getListFlag(){
        return flag;
    }

    public List<Contact> getContacts(){
        return contacts;
    }

    public Contact getContact(){
        return contact;
    }

}
