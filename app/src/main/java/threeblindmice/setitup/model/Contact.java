package threeblindmice.setitup.model;

import android.annotation.TargetApi;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Created by Slate on 2018-05-06.
 */

public class Contact {

    private String name;

    public Contact(String name){
        this.name = name;
    }


    // TODO: Update the generatedDigest to include other identifying info
    private String generateDigest(){
        return name;
    }

    public String getName(){
        return name;
    }

    @TargetApi(26)
    // Returns a String Object of the identityHash in Base64 representation
    public String getHash() {
        try {
            MessageDigest digestContainer = MessageDigest.getInstance("SHA-256");
            digestContainer.update(generateDigest().getBytes());
            return new String(Base64.getEncoder().encodeToString(digestContainer.digest()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // Failure point
        return null;
    }

}




