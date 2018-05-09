package threeblindmice.setitup.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import threeblindmice.setitup.model.Contact;
import threeblindmice.setitup.model.ContactsModel;

/**
 * Created by Slate on 2018-05-06.
 */

public class ContactViewModel extends BaseObservable {
    //public ObservableMap<Integer, Integer> contactId = new ObservableArrayMap();
    private Contact mContact;
    private ContactsModel mContactsModel;

    public ContactViewModel(ContactsModel contactsModel){
        mContactsModel = contactsModel;
    }

    public Contact getContact(){
        return mContact;
    }

    @Bindable
    public String getName(){
        return mContact.getName();
    }
    public void setContact(Contact contact){
        mContact = contact;
        notifyChange();
        // Potential to optimize performance by using notifyPropertyChanged(Br.title) to update
        // fields independently. Likely unnecessary given the lifecycle of contacts.
    }
}
