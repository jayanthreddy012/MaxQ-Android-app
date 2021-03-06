
/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.active.maxq;




import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.StringTokenizer;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import android.graphics.Typeface;

import me.anwarshahriar.calligrapher.Calligrapher;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_READY = 10;
    public static final String TAG = "MaxQ";
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int STATE_OFF = 10;

    TextView mRemoteRssiVal;
    RadioGroup mRg;
    private int mState = UART_PROFILE_DISCONNECTED;
    private UartService mService = null;
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBtAdapter = null;
    //private ListView messageListView;
    private ArrayAdapter<String> listAdapter;
    private Button btnConnectDisconnect,btnlogger,btnfinalplot,buttonoff, btnstatus;
    //private EditText edtMessage;
    //public int beginOfLineIndex = -1;
    //public boolean checkStart = false;
    //public boolean checkEnd = false;
    public static int stringId;
    public static double temperature;
    public static long[] testId=new long[10000];
    public static String[] PayloadStatus=new String[10000];
    public static String[] Box_Status=new String[10000];
    public static String timeString;
    public static String deviceName;
    public static int[] Time=new int[1000];
    public  static String[] DateTime=new String[10000];
    public static String[] texttopdf = new String[10000];
    public static  int Icount=0;
    public static int j=0;
    public String[] string_array =new String[10000];
    public static String[] file_array =new String[10000];
    public static String fileInPrint;
    public static int string_i=0;
    public static int file_i=0;
    public static int string_count=1;
    public static int $_count=0;
    public static int hash_count=0;
    public String finaltext=null;
    public String dataInPrint=null;

    private StringBuilder recDataString = new StringBuilder();
    private ProgressBar prg;

    LineGraphSeries<DataPoint> series1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "fonts/GothamLight.ttf", true);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        //messageListView = (ListView) findViewById(R.id.listMessage);
        listAdapter = new ArrayAdapter<String>(this, R.layout.message_detail);
        //messageListView.setAdapter(listAdapter);
        //messageListView.setDivider(null);
        btnConnectDisconnect=(Button) findViewById(R.id.btn_select);

        prg = (ProgressBar) findViewById(R.id.progressBar2);

        btnfinalplot=(Button)findViewById(R.id.btnfinal);

        btnfinalplot.setEnabled(false);





        service_init();

     
       
        // Handle Disconnect & Connect button
        btnConnectDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBtAdapter.isEnabled()) {
                    Log.i(TAG, "onClick - BT not enabled yet");
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }
                else {
                	if (btnConnectDisconnect.getText().equals("Connect")){
                		
                		//Connect button pressed, open DeviceListActivity class, with popup windows that scan for devices
                		
            			Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
            			startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
        			} else {
        				//Disconnect button pressed
        				if (mDevice!=null)
        				{
        					mService.disconnect();


        					
        				}
        			}
                }
            }
        });

       /* btnfinalplot.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //EditText editText = (EditText) findViewById(R.id.sendText);
                //String message = editText.getText().toString();
                String message="a";
                byte[] value;
                try {
                    //send data to service
                    value = message.getBytes("UTF-8");
                    mService.writeRXCharacteristic(value);
                    //Update the log with time stamp

                    //String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    //listAdapter.add("["+currentDateTimeString+"] on");
                    //messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
                    //edtMessage.setText("");
                    for (int j=0; j < 100 ;j++) {
                        prg.setProgress(j);
                    }
                    Toast.makeText(MainActivity.this,"Create Graph",Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                try
                {
                    Thread.sleep(3000);
                }
                catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }

                //finish();
                //checkStart = false;
                //checkEnd = false;
                //Intent intent=new Intent(MainActivity.this,GraphActivity.class);
                //startActivity(intent);
                //graphplot1();


            }
        });
*/

/*
        btncontrol.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                Intent intent=new Intent(MainActivity.this,ControlDevice.class);
                startActivity(intent);
            }
        });

*/
        
    }

    public void onBtnClick(View v){
        String message="a";
        byte[] value;
        try {
            value = message.getBytes("UTF-8");
            mService.writeRXCharacteristic(value);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Thread thread = new Thread(){
          @Override
            public void run(){

              super.run();
              for(int j=0; j <=100;){
                  try{
                      //long check = testId[2];
                      //Log.d(TAG, "Unix Time:" + MainActivity.testId[j]);
                      long delay = stringId*1000/300;
                      sleep(delay);
                  } catch (InterruptedException e){
                      e.printStackTrace();
                  }

                  prg.setProgress(j);
                  j=j+10;
              }
          }

        };
        thread.start();
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

    private Handler mHandler = new Handler() {
        @Override
        
        //Handler events that received from UART service 
        public void handleMessage(Message msg) {
  
        }
    };

    public int i=0;

    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            final Intent mIntent = intent;
           //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
            	 runOnUiThread(new Runnable() {
                     public void run() {
                         	String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                             Log.d(TAG, "UART_CONNECT_MSG");

                             btnConnectDisconnect.setText("Disconnect");
                             //edtMessage.setEnabled(true);
                            btnfinalplot.setEnabled(true);
                             ((TextView) findViewById(R.id.deviceName)).setText(mDevice.getName() + System.getProperty ("line.separator")+ "Ready");
                             listAdapter.add("["+currentDateTimeString+"] Connected to: "+ mDevice.getName());
                        	 	//messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
                             mState = UART_PROFILE_CONNECTED;
                     }
            	 });
            }
           
          //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {

            	 runOnUiThread(new Runnable() {
                     public void run() {
                    	 	 String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                             Log.d(TAG, "UART_DISCONNECT_MSG");
                             btnConnectDisconnect.setText("Connect");
                             //edtMessage.setEnabled(false);
                             btnfinalplot.setEnabled(false);
                             ((TextView) findViewById(R.id.deviceName)).setText("Not Connected");
                             listAdapter.add("["+currentDateTimeString+"] Disconnected to: "+ mDevice.getName());
                             mState = UART_PROFILE_DISCONNECTED;
                             mService.close();
                            //setUiState();
                         
                     }
                 });
            }
            
          
          //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
             	 mService.enableTXNotification();
            }
          //*********************//
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
              
                 final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                //Log.d(TAG, "txValue" + txValue);
                 runOnUiThread(new Runnable() {
                     public void run() {
                         try {
                             String text = new String(txValue, "UTF-8");
                             recDataString.append(text);
                             //Log.d(TAG, "String Now = " + text);
                             int endOfLineIndex = recDataString.indexOf("@");                    // determine the end-of-line
                             int beginOfLineIndex = recDataString.indexOf("~");

                             int beginOfFile = recDataString.indexOf("$$");
                             boolean checkStart = recDataString.toString().contains("$$");
                             int endOfFile = recDataString.indexOf("##");
                             boolean checkEnd = recDataString.toString().contains("##");

                             int beginOfExcel = recDataString.indexOf("*");
                             boolean checkStartExcel = recDataString.toString().contains("%");






                             //Log.d(TAG, "Check $$ = " + checkStart );
                             //Log.d(TAG, "Check ## = " + checkEnd );
                             //Log.d(TAG, "Check ~ = " + beginOfLineIndex );
                             //Log.d(TAG, "Check @ = " + endOfLineIndex );


                             //Log.d(TAG, "Check endOfLineIndex = " + $_count );
                             //Log.d(TAG, "Check end = " + hash_count );



                             if (checkStart){
                                 fileInPrint = recDataString.substring(beginOfExcel+1, endOfFile-1);    // extract string
                                 Log.d(TAG, "File Received = " + fileInPrint);
                                 int fileLength = fileInPrint.length();                          //get length of data received
                                 //Log.d(TAG, "File Length = " + String.valueOf(fileLength));
                                 //String lines[] = fileInPrint.split(System.getProperty("line.separator"));

                                     //Log.d(TAG, "Line = " + lines[1]);

                                 if (FileHelper.saveToFile(fileInPrint)){
                                     Toast.makeText(MainActivity.this,"Saved to file",Toast.LENGTH_SHORT).show();
                                     Intent intent=new Intent(MainActivity.this,GraphActivity.class);
                                     startActivity(intent);
                                 }else{
                                     Toast.makeText(MainActivity.this,"Error save file!!!",Toast.LENGTH_SHORT).show();
                                 }

                                 recDataString.delete(0, recDataString.length());

                             }




                              if ( endOfLineIndex > 0) {                                           // make sure there data before ~
                                 String dataInPrint = recDataString.substring(beginOfLineIndex, endOfLineIndex);    // extract string
                                 //Log.d(TAG, "Data Received = " + dataInPrint);
                                 int dataLength = dataInPrint.length();                          //get length of data received
                                 //Log.d(TAG, "String Length = " + String.valueOf(dataLength));
                                 //String currentDateTimeString =DateFormat.getTimeInstance().format(new Date());
                                 //Log.d(TAG, "String now:" + text);
                                 //DateFormat.getTimeInstance().format(new Date());
                                 TextView t = (TextView) findViewById(R.id.temp);
                                 TextView s = (TextView) findViewById(R.id.payload_status);
                                 TextView e = (TextView) findViewById(R.id.elapsed_time);
                                 TextView b = (TextView) findViewById(R.id.box_status);


                                 if (dataInPrint.charAt(0) == '~') {
                                     String[] strings = dataInPrint.split(",");
                                     strings[0] = strings[0].substring(1);
                                     stringId = Integer.parseInt(strings[0]);
                                     int hours = stringId / 3600;
                                     int minutes = (stringId % 3600) / 60;
                                     int seconds = stringId % 60;

                                     timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                                     //Log.d(TAG, "Elapsed Time:" + timeString);

                                     e.setText(timeString);
                                     //e.setText(strings[0]);
                                     temperature = Double.parseDouble(strings[1]);
                                     t.setText(strings[1]);
                                     //Log.d(TAG, "temperature:" + temperature[i]);

                                     //listAdapter.add("Temperature inside the Box:" + temperature[i] + " C");


                                     String compare = "0";

                                     if (strings[2].equals(compare)) {
                                         strings[2] = "Lid Close";
                                     } else
                                         strings[2] = "Lid Open";

                                     //Log.d(TAG, "Box status:" + strings[2]);
                                     b.setText(strings[2]);


                                     if (strings[3].equals(compare)) {
                                         strings[3] = "Acceptable";
                                     } else
                                         strings[3] = "Not Acceptable";
                                     //Log.d(TAG, "Payload status check:" + strings[3]);
                                     s.setText(strings[3]);

                                     testId[i] = Long.parseLong(strings[4]);
                                     //long gap = testId[2]- testId[1];
                                     Log.d(TAG, "Unix Time Check:" + i);

                                     i++;

                                 }
                                 recDataString.delete(0, recDataString.length());
                                  dataInPrint= "";


                             }


                         } catch (Exception e) {
                             Log.e(TAG, "Error: " + e.toString());
                         }

                     }
                 });
             }
           //*********************//
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)){
            	showMessage("Device doesn't support UART. Disconnecting");
            	mService.disconnect();
            }
            
            
        }
    };

    private void service_init() {

        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
  
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
    	 super.onDestroy();
        Log.d(TAG, "onDestroy()");
        
        try {
        	LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        } 
        unbindService(mServiceConnection);
        mService.stopSelf();
        mService= null;
       
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    //@Override
  /*  public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
 
    }
*/
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

        case REQUEST_SELECT_DEVICE:
        	//When the DeviceListActivity return, with the selected device address
            if (resultCode == Activity.RESULT_OK && data != null) {
                String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                deviceName = mDevice.getName();
               
                Log.d(TAG, "... onActivityResultdevice.address==" + mDevice + "mserviceValue" + mService);
                ((TextView) findViewById(R.id.deviceName)).setText(mDevice.getName()+ " - connecting");
                mService.connect(deviceAddress);
                            

            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();

            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show();
                finish();
            }
            break;
        default:
            Log.e(TAG, "wrong request code");
            break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
       
    }

    
    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  
    }

    @Override
    public void onBackPressed() {
        if (mState == UART_PROFILE_CONNECTED) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            showMessage("MaxQ App is running in background.\n             Disconnect to exit");
        }
        else {
            new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.popup_title)
            .setMessage(R.string.popup_message)
            .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
   	                finish();
                }
            })
            .setNegativeButton(R.string.popup_no, null)
            .show();
        }
    }
}
