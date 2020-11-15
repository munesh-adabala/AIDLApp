package com.example.aidlsensorlibrary;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Vector;

/**
 * Service where it gets the Rotation vector sensor data and sends to the Third parties that are bound to the service and
 * registered to receive the data.
 * Note: This is bound service since, the sensor data is required only when someone bounds to it.
 */
public class AIDLService extends Service {
    private static final String TAG = "AIDLService";
    private MBinderImpl binder = new MBinderImpl();
    private SensorManager sensorManager;
    private static final int SAMPLING_PERIOD = 8 * 1000;

    @Override
    public void onCreate() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor rotaionVectorSensor = null;
        if (sensorManager != null) {
            rotaionVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            sensorManager.registerListener(sensorEventListener, rotaionVectorSensor, SAMPLING_PERIOD);
        }
        super.onCreate();
    }

    /**
     * Sensor Events listener where we get the sensor data updates.
     */
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (Sensor.TYPE_ROTATION_VECTOR == event.sensor.getType()) {
                sendSensorData(event);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     * Sends the sensor data to the registered callbakcs.
     *
     * @param event
     */
    private void sendSensorData(SensorEvent event) {
        for (IMyAidlInterface callback : binder.callBacks) {
            try {
                callback.onValueChanged(event.values);
            } catch (RemoteException e) {
                Log.e(TAG, "onSensorChanged: Exception while sending data--" + e.getMessage());
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * AIDL interface stub implementation which will be received by the client on service connection to
     * communicate with this service.
     */
    private static class MBinderImpl extends CommCallback.Stub {
        private Vector<IMyAidlInterface> callBacks = new Vector<>();

        @Override
        public void registerListener(IMyAidlInterface callback) throws RemoteException {
            callBacks.add(callback);
        }

        @Override
        public void deregisterListener(IMyAidlInterface callback) throws RemoteException {
            callBacks.remove(callback);
        }
    }

    @Override
    public void onDestroy() {
        if (sensorManager != null)
            sensorManager.unregisterListener(sensorEventListener);
        super.onDestroy();
    }
}
