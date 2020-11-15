// CommCallback.aidl
package com.example.aidlsensorlibrary;
// Declare any non-default types here with import statements
import com.example.aidlsensorlibrary.IMyAidlInterface;

interface CommCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void registerListener(IMyAidlInterface callback);
     void deregisterListener(IMyAidlInterface callback);
}
