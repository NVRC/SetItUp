package threeblindmice.setitup.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import threeblindmice.setitup.model.Contact;

/**
 * Created by Slate on 2018-05-06.
 */



public class ContactViewModel extends BaseObservable {
    //public ObservableMap<Integer, Integer> contactId = new ObservableArrayMap();
    private Contact mContact;
    private Context mContext;



    public ContactViewModel(Context context){
        this.mContext = context;
    }



    @Bindable
    public Drawable getDrawable() {
        return new BitmapDrawable(mContext.getResources(), mContact.getPhoto());
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
