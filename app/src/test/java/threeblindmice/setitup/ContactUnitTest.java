package threeblindmice.setitup;

public class ContactUnitTest {

    String testHash;
    String testName;
    String testNo;
    public ContactUnitTest(){
        testHash = "test";
        testName = "Murphy";
        testNo = "8005550100";

    }

/*
TODO: Resolve JAR stub dependencies by changing build order or injecting Android.* jars


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

    @Test
    public void testPhoneNumbersHashSetUniqueness(){
        HashSet<String> testHashSet = new HashSet<String>();
        Contact testContact = new Contact(testName);

        // First #
        testContact.addPhoneNumber(testNo);
        testHashSet.add(testNo);

        // Second #
        String temp = testNo.replace("1","2");
        testContact.addPhoneNumber(temp);
        testHashSet.add(temp);

        //
        testContact.addPhoneNumber(testNo);
        assertTrue(testContact.getNumbers().equals(testHashSet));

    }

     */
}