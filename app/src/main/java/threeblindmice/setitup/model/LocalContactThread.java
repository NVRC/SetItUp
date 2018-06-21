package threeblindmice.setitup.model;

public class LocalContactThread extends Thread {

    private static final int UPDATE_PERIOD = 3000; // milliseconds

    @Override
    public void run(){
        //TODO
        // Periodically check local contacts for updates


        try{
            Thread.sleep(UPDATE_PERIOD);
        } catch (InterruptedException e){
            e.printStackTrace();
            // Implement thread.interrupt() behavior here
        }


    }
}
