package com.example.neymar.bluetooth18.controll;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NEYMAR on 2020/4/30.
 */
public class BlueToothControll{
    private BluetoothAdapter mAdapter;

    public BlueToothControll(){
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 打开蓝牙
     */
    public void turnOnBlueTooth(Activity activity, int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开蓝牙可见性
     */
    public void enableVisibily(Context context){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
        context.startActivity(intent);
    }

    /**
     * 查找设备
     */
    public void findDevice() {
        assert (mAdapter != null);
        mAdapter.startDiscovery();
    }

    /**
     * 获取已绑定设备
     */
    public List<BluetoothDevice> getBondedDeviceList(){
        return new ArrayList<>(mAdapter.getBondedDevices());
    }

    /**
     * 关闭蓝牙
     */
    public void turnOffBlueTooth(){
        mAdapter.disable();
    }
}
