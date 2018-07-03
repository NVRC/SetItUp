package threeblindmice.setitup.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Bitmap;

import threeblindmice.setitup.model.Contact;

/**
 * Created by Slate on 2018-05-06.
 */



public class ContactViewModel extends BaseObservable {
    private Contact mContact;



    public ContactViewModel(){

    }

    public Contact getContact(){
        return mContact;
    }

    @Bindable
    public Bitmap getPhoto(){ return mContact.getPhoto();}

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
