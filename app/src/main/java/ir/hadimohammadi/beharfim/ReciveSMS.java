package ir.hadimohammadi.beharfim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class ReciveSMS extends BroadcastReceiver {

    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        String msgBodys = msgs[i].getServiceCenterAddress();
                        if(msg_from.equals("+989350001400")|msg_from.equals("+989830004757")) {

                            String Code = msgBody.split(":")[1].substring(0,4);
                           // Toast.makeText(context, msgBody, Toast.LENGTH_SHORT).show();
                            Login.et4.setText(Code);
                        }else {
                            String Code = msgBody.split(":")[1].substring(0,4);
                         //  Toast.makeText(context, msgBodys, Toast.LENGTH_SHORT).show();
                            Login.et4.setText(Code);
                        }
                    }
                }catch(Exception e){

                }
            }
        }
    }
}