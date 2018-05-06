package threeblindmice.setitup.viewmodel;

import java.util.Observable;

import android.databinding.ObservableMap;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.util.ArrayMap;
import android.databinding.ObservableArrayMap;

/**
 * Created by Slate on 2018-05-06.
 */

public class ViewModel extends Observable {
    public ObservableMap<Integer, Integer> contactId = new ObservableArrayMap();
}
