package threeblindmice.setitup;

import org.junit.Test;

import threeblindmice.setitup.model.Contact;

import static org.junit.Assert.assertTrue;


public class LocalContactThreadUnitTest {

    String testHash;
    String testName;
    public LocalContactThreadUnitTest(){
        testHash = "test";
        testName = "Murphy";
    }

    @Test
    public void testGenerateHash() {

        // Base64 representation
        String knownHash = "n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg=";

        Contact contact = new Contact(testHash);
        assertTrue(knownHash.equals(contact.getHash()));
    }

    @Test
    public void testConstructor() {

        Contact testContact = new Contact(testName);
        assertTrue(testContact.getName().equals(testName)); // Safe due to String equals override
    }
}