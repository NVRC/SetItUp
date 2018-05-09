package threeblindmice.setitup.events;

import threeblindmice.setitup.model.Contact;

/**
 * Created by Slate on 2018-05-08.
 */

public class AddContactEvent {
    private Contact contact;

    public AddContactEvent(Contact obj){
        if(obj instanceof Contact){
            contact = obj;
        }
    }

    public Contact getContact(){
        return contact;
    }

}
