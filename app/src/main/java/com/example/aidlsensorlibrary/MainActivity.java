package com.example.aidlsensorlibrary;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private CommCallback mSerivce;
    private static final String AIDL_SERVICE_ACTION = "com.example.aidlsensorlibrary.AIDL";
    private TextView textView;
    private Handler uiHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        uiHandler=new Handler();
        bindToService();
    }

    private void initViews(){
        textView=findViewById(R.id.text_view);
        Button bindButton = findViewById(R.id.bind_button);
        Button unbindButton = findViewById(R.id.unbind_button);
        bindButton.setOnClickListener(this);
        unbindButton.setOnClickListener(this);
    }

    /**
     * Sensor Service provides the Rotation vector sensor data using this callback.
     */
    private IMyAidlInterface callback = new IMyAidlInterface.Stub() {
        @Override
        public void onValueChanged(final float[] data) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText(Arrays.toString(data));
                }
            });
        }
    };

    /**
     * Service connections where we will use it while binding to the service.
     * Here we receive the AIDL interface and we use it to communicate with service.
     */
    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSerivce = CommCallback.Stub.asInterface(service);
            try {
                mSerivce.registerListener(callback);
            } catch (RemoteException e) {
                Log.e(TAG, "onServiceConnected: Exception while accessing service functions" + e.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mSerivce = null;
            Log.e(TAG, "onServiceDisconnected: ");
        }
    };

    /**
     * API helps in Binding to the Third Party service
     */
    private void bindToService() {
        Intent intent = new Intent(this, AIDLService.class);
        intent.setAction(AIDL_SERVICE_ACTION);
        bindService(intent, serviceConn, Context.BIND_AUTO_CREATE);
    }

    /**
     * API helps in Unbinding from the third party service.
     */
    private void unbindFromService() {
        try {
            mSerivce.deregisterListener(callback);
            unbindService(serviceConn);
        } catch (Exception e) {
            Log.e(TAG, "unbindFromService: Exception while deregestering callback"+e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        unbindFromService();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bind_button:
                bindToService();
                break;
            case R.id.unbind_button:
                unbindFromService();
                break;
        }
    }
}