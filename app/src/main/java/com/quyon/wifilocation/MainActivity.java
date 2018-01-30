package com.quyon.wifilocation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.quyon.wifilocation.WiFiManagent;
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
    private WiFiManagent wiFiManagent;
    private String fileName = null;
    private boolean isNewFile = true;
    private double x = 0.0;
    private double y = 0.0;
    private int z = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanButton = (Button)findViewById(R.id.scanButton);
        write2FileButton = (Button)findViewById(R.id.write2File);
        XeditText = (EditText)findViewById(R.id.XeditText);
        YeditText = (EditText)findViewById(R.id.YeditText);
        listView = (ListView)findViewById(R.id.listView);
        wiFiManagent = new WiFiManagent(this);
        try {
            write2File("666.txt");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    String getCurrentTime(){
        Date date = new Date(System.currentTimeMillis());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    void write2File(String fileName) throws IOException {
        ArrayList<ScanResult>wifiList = wiFiManagent.getWifiList();
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),fileName);
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
}
