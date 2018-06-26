package threeblindmice.setitup.events;

import threeblindmice.setitup.model.Contact;

public class RefreshContactListEvent {
    private Contact contact;
    private boolean addFlag;

    public RefreshContactListEvent(Contact obj, boolean flag){
        if(obj instanceof Contact){
            contact = obj;
            addFlag = flag;
        }
    }

    public Contact getContact(){
        return contact;
    }

    public boolean getFlag(){
        return this.addFlag;
    }
}
