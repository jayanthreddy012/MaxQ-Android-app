package com.active.maxq;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;

import static com.active.maxq.DeviceListActivity.TAG;

/**
 * Created by Trung on 10/3/2017.
 */

public class ControlDevice extends Activity {

    private UartService mService = null;

    private Button btnfinalplot, btnstatus, btncontrol, btnselect, btnup, btndow, btnpower;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control);
        btnfinalplot=(Button)findViewById(R.id.btnfinal);
        //Button buttonoff = (Button) findViewById(R.id.btnoff);
        //btnfinalplot.setEnabled(false);
        //btnlogger.setEnabled(true);





    }

    //UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

        }

        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnect(mDevice);
            mService = null;
        }
    };

}



