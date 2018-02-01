package com.quyon.wifilocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button scanButton;
    private Button write2FileButton;
    private EditText XeditText;
    private EditText YeditText;
    private ListView listView;
    private Spinner spinner;
    private MenuItem menuItem1;
    private MenuItem menuItem2;
    private WiFiManagent wiFiManagent;
    private String fileName = null;
    private boolean isNewFile = true;
    private boolean isRecording = false;
    private Float x = 0f;
    private Float y = 0f;
    private int z = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanButton = findViewById(R.id.scanButton);
        write2FileButton = findViewById(R.id.write2File);
        XeditText = findViewById(R.id.XeditText);
        YeditText = findViewById(R.id.YeditText);
        listView = findViewById(R.id.listView);
        spinner = findViewById(R.id.Zspinner);
        wiFiManagent = new WiFiManagent(this);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //扫描并显示
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                }
                wiFiManagent.scanWiFi();
                ArrayAdapter<String>arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, wiFiManagent.getBasicInfo());
                listView.setAdapter(arrayAdapter);
            }
        });
        write2FileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取坐标
                x = Float.valueOf(XeditText.getText().toString());
                y = Float.valueOf(YeditText.getText().toString());
                //写入文件
                if (isNewFile){
                    fileName = getCurrentTime()+".txt";
                    isNewFile =false;
                }
                try {
                    write2File(fileName);
                    Toast.makeText(MainActivity.this,"内容已写入"+fileName,Toast.LENGTH_SHORT).show();
                }catch (IOException e){
                    Log.e("写入文件出错",e.toString());
                }

            }
        });
        write2FileButton.setEnabled(false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Resources resources = getResources();
                String[] floor = resources.getStringArray(R.array.floor);
                z = Integer.parseInt(floor[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.startScan:
                isRecording = true;
                write2FileButton.setEnabled(true);
                break;
            case R.id.stopScan:
                isRecording = false;
                isNewFile = true;
                write2FileButton.setEnabled(false);
                break;
                default:
                    break;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){//更改菜单是否可控制
        if (isRecording){
            menu.findItem(R.id.startScan).setEnabled(false);
            menu.findItem(R.id.stopScan).setEnabled(true);
        }else {
            menu.findItem(R.id.startScan).setEnabled(true);
            menu.findItem(R.id.stopScan).setEnabled(false);
        }
        return true;
    }

    String getCurrentTime(){
        Date date = new Date(System.currentTimeMillis());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    void write2File(String fileName) throws IOException {
        ArrayList<ScanResult>wifiList = wiFiManagent.getWifiList();
        File file = new File(MainActivity.this.getExternalFilesDir(null),fileName);
        FileWriter fileWriter = new FileWriter(file,!isNewFile);
        fileWriter.write(x + "|" + y + "|" + z + ' ');
        StringBuilder stringBuilder = new StringBuilder();
        for (ScanResult e:wifiList){
            stringBuilder.append(e.BSSID).append('|').append(e.level).append(' ');
        }
        stringBuilder.append("\r");
        fileWriter.write(stringBuilder.toString());
        fileWriter.flush();
        fileWriter.close();
    }
    public void onRequestPermissionResult(int requestCode, String[] permission, int[] grantResult){
        switch (requestCode){
            case 1:
                if(grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED){
                    wiFiManagent.scanWiFi();
                }else {
                    Toast.makeText(MainActivity.this, "位置权限为搜索WIFI必须的权限，\n请务必授予",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
