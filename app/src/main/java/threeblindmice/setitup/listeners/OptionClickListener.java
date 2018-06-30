package threeblindmice.setitup.listeners;

import android.view.View;

import threeblindmice.setitup.interfaces.NavInterface;

//  Handles fragment option selection with unique IDs (overkill)
public class OptionClickListener implements View.OnClickListener {
    private String id;
    private NavInterface callbacks;

    public OptionClickListener(){
        super();
    }

    public void setId(String newId){
        this.id = newId;
    }

    public void setCallback(NavInterface navInt){
        this.callbacks = navInt;
    }

    @Override
    public void onClick(View v){
        callbacks.onOptionSelected(id);
    }


}
