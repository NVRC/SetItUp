package threeblindmice.setitup.view;

import android.app.PendingIntent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import threeblindmice.setitup.R;

public class SmsFragment extends Fragment implements View.OnClickListener {
    private String smsNum = "6133668488";
    private String smsText = "This is some trial text.";
    public static SmsFragment newInstance(){
        return new SmsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_sms, container, false);


        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        TextView textView = (TextView) getView().findViewById(R.id.sms_num);
        // Use format with "smsto:" and phone number to create smsNumber.
        textView.setText(smsNum);
        // Find the sms_message view.
        TextView smsEditText = (TextView) getView().findViewById(R.id.sms_text);
        smsEditText.setText(smsText);

        Button smsButton = (Button) getView().findViewById(R.id.sms_send);
        smsButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        System.out.println("\t  Clicking send sms");
        // Set the service center address if needed, otherwise null.
        String scAddress = null;
        // Set pending intents to broadcast
        // when message sent and when delivered, or set to null.
        PendingIntent sentIntent = null, deliveryIntent = null;
        // Use SmsManager.
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage
                (smsNum, scAddress, smsText,
                        sentIntent, deliveryIntent);
    }

}
