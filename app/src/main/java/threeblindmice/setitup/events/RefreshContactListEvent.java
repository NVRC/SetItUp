package threeblindmice.setitup.events;

import java.util.List;

import threeblindmice.setitup.model.Contact;

public class RefreshContactListEvent {
    public enum State{
        INIT,
        SINGLE
    }

    private Contact contact;
    private boolean addFlag;
    private State state;
    private List<Contact> contacts;

    public RefreshContactListEvent(Contact obj, boolean flag){
        if(obj instanceof Contact){
            contact = obj;
            addFlag = flag;

        }
    }

    public RefreshContactListEvent(List<Contact> contactList, State flag1){
        if(flag1 == State.INIT){
            contacts.addAll(contactList);
        }

    }

    public Contact getContact(){
        return contact;
    }

    public boolean getFlag(){
        return this.addFlag;
    }
    public State getState(){
        return this.state;
    }
}
