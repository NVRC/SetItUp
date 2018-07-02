package threeblindmice.setitup.events;

import java.util.List;

import threeblindmice.setitup.model.Contact;
import threeblindmice.setitup.util.State;

public class RefreshContactListEvent {


    private Contact contact;
    private boolean addFlag;
    private State state = null;
    private List<Contact> contacts;

    public RefreshContactListEvent(Contact obj, boolean flag){
        if(obj instanceof Contact){
            contact = obj;
            addFlag = flag;
            this.state = State.SINGLE;


        }
    }

    public RefreshContactListEvent(List<Contact> contactList, State newState){


            this.contacts = contactList;
            this.state = newState;


    }

    public Contact getContact(){
        return contact;
    }
    public List<Contact> getContacts(){
        return contacts;
    }
    public boolean getFlag(){
        return this.addFlag;
    }
    public State getState(){
        return this.state;
    }
}
