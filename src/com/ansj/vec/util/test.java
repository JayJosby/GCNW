package com.ansj.vec.util;// 本题为考试单行多行输入输出规范示例，无需提交，不计分。
import javax.print.DocFlavor;
import java.util.*;

public class test {
    static int min=Integer.MAX_VALUE;
    static int xx=0,yy=0,m=0,n=0,ans=0;
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        float [][]m = new float[3][3];
        Arrays.fill(m[0],0);
        Arrays.fill(m[1],0);
        Arrays.fill(m[2],0);
        int num=0;
        while (in.hasNext()){
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    m[i][j]+=in.nextFloat();
                }
            }
            num+=1;
            if(num==6){
                break;
            }
        }

        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                m[i][j]/=num;
                System.out.print(" "+m[i][j]);
            }
            System.out.println();
        }


    }

    public static void digui(char[][]map, int x,int y){
        if(x==0||y==0||x==m-1||y==n-1){
            if(ans<min){
                min= ans;
            }
            return;
        }
        if(map[x-1][y]!='#'){
            if(map[x-1][y]=='*'){
                map[x-1][y]='.';
                ans+=1;
                digui(map,x-1,y);
                map[x-1][y]='#';
                ans-=1;
            }else{
                digui(map,x-1,y);
                map[x-1][y]='#';
            }
        }
        if(map[x+1][y]!='#'){
            if(map[x+1][y]=='*'){
                map[x+1][y]='.';
                ans+=1;
                digui(map,x+1,y);
                map[x+1][y]='#';
                ans-=1;
            }else{
                digui(map,x+1,y);
                map[x-1][y]='#';
            }
        }
        if(map[x][y-1]!='#'){
            if(map[x][y-1]=='*'){
                map[x][y-1]='.';
                ans+=1;
                digui(map,x,y-1);
                map[x][y-1]='#';
                ans-=1;
            }else{
                digui(map,x,y-1);
                map[x-1][y]='#';
            }
        }
        if(map[x][y+1]!='#'){
            if(map[x][y+1]=='*'){
                map[x][y+1]='.';
                ans+=1;
                digui(map,x,y+1);
                map[x][y+1]='#';
                ans-=1;
            }else{
                digui(map,x,y+1);
                map[x-1][y]='#';
            }
        }
        return;
    }
    public static void main22(String[] args) {
        Scanner in = new Scanner(System.in);
        String str = in.nextLine();
        if(str==null||str.length()==0){
            System.out.print(0);
        }
        if(str.length()==1){
            System.out.print(1);
        }
        int left =0, right =0, n=str.length();
        HashMap<Character, Integer> map = new HashMap<>();
        int max =0;
        while(right<n){
            char ch = str.charAt(right);
            map.put(ch,map.getOrDefault(ch,0)+1);
            if(map.get(ch)==2){
                while(left<right&&map.get(ch)==2){
                    char ch2 =str.charAt(left);
                    map.put(ch2, map.get(ch2)-1);
                    left++;
                }
            }
            max =Math.max(right-left+1,max);
            right++;
        }
        System.out.print(max);
    }
    public static void main2(String[] args) {
        Scanner in = new Scanner(System.in);
        int m = in.nextInt();
        int n = in.nextInt();
        int mat[][] = new int[m][n];
            //注意while处理多个case
            for(int i=0;i<m;i++){
                for (int j=0;j<n;j++){
                    mat[i][j] = in.nextInt();
                }
            }
            int iii=Math.round(2);
            int pos =1,ans=n;
        boolean mark =true;
        displayMatrix(mat);
        System.out.println();
        for(int hangshu=1;hangshu<m/2;hangshu++){
            int beishu = m/hangshu;
            if(m%hangshu!=0){
                continue;
            }
            int temp[][][]= new int[beishu][hangshu][n];
            for(int i=0;i<beishu;i++){
                for(int j=0;j<hangshu;j++){
                    for(int k=0;k<n;k++){
                        temp[i][j][k] = mat[i*hangshu+j][k];
                    }
                }
            }

            boolean can= true;
            for(int i=2;i<=beishu;i++){
                int [][]t1=temp[0];
                if(i%2==0){
                    if(!ieq(t1,temp[i-1])){
                        can =false;
                        break;
                    }
                }
                if(i%2==1){
                    if(!eq(t1,temp[i-1])){
                        can =false;
                        break;
                    }
                }
            }

            if(can&&hangshu<ans){
                ans=hangshu;
            }

        }
        int res[][]=new int[ans][n];
        for(int i=0;i<ans;i++){
            res[i]=mat[i];
        }
        displayMatrix(res);
    }

    public static void displayMatrix(int matrix[][]) {
        for (int i=0;i<matrix.length; i++) {
            for (int j=0;j<matrix[0].length;j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }


    private static boolean ieq(int[][] mat, int [][]mat2){
        boolean mark =true;
        for(int i=0;i<mat.length;i++){
            for(int j=0;j<mat[0].length;j++){
                if(mat[i][j]!=mat2[mat.length-i-1][j]){
                    return false;
                }
            }
        }
        return mark;
    }

    private static boolean eq(int[][] mat, int [][]mat2){
        boolean mark =true;
        for(int i=0;i<mat.length;i++){
            for(int j=0;j<mat[0].length;j++){
                if(mat[i][j]!=mat2[i][j]){
                    return false;
                }
            }
        }
        return mark;
    }
    public boolean eq(int[][] mat, int m1, int m2){
        boolean mark =true;
        for(int i=0;i<mat[0].length;i++){
            if(mat[m1][i]!=mat[m2][i]){
                return false;
            }
        }
        return mark;
    }

    static int solution(String s) {
        Integer min =new Integer(Integer.MAX_VALUE);
        Integer path= new Integer(0);
        List<Integer> list =new ArrayList<>();
        list.add(Integer.MAX_VALUE);
        minop(s,0,list,path);
        return list.get(0);
    }

    public static void minop(String s, int start, List<Integer> list, Integer path){
        if(start==s.length()){
            return;
        }
        if(s.charAt(start)==s.charAt(s.length()-1)){
            if(path+1<list.get(0)){
                list.remove(0);
                list.add(path.intValue()+1);
                return;
            }
        }
        for(int i=start;i<s.length();i++){
            for(int j=i;j<s.length();j++){
                if(s.charAt(i)==s.charAt(j)){
                    path+=1;
                    String sss= s.substring(i,j+1);
                    String ss= s.substring(j+1,s.length());
                    minop(s,j+1,list,path);
                    path-=1;
                }
            }
        }
    }
}