package threeblindmice.setitup.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import threeblindmice.setitup.R;

public class EmptyFragment extends Fragment  {


    public static EmptyFragment newInstance(){
        return new EmptyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_empty, container, false);



        return view;
    }




}
