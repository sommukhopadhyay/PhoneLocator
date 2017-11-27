package com.som_itsolutions.phonelocator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity /*implements SensorEventListener*/{

    Button mActivatePhoneLocatorBtn;
    Button mStopPhoneLocatorService;
    Button mSendSMSBtn;
    String smsNumberOfThePhoneToBeFound;
    EditText mSmsNumberEditText;
    EditText mAlternateNumberEditText;
    static String smsFromNumber;
    static String smsMessage;
    private static MainActivity mMainActivity;
    String mFindMyPhone = "Find My Phone";
    private static final double FREEFALL_THRESHOLD = 2.0;
    //static MediaPlayer mediaPlayer;
    public static String mAlternateNumber;
    static Intent phoneLocatorService = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.siren);
        //mediaPlayer.setLooping(true);
        /*final Intent */phoneLocatorService = new Intent(getApplicationContext(), PhoneLocatorService.class);
        mMainActivity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mSmsNumberEditText = (EditText)findViewById(R.id.editTextSMSNumber);


        mAlternateNumberEditText = (EditText)findViewById(R.id.editTextAlternateNumber);

        mActivatePhoneLocatorBtn = (Button)findViewById(R.id.buttonPhoneLocator);
        mActivatePhoneLocatorBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mAlternateNumber= mAlternateNumberEditText.getText().toString();
                phoneLocatorService.putExtra("ALTERNATE_NUMBER", mAlternateNumber);
                startService(phoneLocatorService);
            }
        });

        mStopPhoneLocatorService = (Button)findViewById(R.id.buttonStopPhoneLocator);
        mStopPhoneLocatorService.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                getApplicationContext().stopService(phoneLocatorService);
               /* if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }*/
                PhoneLocatorService.firstTimeAlert = false;
            }
        });

        mSendSMSBtn = (Button)findViewById(R.id.buttonSendSMS);
        mSendSMSBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(!mSmsNumberEditText.getText().toString().equals("")){
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(mSmsNumberEditText.getText().toString(), null, mFindMyPhone, null, null);
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static MainActivity getmMainActivity(){

        return mMainActivity;
    }

    protected void onPause() {
        super.onPause();

    }

    protected void onResume() {
        super.onResume();
    }

    protected void onStop(){
      super.onStop();
        /*if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }*/

        if (PhoneLocatorService.firstTimeAlert == true){
            PhoneLocatorService.firstTimeAlert = false;
        }

    }

}
