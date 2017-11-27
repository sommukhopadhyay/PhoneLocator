package com.som_itsolutions.phonelocator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class DisplayAlternateNumberActivity extends AppCompatActivity {

    private String mAlternateNumber;
    TextView mDisplayMessage;
    /*MediaPlayer mediaPlayer;
    AudioManager am;*/
    //private PowerManager.WakeLock mWakeLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_alternate_number);


        /*am = (AudioManager)(getApplicationContext().getSystemService(Context.AUDIO_SERVICE));
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.siren);*/

        /*PowerManager manager =
                (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, DisplayAlternateNumberActivity.class.getName());
        mWakeLock.acquire();*/
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);


        Intent i = getIntent();
        mAlternateNumber = i.getStringExtra("ALT_NUMBER");

        //mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);

       /* if(PhoneLocatorService.mediaPlayerOncePlayed){
            try {
                //Log.i("PhoneLocatorService", "Prepare");
                mediaPlayer.prepare();
                Log.i("DisplayAlternateNumber", "Prepare");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/




        //PhoneLocatorService.mediaPlayerOncePlayed = true;


       /* if(am.isMicrophoneMute()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                am.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
            }
            else {
                am.setStreamMute(AudioManager.STREAM_ALARM, false);
            }
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.start();*/

        /*mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.seekTo(0);
                mp.start();
            }
        });*/

        //PhoneLocatorService.mediaPlayerOncePlayed = true;

        mDisplayMessage = (TextView)findViewById(R.id.textViewAlternateNumberDisplay);

        String displayMessageStr = "I have lost my phone. If someone gets it please call on " + MainActivity.mAlternateNumber + " to return it";

        mDisplayMessage.setText(displayMessageStr);

        Intent locationSendService = new Intent(getApplicationContext(), LocationSendService.class);

        startService(locationSendService);


       /* mediaPlayer.setLooping(true);
        mediaPlayer.start();*/

    }

    protected void onPause() {
        super.onPause();
        /*if(this.isFinishing()){
            MainActivity.mediaPlayer.stop();
        }*/
    }


    protected void onResume() {
        super.onResume();
    }

    protected void onStop(){
      super.onStop();
        if(LocationSendService.mediaPlayer != null){
            LocationSendService.mediaPlayer.stop();
            LocationSendService.mediaPlayer.release();
            LocationSendService.mediaPlayer = null;
        }
        if(LocationSendService.mTimer != null) {
            LocationSendService.mTimer.cancel();
            LocationSendService.mTimer.purge();
        }
        getApplicationContext().stopService(MainActivity.getmMainActivity().phoneLocatorService);
        //mWakeLock.release();
    }

    protected void onStart(){
        super.onStart();

    }
}
