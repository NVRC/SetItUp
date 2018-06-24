package threeblindmice.setitup.events;

import java.util.List;

import threeblindmice.setitup.model.Contact;

public class RefreshContactListEvent {
    private List<Contact> contacts;

    public RefreshContactListEvent(List<Contact> obj){
        if(obj instanceof List){
            contacts = obj;
        }
    }

    public List<Contact> getContacts(){
        return contacts;
    }
}
