package threeblindmice.setitup.events;

import threeblindmice.setitup.model.Contact;

/**
 * Created by Slate on 2018-05-08.
 */

public class RemoveContactEvent {
    private Contact contact;

    public RemoveContactEvent(Contact obj){
        if(obj instanceof Contact){
            contact = obj;
        }
    }

    public Contact getContact(){
        return contact;
    }

}
