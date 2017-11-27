package com.som_itsolutions.phonelocator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by som on 19/6/16.
 */
public class SMSBroadcastReceiver extends BroadcastReceiver{

    private String mAlternateNumber;
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SMSBroadcastReceiver";

    public static double lat;
    public static double lon;


    Intent mLocationSendServiceIntent;
    //private static boolean mediaPlayerOncePlayed = false;

    public SMSBroadcastReceiver() {
        mLocationSendServiceIntent = new Intent(MainActivity.getmMainActivity(), LocationSendService.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Intent recieved: " + intent.getAction());

        mAlternateNumber = PhoneLocatorService.mAlternateNumber;

        if (intent.getAction() == SMS_RECEIVED) {


            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[])bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String format = bundle.getString("format");
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    }
                    else{
                        messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    }

                }
                if (messages.length > -1) {
                    Log.i(TAG, "Message recieved: " + messages[0].getMessageBody());
                    MainActivity.smsMessage = messages[0].getMessageBody();
                    MainActivity.smsFromNumber = messages[0].getDisplayOriginatingAddress();

                    if(MainActivity.smsMessage.toLowerCase().contains("find my phone")){
                        //Start the service

                       /* //start the alert sound
                        if(PhoneLocatorService.mediaPlayerOncePlayed){
                            try {
                                //Log.i("PhoneLocatorService", "Prepare");
                                MainActivity.mediaPlayer.prepare();
                                Log.i("PhoneLocatorService", "Prepare");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        MainActivity.mediaPlayer.setLooping(true);
                        MainActivity.mediaPlayer.start();

                        PhoneLocatorService.mediaPlayerOncePlayed = true;*/

                        //context.startService(mLocationSendServiceIntent);

                        Intent displayMessageIntent = new Intent(context, DisplayAlternateNumberActivity.class);
                        displayMessageIntent.putExtra("ALT_NUMBER", mAlternateNumber);

                        displayMessageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(displayMessageIntent);

                    }

                    if(MainActivity.smsMessage.toLowerCase().contains("i am here")){
                        //Display the Google Map
                        String str = "i am here";
                        String stringToBeParsed = MainActivity.smsMessage;
                        int firstHashPosition = stringToBeParsed.indexOf("#");

                        String latSubstring = stringToBeParsed.substring(str.length(),firstHashPosition);
                        String lonSubstring = stringToBeParsed.substring(firstHashPosition + 1);

                        lat = Double.parseDouble(latSubstring);
                        lon = Double.parseDouble(lonSubstring);

                        Log.i("SMSReceiver Latitude", Double.toString(lat));
                        Log.i("SMSReceiver Longitude", Double.toString(lon));
                        
                       //Now display the Map with pin having this lat & lon
                        Intent iMapActivity = new Intent(context, PhoneLocationMapsActivity.class);
                        iMapActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        iMapActivity.putExtra("lat", lat);
                        iMapActivity.putExtra("lon", lon);
                        context.startActivity(iMapActivity);


                    }
                }
            }
        }
    }


}