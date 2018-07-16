package threeblindmice.setitup.events;

import android.view.View;

public class UpdateUIComponentEvent {

    private Object payload;
    private int target;

    public UpdateUIComponentEvent(int view, Object obj){
        this.payload = obj;
        this.target = view;
    }

    public int getView(){
        return target;
    }

    public Object getPayload(){
        return payload;
    }
}
