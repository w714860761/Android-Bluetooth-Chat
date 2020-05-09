package com.example.neymar.bluetooth18;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.neymar.bluetooth18.controll.BlueToothControll;
import com.example.neymar.bluetooth18.controll.DeviceAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NEYMAR on 2020/4/30.
 */
public class BlueToothConnect extends AppCompatActivity {

    private static final int REQUEST_CODE =0 ;
// intent是运行时绑定（run-time binding）机制，它是一种基于某种想要被表露的意图的被动式数据结构，
// 它能在程序运行过程中连接两个不同的组件。通过Intent，
// 你的程序可以向Android表达某种请求或者意愿，
// Android会根据意愿的内容选择适当的组件来完成请求。
    private Intent intent;
    private ProgressBar progressBar;//设备可见事件进度条控件
    private ProgressBar findProgressBar;//设备可见事件进度条控件
    private BlueToothControll mController;//蓝牙控制

    private DeviceAdapter findAdapter;
    private ListView findBlueToothList;
    private List<BluetoothDevice> findDeviceList = new ArrayList<>();//查找到设备的集合

    private ListView connectBlueToothList;
    private List<BluetoothDevice> mBondedDeviceList = new ArrayList<>();//已绑定设备的集合
    private DeviceAdapter haveFindAdapter;

    private Toast mToast;//Toast用于向用户显示一些帮助/提示
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothconncet);

        intUI();
        //开启广播监听
        registerBluetoothReceiver();
        //软件运行时直接申请打开蓝牙
        mController.turnOnBlueTooth(this,REQUEST_CODE);
    }

    //设置广播
    private void registerBluetoothReceiver(){
        IntentFilter filter = new IntentFilter();
        //开始查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //结束查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //查找设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备扫描模式改变，监听蓝牙可见性
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        //绑定状态
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(receiver,filter);
    }

    //注册广播监听搜索结果
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                //setProgressBarIndeterminateVisibility(true);
                findProgressBar.setVisibility(View.VISIBLE);
                //初始化数据列表
                findDeviceList.clear();
                findAdapter.notifyDataSetChanged();
            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                //setProgressBarIndeterminateVisibility(false);
                findProgressBar.setVisibility(View.INVISIBLE);
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //找到一个添加一个
                findDeviceList.add(device);
                findAdapter.notifyDataSetChanged();

            }
             if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {  //此处作用待细查
                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0);
                if(scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
                    //当在可见性时间，则开启进度条
                    //setProgressBarIndeterminateVisibility(true);
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    //当可见性时间到了，则关闭进度条
                    //setProgressBarIndeterminateVisibility(false);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
            else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(remoteDevice == null) {
                    showToast("无设备");
                    return;
                }
                int status = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0);
                if(status == BluetoothDevice.BOND_BONDED) {
                    showToast("已绑定" + remoteDevice.getName());
                } else if(status == BluetoothDevice.BOND_BONDING) {
                    showToast("正在绑定" + remoteDevice.getName());
                } else if(status == BluetoothDevice.BOND_NONE) {
                    showToast("未绑定" + remoteDevice.getName());
                }
            }
        }
    };


    private void intUI(){
        intent=new Intent();
        mController=new BlueToothControll();
        progressBar= (ProgressBar) findViewById(R.id.can_look_bar);
        findProgressBar= (ProgressBar) findViewById(R.id.find_look_bar);

        findBlueToothList= (ListView) findViewById(R.id.findBlueTooth);
        findAdapter = new DeviceAdapter(findDeviceList, this);
        findBlueToothList.setAdapter(findAdapter);
        findBlueToothList.setOnItemClickListener(bondDeviceClick);

        connectBlueToothList= (ListView) findViewById(R.id.connectBlueTooth);
        haveFindAdapter=new DeviceAdapter(mBondedDeviceList,this);
        connectBlueToothList.setAdapter(haveFindAdapter);
        connectBlueToothList.setOnItemClickListener(removeBondDeviceClick);
    }

    /**
     * 单击绑定蓝牙事件
     */
    private AdapterView.OnItemClickListener bondDeviceClick = new AdapterView.OnItemClickListener() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice device = findDeviceList.get(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//当版本大于4.4时，才支持代码绑定设备
                device.createBond();
            }
        }
    };

    private AdapterView.OnItemClickListener removeBondDeviceClick = new AdapterView.OnItemClickListener() {
        public static final String TAG = "解除绑定失败!";

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent=new Intent(BlueToothConnect.this,MessageConnect.class);//把数据传递到NextActivity
            intent.putExtra("age", i+"");
            startActivity(intent);//启动activity
        }
    };

    /**
     * 页面跳转:返回主界面
     * @param view
     */
    public void goBackMian(View view){
        //执行动作
        intent.setClass(BlueToothConnect.this,MainActivity.class);
        //添加当前页面和目的页面
        startActivity(intent);
    }

    /**
     * 打开蓝牙可见性按钮事件
     * @param view
     */
    public void openBlueToothLook(View view){
        mController.enableVisibily(this);
    }

    /**
     * 查找设备的按钮事件
     * @param view
     */
    public void findBlueToothCanLook(View view){
        findAdapter.refresh(findDeviceList);
        mController.findDevice();
    }

    /**
     * 显示已连接设备信息按钮事件
     * @param view
     */
    public void haveFindBlueToothCanLook(View view){
        mBondedDeviceList=  mController.getBondedDeviceList();
        haveFindAdapter.refresh(mBondedDeviceList);//数据更新
    }

    /**
     * 信息提示方法
     * @param text
     */
    private void showToast(String text) {

        if( mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        }
        else {
            mToast.setText(text);
        }
        mToast.show();
    }
}
