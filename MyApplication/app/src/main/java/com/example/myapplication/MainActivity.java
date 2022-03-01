package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    // 权限
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,  // 读外存权限
            Manifest.permission.WRITE_EXTERNAL_STORAGE, // 读外存权限
            Manifest.permission.CAMERA,                 // 相机权限

            Manifest.permission.ACCESS_NETWORK_STATE,   // 网络权限
            Manifest.permission.INTERNET,               // 互联网权限

            Manifest.permission.ACCESS_COARSE_LOCATION, // 定位权限
            Manifest.permission.ACCESS_FINE_LOCATION,   // 定位权限
            Manifest.permission.ACCESS_WIFI_STATE,      // wifi权限
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.RECORD_AUDIO
    };
    String my_Com = "40:74:E0:BA:44:91";
    BluetoothAdapter bluetoothAdapter;
    private int openCode = 0x01;
    MyReceiver myReceiver = new MyReceiver();
    public ArrayList<BluetoothDevice> deviceAddress = new ArrayList<>();
    public ArrayList<String> deviceName = new ArrayList<>();  //存放蓝牙名称和地址\
    Button sendBtn, startBtn;
    TextView inputText;
    EditText outputText;
    private final String BLUETOOTH_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private BluetoothServerSocket bluetoothSocket = null;
    private BluetoothDevice bluetoothDevice = null;
    private DataInputStream dis;
    private DataOutputStream dos;
    private BluetoothManager bluetoothManager;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 101){
                Toast.makeText(MainActivity.this, ""+msg.obj, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(myReceiver, intentFilter);
        initBlueTooth();
        startBlueTooth();
    }

    public class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Toast.makeText(context, "开始", Toast.LENGTH_SHORT).show();
            } else if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //创建搜索蓝牙列表的///
                for (int i = 0; i < deviceAddress.size(); i++) {
                    if (deviceAddress.get(i).getAddress().equals(device.getAddress())) return;
                    //上面if语句就是去除已经获取的蓝牙设备
                }
                // 不是重复的就添加到列表中(获取未配对的蓝牙设备)
                deviceAddress.add(device);  //添加地址到列表中   用于鉴别是否已经添加列表和点击事件用的
                if(device.getAddress().equals(my_Com)){
                    bluetoothDevice = device;
                    Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                    Log.e("main", "conn success");
                }

                deviceName.add("地址："+device.getAddress()+"  "+"名称："+device.getName() + "\n");  //存放蓝牙名称和地址用于显示到列表上的

            } else if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
            }
            show();
        }

        public void show(){
            Log.e("main", deviceAddress.toString() + "\n" + deviceName.toString());

        }
    }

    private void initBlueTooth(){
        sendBtn = findViewById(R.id.send);
        startBtn = findViewById(R.id.startDis);
        inputText = findViewById(R.id.input);
        outputText = findViewById(R.id.output);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beginDiscovery();
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!deviceAddress.contains(bluetoothDevice)){
                    Toast.makeText(MainActivity.this, "未匹配设备", Toast.LENGTH_SHORT).show();
                    return;
                }
                connect_Bt(bluetoothDevice);
            }
        });
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bm.getAdapter();
            Log.e("main", "start");
        }
        else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if(bluetoothAdapter == null){
            Toast.makeText(MainActivity.this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
        }
    }

    private  void startBlueTooth(){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, openCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("resultCode:" + resultCode);
        Log.e("list", resultCode + "");
        beginDiscovery();
    }


    private void beginDiscovery(){
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();  
            Toast.makeText(this,"搜索器打开",Toast.LENGTH_SHORT).show();
        }
        //如果当前不是正在搜索，则开始新的搜索任务
        if (!bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.startDiscovery();//开始扫描周围的蓝牙设备
            Log.e("main", "discover");
        }
    }

    public void connect_Bt(BluetoothDevice device){
        try (BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString(BLUETOOTH_UUID));) {

            bluetoothSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(bluetoothAdapter.getName(), UUID.fromString(BLUETOOTH_UUID));
            Log.e("main", "连接服务端...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        BluetoothSocket accept = null;
                        try {
                            accept = bluetoothSocket.accept();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        new ServerThread(accept,handler).start();
                    }
                }
            });
            socket.connect();
            dos = new DataOutputStream(socket.getOutputStream());
            /**
             * 阻塞方法
             */
            if (outputText.getText().toString().equals("")) {
                dos.flush();
                dos.writeUTF("connect");
            } else {
                dos.writeUTF(outputText.getText().toString());
            }
            dos.flush();

            dis = new DataInputStream(socket.getInputStream());
            /**
             * 阻塞方法
             */
            InputStream  inputStream = socket.getInputStream();
            String readUTF = dis.readUTF();
            System.out.println("接收到服务端信息：" + readUTF);
            inputText.setText(readUTF);
            dos.writeUTF("dddd");
            inputText.setText(dis.readUTF());
            dos.close();
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ServerThread extends Thread {
        private BluetoothSocket socket;
        private Handler handler;

        public ServerThread(BluetoothSocket socket, Handler handler) {
            this.socket = socket;
            this.handler = handler;
        }

        @Override
        public void run() {
            super.run();
            try {
                InputStream inputStream = socket.getInputStream();
                dis = new DataInputStream(inputStream);
                String readUTF = dis.readUTF();
                inputText.setText(readUTF);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    // 动态申请权限
    private void requestPermission(){
        boolean isPermissionRequested = true;
        for (String perm : PERMISSIONS) {
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                isPermissionRequested = false;
                break;
            }
        }
        if(!isPermissionRequested){
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }
    }
}
