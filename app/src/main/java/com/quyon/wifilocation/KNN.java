package com.quyon.wifilocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

import static java.lang.Math.sqrt;

/**
 * KNN又名临近算法
 * 实现步骤：
 * 1. 首先计算出所有的临近距离值
 * 2. 对临近值进行排序
 * 3. 选出临近值最小的K个数
 * 4. 投票选出结果
 *
 * Created by quyon on 2018/2/1.
 *  感谢郭任同学在算法部分的帮助
 */

class KNN {

        class AP{
            public String name="";
            double level=-98;
        }
        class Point{
            double x;
            double y;
            double z;
        }
        private Vector<AP[]> dbaps=new Vector<AP[]>();
        private Vector<AP[]> testaps=new Vector<AP[]>();
        private Vector<Point> db_points=new Vector<Point>();;
        private Vector<Point> true_points=new Vector<Point>();
        private Vector<Point> clac_points=new Vector<Point>();

        private String dbname="";
        private String testname="";

        private static int K = 6;

        public KNN(String db_name,String test_name){
            dbname=db_name;
            testname=test_name;
            initdata();
        }
        private void initdata(){
            try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw

            /* 读入TXT文件 */
                String pathname = dbname; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
                File filename = new File(pathname); // 要读取以上路径的input.txt文件
                InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                String line = "";
                while (line != null) {
                    String[]temp;
                    line = br.readLine(); // 一次读入一行数据
                    temp=line.split("[| ]");
                    Point p=new Point();
                    p.x=Double.parseDouble(temp[0]);
                    p.y=Double.parseDouble(temp[1]);
                    p.z=Double.parseDouble(temp[2]);
                    db_points.add(p);
                    int len=temp.length;
                    len=(len-3)/2;
                    AP[] tap=new AP[len];
                    for(int i=3;i<len;i++){
                        AP ap=new AP();
                        tap[i].name=temp[i++];
                        tap[i].level=Double.parseDouble(temp[i]);
                    }
                    dbaps.add(tap);
                }
                pathname = testname; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
                File filename2 = new File(pathname); // 要读取以上路径的input.txt文件
                InputStreamReader reader2 = new InputStreamReader(new FileInputStream(filename2)); // 建立一个输入流对象reader
                BufferedReader br2 = new BufferedReader(reader2); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                while (line != null) {
                    String[]temp;
                    line = br2.readLine(); // 一次读入一行数据
                    temp=line.split("[| ]");
                    Point p=new Point();
                    p.x=Double.parseDouble(temp[0]);
                    p.y=Double.parseDouble(temp[1]);
                    p.z=Double.parseDouble(temp[2]);
                    true_points.add(p);
                    int len=temp.length;
                    len=(len-3)/2;
                    AP[] tap=new AP[len];
                    for(int i=3;i<len;i++){
                        AP ap=new AP();
                        tap[i].name=temp[i++];
                        tap[i].level=Double.parseDouble(temp[i]);
                    }
                    testaps.add(tap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
        * 待求解数组
        * 求出求解的分类与二维数组间元素的临近距离
       */
        public  boolean clacKNN(){

            double[] questionDistinces = new double[dbaps.size()];
            for(int j=0;j<testaps.size();j++) {
                AP distince[] =testaps.get(j);
                for (int i = 0; i < dbaps.size(); i++) {
                    AP[] item = dbaps.get(i);
                    questionDistinces[i] = distince(distince,item );
                }
                System.out.println("临近距离集合：" + Arrays.toString(questionDistinces));
                int nearest[] = paraseKDistince(questionDistinces, K);
                System.out.println("K 个最临近距离下标集合：" + Arrays.toString(nearest));
                Point tpoint=new Point();
                for (int i = 0; i < K; i++) {
                    tpoint.x+=db_points.get(nearest[i]).x/K;
                    tpoint.y+=db_points.get(nearest[i]).y/K;
                    tpoint.z+=db_points.get(nearest[i]).z/K;
                }
                clac_points.add(tpoint);
            }
            return true;
        }

        private double distince(AP[] paraFirstData, AP[] paraSecondData){
            double tempDistince = 0;
            int count=0;
            for (AP aParaSecondData : paraSecondData) {
                for (AP aParaFirstData : paraFirstData) {
                    if (Objects.equals(aParaFirstData.name, aParaSecondData.name)) {
                        count += 1;
                        tempDistince += sqrt((aParaFirstData.level - aParaSecondData.level) * (aParaFirstData.level - aParaSecondData.level));
                    }
                }
            }
            tempDistince/=count;
            return tempDistince;
        }

        double AVE_KNN_Measurement_error() {
            double result=0;
            if(true_points.size()!=clac_points.size())
                return 100;
            for(int i=0;i<true_points.size();i++){
                double dx=true_points.get(i).x-clac_points.get(i).x;
                double dy=true_points.get(i).y-clac_points.get(i).y;
                double dz=true_points.get(i).z-clac_points.get(i).z;
                result+=sqrt(dx*dx+dy*dy+dz*dz);
            }
            result=result/true_points.size();
            return result;
       }

        /**
        * 获取临近距离中的K的距离的下标数组
        */
        private int[] paraseKDistince(double[] distinceArray, int k){
            double[] tempDistince = new double[k+2];
            int[] tempNearest = new int[k+2];
            //初始化两个数组
            tempDistince[0] = Double.MIN_VALUE;
            for(int i=1;i<k+2;i++){
                tempDistince[i] = Double.MAX_VALUE;
                tempNearest[i] = -1;
            }
            //准备筛选临近距离
            for(int i=0;i<distinceArray.length;i++){
                for(int j=k;j>=0;j--){
                    if(distinceArray[i]<tempDistince[j]){
                        tempDistince[j+1] = tempDistince[j];
                        tempNearest[j+1] = tempNearest[j];
                    }else{
                        tempDistince[j+1] = distinceArray[i];
                        tempNearest[j+1] = i;
                        break;
                    }
                }
            }
            int[] returnNearests = new int[k];
            System.arraycopy(tempNearest, 1, returnNearests, 0, k);
            return returnNearests;
        }
}
