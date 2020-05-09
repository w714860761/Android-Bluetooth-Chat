package com.example.neymar.bluetooth18;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Intent intent;
    //private Button blueToothConnect;
    private Button messageReceive;
    private Button map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intUI();

    }

    private void intUI(){
        intent=new Intent();
        //blueToothConnect = (Button) findViewById(R.id.BlueToothConnect);
        messageReceive= (Button) findViewById(R.id.MessageReceive);
        map= (Button) findViewById(R.id.Map);
    }

//    /**
//     * 蓝牙连接按钮事件:界面跳转
//     */
//    public void blueToothConnectClick(View view){
//        //执行动作
//        intent.setClass(MainActivity.this,BlueToothConnect.class);
//        //添加当前页面和目的页面
//        startActivity(intent);
//    }

    /**
     * 信息接收按钮事件:界面跳转
     * @param view
     */
    public void messageConnect(View view){
        //执行动作
        intent.setClass(MainActivity.this,MessageConnect.class);
        //添加当前页面和目的页面
        startActivity(intent);
    }
}
