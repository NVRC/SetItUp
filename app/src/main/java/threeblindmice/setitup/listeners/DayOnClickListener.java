package threeblindmice.setitup.listeners;

import android.view.View;


//  TODO: Refactor to use day of year
public class DayOnClickListener implements View.OnClickListener {

    String day;

    public DayOnClickListener(String dayOfWeek){
        this.day = dayOfWeek;
    }

    @Override
    public void onClick(View view){
        //  TODO: Handle context switch due to day selection
        System.out.println(day+" was selected");
    }
}
