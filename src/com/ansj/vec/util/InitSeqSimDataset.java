package com.ansj.vec.util;

import com.ansj.vec.Word2VEC;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class InitSeqSimDataset {
    public static void main(String[] args) throws IOException {
        //initSICK();
        //initSICK_by_w2v();
        //initSTS();
        //initSTS_by_w2v();
        init_240_by_w2v();
    }

    static void initSICK() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ss\\SICK.txt"));//使用BufferedReader 最大好处是可以按bai行du读取,每次读取一行
        BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ss\\SICK\\score_labeled.txt"));

        String line = br.readLine();
        while (line!= null){
            String[] lines = line.split("\t");
            bw.write(lines[1]+"\t"+lines[2]+"\t"+lines[4]);
            bw.newLine();
            line = br.readLine();
        }
        br.close();
        bw.close();
    }

    static void initSICK_by_w2v() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ss\\SICK.txt"));//使用BufferedReader 最大好处是可以按bai行du读取,每次读取一行
        BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ss\\SICK\\score_w2v.txt"));
        Word2VEC w2v = new Word2VEC();
        w2v.loadJavaModel("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\model\\javaVector_testConnected");
        br.readLine();
        String line = br.readLine();
        while (line!= null){
            String[] lines = line.split("\t");
            ArrayList<Double> sl1= new ArrayList<>();
            ArrayList<Double> sl2= new ArrayList<>();
            for(int i=0;i<200;i++){
                sl1.add(0.0);
                sl2.add(0.0);
            }
            for(String w:lines[1].split(" ")){
                float[] wl1= w2v.getWordVector(w);
                if(wl1==null){
                    System.out.println("没有"+w);
                    wl1 = new float[200];
                    Arrays.fill(wl1, 0);
                }
                for(int i=0;i<wl1.length;i++){
                    sl1.set(i,sl1.get(i)+wl1[i]);
                }
            }
            for(String w:lines[2].split(" ")){
                float[] wl2= w2v.getWordVector(w);
                if(wl2==null){
                    System.out.println("没有"+w);
                    wl2 = new float[200];
                    Arrays.fill(wl2, 0);
                }
                for(int i=0;i<wl2.length;i++){
                    sl2.set(i,sl2.get(i)+wl2[i]);
                }
            }
            float score = (float) Cosin.similarity(sl1, sl2);
            bw.write(lines[1]+"\t"+lines[2]+"\t"+score);
            bw.newLine();
            line = br.readLine();
        }
        br.close();
        bw.close();
    }

    static void initSTS() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ss\\STS.txt"));//使用BufferedReader 最大好处是可以按bai行du读取,每次读取一行
        BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ss\\STS\\score_labeled.txt"));

        String line = br.readLine();
        while (line!= null){
            String[] lines = line.split("\t");
            if(lines.length==7){
                bw.write(lines[5]+"\t"+lines[6]+"\t"+lines[4]);
                bw.newLine();
            }
            line = br.readLine();
        }
        br.close();
        bw.close();
    }

    static void initSTS_by_w2v() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ss\\STS\\score_labeled.txt"));//使用BufferedReader 最大好处是可以按bai行du读取,每次读取一行
        BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ss\\STS\\score_w2v.txt"));
        BufferedWriter bw2 = new BufferedWriter(new FileWriter("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ss\\STS\\score_labeled2.txt"));
        Word2VEC w2v = new Word2VEC();
        w2v.loadJavaModel("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\model\\javaVector");
        br.readLine();
        String line = br.readLine();
        while (line!= null){
            String[] lines = line.split("\t");
            System.out.println(lines.length);
            if(lines.length!=3){
                line = br.readLine();
                continue;
            }
            ArrayList<Double> sl1= new ArrayList<>();
            ArrayList<Double> sl2= new ArrayList<>();
            for(int i=0;i<200;i++){
                sl1.add(0.0);
                sl2.add(0.0);
            }
            for(String w:lines[0].split(" ")){
                float[] wl1= w2v.getWordVector(w);
                if(wl1==null){
                    System.out.println("没有"+w);
                    wl1 = new float[200];
                    Arrays.fill(wl1, 0);
                }
                for(int i=0;i<wl1.length;i++){
                    sl1.set(i,sl1.get(i)+wl1[i]);
                }
            }
            for(String w:lines[1].split(" ")){
                float[] wl2= w2v.getWordVector(w);
                if(wl2==null){
                    System.out.println("没有"+w);
                    wl2 = new float[200];
                    Arrays.fill(wl2, 0);
                }
                for(int i=0;i<wl2.length;i++){
                    sl2.set(i,sl2.get(i)+wl2[i]);
                }
            }
            float score = (float) Cosin.similarity(sl1, sl2);
            Float s=new Float(score);
            if(!s.isNaN()){
                bw.write(lines[0]+"\t"+lines[1]+"\t"+score);
                bw.newLine();
                bw2.write(lines[0]+"\t"+lines[1]+"\t"+lines[2]);
                bw2.newLine();
            }
            line = br.readLine();
        }
        br.close();
        bw.close();
        bw2.close();
    }

    static void init_240_by_w2v() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ws_cn\\CN-WS-297.txt"));
        BufferedWriter bw1 = new BufferedWriter(new FileWriter("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ws_cn\\CN-WS-297\\score_labeled.txt"));//使用BufferedReader 最大好处是可以按bai行du读取,每次读取一行
        BufferedWriter bw2 = new BufferedWriter(new FileWriter("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ws_cn\\CN-WS-297\\score_w2v.txt"));
        Word2VEC w2v = new Word2VEC();
        w2v.loadJavaModel("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\model\\javaVector_cn");
        br.readLine();
        String line = br.readLine();

        while (line!= null){
            System.out.println(line);
            String lines[] = line.split("\t");
            float[] d1= w2v.getWordVector(lines[0]);
            float[] d2= w2v.getWordVector(lines[1]);
            ArrayList w1=new ArrayList<>();
            ArrayList w2=new ArrayList<>();
            if(d1!=null&&d2!=null){
                for(int i=0;i<d1.length;i++){
                    w1.add((double)d1[i]);
                    w2.add((double)d2[i]);
                }
                double sim = Cosin.similarity(w1,w2);
                if(sim>0){
                    bw2.write(lines[0]+" "+lines[1]+" "+Cosin.similarity(w1,w2));
                    bw1.write(lines[0]+" "+lines[1]+" "+lines[2]);
                    bw1.newLine();
                    bw2.newLine();
                }
            }
            line = br.readLine();
        }
        br.close();
        bw1.close();
        bw2.close();
    }
}
