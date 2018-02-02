package com.quyon.wifilocation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    ArrayList<String>fileNames;
    ListView listView;
    EditText file1;
    EditText file2;
    Button file1Button;
    Button file2Button;
    Button scanFileButton;
    Button getNumButton;
    Double finalNum;
    /**
     * 点击文件名时对应的状态:
     * 0：初始状态点击无意义;
     * 1：表示即将选取数据文件;
     * 2：表示即将选取测试文件
     */
    int fileState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        listView = findViewById(R.id.FileNameListView);
        file1 = findViewById(R.id.DataBaseFileName);
        file2 = findViewById(R.id.TestDatabaseFileName);
        file1Button = findViewById(R.id.file1Button);
        file2Button = findViewById(R.id.file2Button);
        scanFileButton = findViewById(R.id.scanFileButton);
        getNumButton = findViewById(R.id.getNumButton);

        fileNames = new ArrayList<>();

        file1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileState = 1;
            }
        });
        file2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileState = 2;
            }
        });
        scanFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileState = 0;//文件状态还原初始
                scanFile();
                ArrayAdapter<String>arrayAdapter = new ArrayAdapter<>(Main2Activity.this, android.R.layout.simple_list_item_1, fileNames);
                listView.setAdapter(arrayAdapter);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = fileNames.get(position);
                //点击listView根据情况改变
                switch (fileState){
                    case 0:
                        break;
                    case 1:
                        file1.setText(filename);
                        break;
                    case 2:
                        file2.setText(filename);
                        break;
                        default:
                            break;
                }
            }
        });
        getNumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileState = 0;
                String fileName1 = getExternalFilesDir(null)+"/"+file1.getText().toString();
                String fileName2 = getExternalFilesDir(null)+"/"+file2.getText().toString();
                if (file1.getText().toString().equals("")||file2.getText().toString().equals("")){
                    Toast.makeText(Main2Activity.this,"文件名不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    //TODO 计算数值finalNum
                    KNN knn = new KNN(fileName1,fileName2);
                    knn.clacKNN();
                    finalNum = knn.AVE_KNN_Measurement_error();
                    Log.d("最终数据",finalNum.toString());
                }
            }
        });
    }

    /**
     * 扫描私有文件夹下的txt文件
     */
    private void scanFile(){
        File file = new File(getExternalFilesDir(null).getAbsolutePath());
        File[] files = file.listFiles();
        fileNames.clear();
        for (File e : files){
            if (e.isFile()&&e.getName().endsWith(".txt")){
                fileNames.add(e.getName());
            }
        }
    }
}
