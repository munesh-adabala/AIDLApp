// IMyAidlInterface.aidl
package com.example.aidlsensorlibrary;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    void onValueChanged(in float[] data);
}
