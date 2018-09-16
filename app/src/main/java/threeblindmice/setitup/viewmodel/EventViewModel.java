package threeblindmice.setitup.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.concurrent.TimeUnit;

import me.everything.providers.android.calendar.Event;

public class EventViewModel extends BaseObservable {
    private Event mEvent;

    public EventViewModel() {

    }

    @Bindable
    public String getTitle() {
        return mEvent.title;
    }

    @Bindable
    public String getStart(){
        long millis = mEvent.dTStart;
        return  String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    @Bindable
    public String getEnd(){
        long millis = mEvent.dTend;
        return  String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }


    public void setEvent(Event event) {
        mEvent = event;
        notifyChange();
        // Potential to optimize performance by using notifyPropertyChanged(Br.title) to update
        // fields independently. Likely unnecessary given the lifecycle of contacts.
    }
}
