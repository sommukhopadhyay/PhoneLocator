package com.som_itsolutions.phonelocator;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by som on 20/6/16.
 */
public class LocationSendService extends IntentService {
    private LocationManager lm;
    private double lat = 0.0;
    private double lon = 0.0;
    List<Address> address;
    static String premise = "";
    static String locality = "";
    static String postalCode = "";
    static String countryStr = "";
    static String thoroughfare = "";
    static String subThoroughfare = "";
    static String subLocality = "";
    private String smsMessageStr = "I am here";
    static public Timer mTimer;
    AudioManager am;
    static MediaPlayer mediaPlayer;
    boolean mSirenPlayedFirstTime = false;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LocationSendService(String name) {
        super(name);

    }

    public void onCreate ()
    {
        super.onCreate();
        lm = (LocationManager) (getApplicationContext().getSystemService(Context.LOCATION_SERVICE));
        am = (AudioManager)(getApplicationContext().getSystemService(Context.AUDIO_SERVICE));
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.siren);
        mSirenPlayedFirstTime = false;
    }

    public LocationSendService() {
        super("LocationSendService");
        //lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    class mylocationlistener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            //int counter = 0;
            if (location != null) {
                lon = location.getLongitude();
                lat = location.getLatitude();

            }
        }

        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Location location = null;
        LocationListener ll = new mylocationlistener();
        try {
            //mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 35000, 0, ll);
                    Log.d("Network", "Network");
                    if (lm != null) {
                        location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            Log.i("Network Latitude :", Double.toString(lat));
                        }
                    }
                }
                //get the location by gps
                if (isGPSEnabled) {
                    if (location == null) {
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 35000, 0, ll);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (lm != null) {
                            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                                Log.i("GPS Location: ", Double.toString(lat));
                            }
                        }
                    }
                    /////Test
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        address = geocoder.getFromLocation(lat, lon, 1);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if(address != null && address.size() != 0){
                        premise = address.get(0).getPremises();
                        locality = address.get(0).getLocality();
                        postalCode = address.get(0).getPostalCode();
                        //country = address.get(0).getCountryName();
                        thoroughfare = address.get(0).getThoroughfare();
                        subThoroughfare = address.get(0).getSubThoroughfare();
                        subLocality = address.get(0).getSubLocality();
                    }
                    /////
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        initTimer();
    }

    public void initTimer() {
        endTimer();
        mTimer = new Timer(true);
        mTimer.scheduleAtFixedRate(new RepeatTask(), 5000, 900000);
    }

    public void endTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }

       /*if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }*/
    }

    private final class RepeatTask extends TimerTask {

        @Override
        public void run() {
            sendSMSAndPlaySiren(MainActivity.smsFromNumber,smsMessageStr);
        }
    }
    private void sendSMSAndPlaySiren(String phoneNumber, String message)/* throws IOException*/
    {
        String newLine = System.getProperty("line.separator");
        String finalSMSString = "";// = message;
        SmsManager sms = SmsManager.getDefault();

        boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if((isNetworkEnabled == true) || (isGPSEnabled == true)){
            finalSMSString = message + lat + "#" + lon ;
            Log.i("SMS Message Latitude:" , Double.toString(lat));
            Log.i("SMS Messgae Longitude: ", Double.toString(lon));
        }

        else{
            finalSMSString = message;
        }
        ArrayList<String> msgStringArray = sms.divideMessage(finalSMSString);

        sms.sendMultipartTextMessage(phoneNumber, null, msgStringArray, null, null);

        if (!mSirenPlayedFirstTime){
            playSiren();
            mSirenPlayedFirstTime = true;
        }
    }

    private void playSiren(){
        if(am.isMicrophoneMute()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                am.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
            }
            else {
                am.setStreamMute(AudioManager.STREAM_ALARM, false);
            }
        }
        /*PhoneLocatorService.*/mediaPlayer.setLooping(true);
        /*PhoneLocatorService.*/mediaPlayer.start();

        /*mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.seekTo(0);
                mp.start();
            }
        });*/

        //PhoneLocatorService.mediaPlayerOncePlayed = true;
    }
}
