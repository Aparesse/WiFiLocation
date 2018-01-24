package com.quyon.wifilocation;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.quyon.wifilocation.WiFiManagent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    WiFiManagent wiFiManagent = new WiFiManagent(this);
    String fileName = null;

    Button scanButton = (Button)findViewById(R.id.scanButton);
    Button write2FileButton = (Button)findViewById(R.id.write2File);
    EditText XeditText = (EditText)findViewById(R.id.XeditText);
    EditText YeditText = (EditText)findViewById(R.id.YeditText);
    ListView listView = (ListView)findViewById(R.id.listView);

    String getTime(){
        Date date = new Date(System.currentTimeMillis());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
        return simpleDateFormat.format(date);
    }
}
