package com.example.neymar.bluetooth18;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.neymar.bluetooth18.connect.Constant;
import com.example.neymar.bluetooth18.controll.BlueToothControll;
import com.example.neymar.bluetooth18.controll.ChatController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NEYMAR on 2020/4/30.
 */
public class MessageConnect extends AppCompatActivity {

    private Intent intent;
    private TextView textView;
    private EditText editText;
    private Toast mToast;//Toast用于向用户显示一些帮助/提示
    private BlueToothControll mController = new BlueToothControll();
    private Handler mUIHandler = new MyHandler();//handler是Android给我们提供用来更新UI的一套机制，也是一套消息处理机制，我们可以发消息，也可以通过它处理消息。

    private StringBuilder mChatText = new StringBuilder();

    private List<BluetoothDevice> mBondedDeviceList = new ArrayList<>();//已绑定设备的集合
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        intUI();
        mBondedDeviceList=  mController.getBondedDeviceList();//获取已绑定设备
        try {
            //提取数据
            String i=getIntent().getStringExtra("age");
            System.out.println("数值:"+i);
            BluetoothDevice device = mBondedDeviceList.get(Integer.parseInt(i));
            ChatController.getInstance().startChatWith(device, mController.getAdapter(),mUIHandler);
        }catch (Exception e){
            showToast(e.getMessage());
        }
    }

    private void intUI(){
        intent=new Intent();
        textView= (TextView) findViewById(R.id.chat);
        editText= (EditText) findViewById(R.id.chat_send);
    }

    /**
     * 页面跳转:返回主界面
     * @param view
     */
    public void goBackMian(View view){
        //执行动作
        intent.setClass(MessageConnect.this,MainActivity.class);
        //添加当前页面和目的页面
        startActivity(intent);
    }

    /**
     * 服务端按钮
     * @param view
     */
    public void acceptServer(View view){
        ChatController.getInstance().waitingForFriends(mController.getAdapter(), mUIHandler);
        showToast("等待客户端连接....");
    }

    /**
     * 客户端按钮
     */
    public void sendServer(View view){
        //执行动作
        intent.setClass(MessageConnect.this,BlueToothConnect.class);
        //添加当前页面和目的页面
        startActivity(intent);
    }

    /**
     * 断开连接
     * @param view
     */
    public void removeConnecrer(View view){
        mController.turnOffBlueTooth();
    }
    /**
     * 发送信息
     * @param view
     */
    public void sendMessage(View view){
        String ext = editText.getText().toString();
        ChatController.getInstance().sendMessage(ext);
        //
        mChatText.append(ext).append("\n");
        textView.setText(mChatText.toString());//将发送内容显示到聊天面板上
        editText.setText("");//发送数据后清空输入框的内容
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
//    public void enterChatMode() {
//        mListView.setVisibility(View.GONE);
//        mChatPanel.setVisibility(View.VISIBLE);
//    }
//
//    public void exitChatMode() {
//        mListView.setVisibility(View.VISIBLE);
//        mChatPanel.setVisibility(View.GONE);
//    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.MSG_START_LISTENING:
                    setProgressBarIndeterminateVisibility(true);
                    break;
                case Constant.MSG_FINISH_LISTENING:
                    setProgressBarIndeterminateVisibility(false);
                    break;
                case Constant.MSG_GOT_DATA:
                    byte[] data = (byte[]) msg.obj;
                    mChatText.append(ChatController.getInstance().decodeMessage(data)).append("\n");
                    textView.setText(mChatText.toString());
                    break;
                case Constant.MSG_ERROR:
                    //exitChatMode();
                    showToast("error: "+String.valueOf(msg.obj));
                    break;
                case Constant.MSG_CONNECTED_TO_SERVER:
                    //enterChatMode();
                    showToast("连接成功");
                    break;
                case Constant.MSG_GOT_A_CLINET:
                    //enterChatMode();
                    showToast("Got a Client");
                    break;
            }
        }
    }
}
