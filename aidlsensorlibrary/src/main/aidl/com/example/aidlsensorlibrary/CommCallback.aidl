// CommCallback.aidl
package com.example.aidlsensorlibrary;
import com.example.aidlsensorlibrary.IMyAidlInterface;
// Declare any non-default types here with import statements

interface CommCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
        void registerListener(IMyAidlInterface callback);
        void deregisterListener(IMyAidlInterface callback);
}
