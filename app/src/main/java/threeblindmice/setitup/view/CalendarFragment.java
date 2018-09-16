package threeblindmice.setitup.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.everything.providers.android.calendar.Calendar;
import me.everything.providers.android.calendar.CalendarProvider;
import threeblindmice.setitup.R;

public class CalendarFragment extends Fragment  {


    public static CalendarFragment newInstance(){
        return new CalendarFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();

        CalendarProvider calendarProvider = new CalendarProvider(getContext());
        List<Calendar> cals = calendarProvider.getCalendars().getList();



    }



}
