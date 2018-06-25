package threeblindmice.setitup.model;

import android.annotation.TargetApi;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Nathaniel Charlebois on 2018-05-06.
 */

public class Contact {

    private String name;
    private String hash;

    // Perhaps a PhoneNumber object should be created, but the expression is so varied
    // REGEX to valid later
    private HashSet<String> numberHashSet = new HashSet<String>();
    /* The below snippet iterator accross all unique items
        Iterator<String> itr = numberHashSet.iterator();
        while(itr.hasNext()){
            currNo = itr.next();
        }
    */



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

    public HashSet<String> getNumbers(){
        return this.numberHashSet;
    }


    public void addPhoneNumber(String newNo){
        //TODO:
        //  Validate # (Handled perhaps in PhoneNumber)
        //  Check for # uniqueness (hashSet)
        numberHashSet.add(newNo);

        // Update the Contacts Hash
        this.hash = generateHash(generateDigest());
    }

    public void addPhoneNumberSet(Set<String> numberSet){
        numberHashSet.addAll(numberSet);
    }

    public String getHash(){
        return this.hash;
    }

    @TargetApi(26)
    // Returns a String Object of the identity hash in Base64 representation
    private String generateHash(String seed) {
        try {
            MessageDigest digestContainer = MessageDigest.getInstance("SHA-256");
            digestContainer.update(seed.getBytes());
            return new String(Base64.getEncoder().encodeToString(digestContainer.digest()));
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
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
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




