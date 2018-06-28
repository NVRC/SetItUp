package threeblindmice.setitup.events;

public class UpdateFragmentEvent {

    private String fragmentTag;

    public UpdateFragmentEvent(String tag){
       this.fragmentTag = tag;
    }

    public String getTag() {
        return fragmentTag;
    }
}
