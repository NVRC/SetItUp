package threeblindmice.setitup.model;

import android.annotation.TargetApi;
import android.graphics.Bitmap;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *  Created by Nathaniel Charlebois on 2018-05-06.
 *
 *
 */

public class Contact  implements Comparable<String>{

    private String name;
    private String hash;
    private Bitmap photo;

    // Perhaps a PhoneNumber object should be created, but the expression is so varied
    // REGEX to valid later
    private HashSet<String> numberHashSet = new HashSet<String>();


    public Contact(String name){
        this.name = name;
        this.hash = generateHash(generateDigest());
    }

    private String generateDigest(){
        return name;
    }

    public String getName(){
        return name;
    }

    public void setPhoto(Bitmap newPhoto){
        this.photo = newPhoto;
    }

    public Bitmap getPhoto(){
        return photo;
    }

    public HashSet<String> getNumbers(){
        return this.numberHashSet;
    }


    public void addPhoneNumber(String newNo){
        //TODO:
        //  Validate # (Handled perhaps in PhoneNumber)
        //  Check for # uniqueness (hashSet)                    DONE
        numberHashSet.add(newNo);

        // Update the Contacts Hash
        this.hash = generateHash(generateDigest());
    }

    public void addPhoneNumberSet(Set<String> numberSet){
        numberHashSet.addAll(numberSet);
    }

    public String getHash(){
        return hash;
    }


    //  Required to implement Comparable<String>
    //  Allows for Contacts to be efficiently sorted and displayed in RecyclerView
    //  Also enables fast scroll
    @Override
    public int compareTo(String nameAlt){
        return name.compareToIgnoreCase(nameAlt);
    }


    // Returns a String Object of the identity hash in Base64 representation
    @TargetApi(23)
    private String generateHash(String seed) {
        try {
            MessageDigest digestContainer = MessageDigest.getInstance("SHA-256");
            digestContainer.update(seed.getBytes());
            return new String(android.util.Base64.encodeToString(digestContainer.digest(), android.util.Base64.DEFAULT));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // Failure point
        return null;
    }

    // Hashing reduces obj evaluation time. Fewer Conditionals
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Contact){
            Contact c = (Contact) obj;
            if (this.getHash().equals(c.getHash())) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        //  Seed values are primes
        return new HashCodeBuilder(17, 31).
                // if deriving: appendSuper(super.hashCode()).
                        append(this.hash).
                        toHashCode();
    }

    @Override
    public String toString(){
        String noString = "";
        Iterator<String> itr = numberHashSet.iterator();
        while(itr.hasNext()){
            noString  += itr.next().toString() + "\n\t";
        }
        return "Name:\t" + this.name +
                "#s :\t" + noString;
    }

}




