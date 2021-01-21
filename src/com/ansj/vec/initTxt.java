package com.ansj.vec;

import java.io.*;

public class initTxt{
    public static void main(String[] args) throws IOException {
        remove();
    }

    public static void remove() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ss\\result\\mark_SICK.txt"));//使用BufferedReader 最大好处是可以按bai行du读取,每次读取一行
        BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ss\\result\\mark_SICK-1.txt"));
        String line = br.readLine();
        while(line!=null){
            String[] lines = line.split(" ");
            System.out.println(lines.length);
            if(lines.length==2){
                bw.write(line);
                bw.newLine();
            }
            line = br.readLine();
        }
        br.close();
        bw.close();
        System.out.println("end");
    }

    public void init() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\text8"));//使用BufferedReader 最大好处是可以按bai行du读取,每次读取一行
        BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\text9.txt"));

        String temp=br.readLine();//定义字符串,用于保dao存每行读取到的数据
//        while (temp != null) {
//            System.out.println(temp);
//        }
        System.out.println("\n text8 has been loaded");
        int pos =0,num=0;
        for(int i=0;i<temp.length();i++){
            if(numOfSpace(temp.substring(pos,i))>=20){
                bw.write(temp.substring(pos,i-1));
                System.out.println(num+" "+temp.substring(pos,i-1));
                bw.newLine();
                pos = i;
                num+=1;
            }
        }
        //关闭流
        bw.close();
    }

    public int numOfSpace(String s){
        int num=0;
        for(int i=0;i<s.length();i++){
            if(s.charAt(i)==' '){
                num+=1;
            }
        }
        return num;
    }
}
