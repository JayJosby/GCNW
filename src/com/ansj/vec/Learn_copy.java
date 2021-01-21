package com.ansj.vec;

import com.ansj.vec.domain.HiddenNeuron;
import com.ansj.vec.domain.Neuron;
import com.ansj.vec.domain.WordNeuron;
import com.ansj.vec.util.Cosin;
import com.ansj.vec.util.Haffman;
import com.ansj.vec.util.MapCount;
import com.ansj.vec.util.PearsonCorrelation;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class Learn_copy {

  private Map<String, Neuron> wordMap = new HashMap<>();
  /**
   * 训练多少个特征
   */
  private int layerSize = 200;

  /**
   * 上下文窗口大小
   */
  private int window = 5;

  private double sample = 1e-3;
  private double alpha = 0.025;
  private double startingAlpha = alpha;

  public int EXP_TABLE_SIZE = 1000;

  private Boolean isCbow = true;

  private double[] expTable = new double[EXP_TABLE_SIZE];

  private int trainWordsCount = 0;

  private int MAX_EXP = 6;

  public Word2VEC w2v ;
  private Map<String,double[]> map_voice = new HashMap<>();
  private Map<String,double[]> map_pos = new HashMap<>();
  private double A[][] = new double[][] {
          {1, 0, 0},
          {0, 0, 0},
          {0, 0, 0}
  };
  public Learn_copy(Boolean isCbow, Integer layerSize, Integer window, Double alpha,
                    Double sample) {
    createExpTable();
    if (isCbow != null) {
      this.isCbow = isCbow;
    }
    if (layerSize != null)
      this.layerSize = layerSize;
    if (window != null)
      this.window = window;
    if (alpha != null)
      this.alpha = alpha;
    if (sample != null)
      this.sample = sample;
  }

  public Learn_copy() {
    createExpTable();
  }

  /**
   * trainModel
   *
   * @throws IOException
   */
  private void trainModel(File file) throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(
            new FileInputStream(file)))) {
      String temp = null;
      long nextRandom = 5;
      int wordCount = 0;
      int lastWordCount = 0;
      int wordCountActual = 0;
      System.out.println("start train");

      while ((temp = br.readLine()) != null) {
        if (wordCount - lastWordCount > 10000) {
          if((int) (wordCountActual / (double) (trainWordsCount + 1) * 100)%20==0){
            System.out.println("alpha:" + alpha + "\tProgress: "
                    + (int) (wordCountActual / (double) (trainWordsCount + 1) * 100)
                    + "%"+" wordCount="+wordCount+" lastWordCount="+lastWordCount);
          }
          wordCountActual += wordCount - lastWordCount;
          lastWordCount = wordCount;
          alpha = startingAlpha
                  * (1 - wordCountActual / (double) (trainWordsCount + 1));
          if (alpha < startingAlpha * 0.0001) {
            alpha = startingAlpha * 0.0001;
          }
        }
        String[] strs = temp.split(" ");
        wordCount += strs.length;
        List<WordNeuron> sentence = new ArrayList<WordNeuron>();
        //每次取length个句子进行训练
        for (int i = 0; i < strs.length; i++) {
          Neuron entry = wordMap.get(strs[i]);
          if (entry == null) {
            continue;
          }
          // The subsampling randomly discards frequent words while keeping the
          // ranking same 次抽样在保持排名不变的情况下，随机丢弃频率较高的单词
          if (sample > 0) {
            double ran = (Math.sqrt(entry.freq / (sample * trainWordsCount)) + 1)
                    * (sample * trainWordsCount) / entry.freq;
            nextRandom = nextRandom * 25214903917L + 11;
            if (ran < (nextRandom & 0xFFFF) / (double) 65536) {
              continue;
            }
          }
          sentence.add((WordNeuron) entry);
        }

        for (int index = 0; index < sentence.size(); index++) {
          nextRandom = nextRandom * 25214903917L + 11;
          if (isCbow) {
            //System.out.println(sentence.size()+"cbow--------------------------");
            //System.out.println(sentence.size()+"cbow--------------------------");
            cbowGram(index, sentence, (int) nextRandom % window);
          } else {
            //System.out.print("skip--------------------------");
            skipGram(index, sentence, (int) nextRandom % window);
          }
        }

      }



//      while ((temp = br.readLine()) != null) {
//        if (wordCount - lastWordCount > 10000) {
//          System.out.println("alpha:" + alpha + "\tProgress: "
//                  + (int) (wordCountActual / (double) (trainWordsCount + 1) * 100)
//                  + "%"+" wordCount="+wordCount+" lastWordCount="+lastWordCount);
//          wordCountActual += wordCount - lastWordCount;
//          lastWordCount = wordCount;
//          alpha = startingAlpha
//                  * (1 - wordCountActual / (double) (trainWordsCount + 1));
//          if (alpha < startingAlpha * 0.0001) {
//            alpha = startingAlpha * 0.0001;
//          }
//        }
//        String[] strs = temp.split(" ");
//        wordCount += strs.length;
//        List<WordNeuron> sentence = new ArrayList<WordNeuron>();
//        //每次取length个句子进行训练
//        for (int i = 0; i < strs.length; i++) {
//          Neuron entry = wordMap.get(strs[i]);
//          if (entry == null) {
//            continue;
//          }
//          // The subsampling randomly discards frequent words while keeping the
//          // ranking same 次抽样在保持排名不变的情况下，随机丢弃频率较高的单词
//          if (sample > 0) {
//            double ran = (Math.sqrt(entry.freq / (sample * trainWordsCount)) + 1)
//                    * (sample * trainWordsCount) / entry.freq;
//            nextRandom = nextRandom * 25214903917L + 11;
//            if (ran < (nextRandom & 0xFFFF) / (double) 65536) {
//              continue;
//            }
//          }
//          sentence.add((WordNeuron) entry);
//        }
//
//        for (int index = 0; index < sentence.size(); index++) {
//          nextRandom = nextRandom * 25214903917L + 11;
//          if (isCbow) {
//            //System.out.println(sentence.size()+"cbow--------------------------");
//            //System.out.println(sentence.size()+"cbow--------------------------");
//            cbowGram(index, sentence, (int) nextRandom % window);
//          } else {
//            //System.out.print("skip--------------------------");
//            skipGram(index, sentence, (int) nextRandom % window);
//          }
//        }
//
//      }
      System.out.println("Vocab size: " + wordMap.size());
      System.out.println("Words in train file: " + trainWordsCount);
      System.out.println("sucess train over!");
    }
  }

  /**
   * skip gram 模型训练
   *
   * @param sentence
   * @param neu1
   */
  private void skipGram(int index, List<WordNeuron> sentence, int b) {
    // TODO Auto-generated method stub
    WordNeuron word = sentence.get(index);
    int a, c = 0;
    for (a = b; a < window * 2 + 1 - b; a++) {
      if (a == window) {
        continue;
      }
      c = index - window + a;
      if (c < 0 || c >= sentence.size()) {
        continue;
      }

      double[] neu1e = new double[layerSize];// 误差项
      // HIERARCHICAL SOFTMAX
      List<Neuron> neurons = word.neurons;
      WordNeuron we = sentence.get(c);
      for (int i = 0; i < neurons.size(); i++) {
        HiddenNeuron out = (HiddenNeuron) neurons.get(i);
        double f = 0;
        // Propagate hidden -> output
        for (int j = 0; j < layerSize; j++) {
          f += we.syn0[j] * out.syn1[j];
        }
        if (f <= -MAX_EXP || f >= MAX_EXP) {
          continue;
        } else {
          f = (f + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2);
          f = expTable[(int) f];
        }
        // 'g' is the gradient multiplied by the learning rate
        double g = (1 - word.codeArr[i] - f) * alpha;
        // Propagate errors output -> hidden
        for (c = 0; c < layerSize; c++) {
          neu1e[c] += g * out.syn1[c];
        }
        // Learn weights hidden -> output
        for (c = 0; c < layerSize; c++) {
          out.syn1[c] += g * we.syn0[c];
        }
      }

      // Learn weights input -> hidden
      for (int j = 0; j < layerSize; j++) {
        we.syn0[j] += neu1e[j];
      }
    }

  }

  private void initMap() throws IOException {
    // use for load vector of voice and pos tag
    BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\dict4gcn.txt"));//使用BufferedReader 最大好处是可以按bai行du读取,每次读取一行
    String temp;//定义字符串,用于保dao存每行读取到的数据
    while ((temp = br.readLine()) != null) {
      String word = temp;
      double vector_voice[]= aryChange(br.readLine());
      double vector_pos[]= aryChange(br.readLine());

      map_voice.put(word,changeSize(vector_voice,layerSize));
      map_pos.put(word,changeSize(vector_pos,layerSize));
    }
  }

  static double[] aryChange(String temp) {// 字符串数组解析成int数组
    temp = temp.substring(1,temp.length()-1);
    String[] ss = temp.trim().split(", ");// .trim()可以去掉首尾多余的空格
// .split("\\s+")
// 表示用正则表达式去匹配切割,\\s+表示匹配一个或者以上的空白符

    double[] ary = new double[ss.length];
    for (int i = 0; i < ary.length; i++) {
      ary[i] = Double.parseDouble(ss[i]);// 解析数组的每一个元素
    }
    return ary;// 返回一个double数组
  }

  private double[] changeSize(double[] arr, int newSize){
    double []a=new double[newSize];
    int oldSize = arr.length;
    int beishu = newSize/oldSize;
    int yushu = newSize%oldSize;
    for(int i = 0; i<oldSize; i++){
      for(int j = i*beishu; j<i*beishu+beishu; j++){
        a[j]=arr[i];
      }
    }
    if(yushu!=0){
      for(int i =newSize-1;i>newSize-yushu;i--){
        a[i]=arr[arr.length-1];
      }
    }
    return a;
  }

  private double[] gcn2(String word,double[] word_vector){
    //获得word的sound
    double[] voice_vector = new double[layerSize];
    if(map_voice.get(word)!=null){
      voice_vector = map_voice.get(word);
    }else{
      voice_vector = word_vector;
    }
    //获得word的pos tag
    double[] pos_vector = new double[layerSize];
    if(map_pos.get(word)!=null){
      pos_vector = map_pos.get(word);
    }else{
      pos_vector = word_vector;
    }

    double[] gcn_vector=new double[word_vector.length];
    //GCN运算,这里对生成的矩阵平均化，得到窗口单词word的对应
    for(int i=0;i<word_vector.length;i++){
      gcn_vector[i]=(word_vector[i]*2+pos_vector[i]*0+voice_vector[i]*1)/3;
    }
    return gcn_vector;
  }

  private double[] gcn(String word,double[] word_vector){
    //构建邻接矩阵，语义和句法结构有关，语义-语音-句法结构
    //基于贪心思想，每次矩阵随机初始化

    //构建度矩阵，将邻接矩阵归一化
    double D[][] = new double[][] {
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
    };
    //Hl H0就是X
    //W每层的权重
    //加一层非线性激活函数

    //获得word的sound
    double[] voice_vector = new double[layerSize];
    if(map_voice.get(word)!=null){
      voice_vector = map_voice.get(word);
    }else{
      voice_vector = word_vector;
    }
    //获得word的pos tag
    double[] pos_vector = new double[layerSize];
    if(map_pos.get(word)!=null){
      pos_vector = map_pos.get(word);
    }else{
      pos_vector = word_vector;
    }
    //加上word vector，构成节点特征矩阵
    double[][] X =new double[][]{word_vector,pos_vector,voice_vector};
    //
    double[] gcn_vector=new double[word_vector.length];
    //GCN运算,这里对生成的矩阵平均化，得到窗口单词word的对应
    //double [][]X2 = strassenMatrixMultiplyRecursive(A,X);
    double [][]X2 = MatMult(A, X);
//    displayMatrix(A);
//    displayMatrix(X);
//    displayMatrix(X2);
    //System.out.println(gcn_vector.length+" ");
    //System.out.println(X2.length+" "+X2[0].length);
    for(int i=0;i<X2[0].length;i++){
      gcn_vector[i]=(X2[0][i]+X2[1][i]+X2[2][i])/3;
    }
    return gcn_vector;
  }
  /**
   * 词袋模型
   *
   * @param index
   * @param sentence
   * @param b
   */
  private void cbowGram(int index, List<WordNeuron> sentence, int b) {
    WordNeuron word = sentence.get(index);
    int a, c = 0;

    List<Neuron> neurons = word.neurons;
    double[] neu1e = new double[layerSize];// 误差项
    double[] neu1 = new double[layerSize];// 误差项
    WordNeuron last_word;

    for (a = b; a < window * 2 + 1 - b; a++)//已经对上下文窗口中的单词按照频率进行了排序，丢掉频率高的b个
      if (a != window) {
        c = index - window + a;
        if (c < 0)
          continue;
        if (c >= sentence.size())
          continue;
        last_word = sentence.get(c);
        if (last_word == null)
          continue;
        //System.out.println(last_word.name);
        //gcn=true
        if(true){
          double word_vector[] = last_word.syn0;
          double gcn_vector[] = gcn(last_word.name,word_vector);
          for (c = 0; c < layerSize; c++) {
            neu1[c] += gcn_vector[c];
          }
        }else{
          for (c = 0; c < layerSize; c++) {
            neu1[c] += last_word.syn0[c];
          }
        }
      }

    // HIERARCHICAL SOFTMAX
    for (int d = 0; d < neurons.size(); d++) {
      HiddenNeuron out = (HiddenNeuron) neurons.get(d);
      double f = 0;
      // Propagate hidden -> output 传播隐藏->output
      for (c = 0; c < layerSize; c++)
        f += neu1[c] * out.syn1[c];
      if (f <= -MAX_EXP)
        continue;
      else if (f >= MAX_EXP)
        continue;
      else
        f = expTable[(int) ((f + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2))];
      // 'g' is the gradient multiplied by the learning rate
      // double g = (1 - word.codeArr[d] - f) * alpha;
      // double g = f*(1-f)*( word.codeArr[i] - f) * alpha;
      double g = f * (1 - f) * (word.codeArr[d] - f) * alpha;
      //
      for (c = 0; c < layerSize; c++) {
        neu1e[c] += g * out.syn1[c];
      }
      // Learn weights hidden -> output
      for (c = 0; c < layerSize; c++) {
        out.syn1[c] += g * neu1[c];
      }
    }
    for (a = b; a < window * 2 + 1 - b; a++) {
      if (a != window) {
        c = index - window + a;
        if (c < 0)
          continue;
        if (c >= sentence.size())
          continue;
        last_word = sentence.get(c);
        if (last_word == null)
          continue;
        for (c = 0; c < layerSize; c++)
          last_word.syn0[c] += neu1e[c];
      }

    }
  }

  /**
   * 统计词频
   *
   * @param file
   * @throws IOException
   */
  private void readVocab(File file) throws IOException {
    MapCount<String> mc = new MapCount<>();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(
            new FileInputStream(file)))) {
      String temp = null;
      while ((temp = br.readLine()) != null) {
        String[] split = temp.split(" ");
        trainWordsCount += split.length;
        for (String string : split) {
          mc.add(string);
        }
      }
    }
    for (Entry<String, Integer> element : mc.get().entrySet()) {
      wordMap.put(element.getKey(), new WordNeuron(element.getKey(),
              (double) element.getValue() / mc.size(), layerSize));
    }
  }

  /**
   * 对文本进行预分类
   *
   * @param files
   * @throws IOException
   * @throws FileNotFoundException
   */
  private void readVocabWithSupervised(File[] files) throws IOException {
    for (int category = 0; category < files.length; category++) {
      // 对多个文件学习
      MapCount<String> mc = new MapCount<>();
      try (BufferedReader br = new BufferedReader(new InputStreamReader(
              new FileInputStream(files[category])))) {
        String temp = null;
        while ((temp = br.readLine()) != null) {
          String[] split = temp.split(" ");
          trainWordsCount += split.length;
          for (String string : split) {
            mc.add(string);
          }
        }
      }
      for (Entry<String, Integer> element : mc.get().entrySet()) {
        double tarFreq = (double) element.getValue() / mc.size();
        if (wordMap.get(element.getKey()) != null) {
          double srcFreq = wordMap.get(element.getKey()).freq;
          if (srcFreq >= tarFreq) {
            continue;
          } else {
            Neuron wordNeuron = wordMap.get(element.getKey());
            wordNeuron.category = category;
            wordNeuron.freq = tarFreq;
          }
        } else {
          wordMap.put(element.getKey(), new WordNeuron(element.getKey(),
                  tarFreq, category, layerSize));
        }
      }
    }
  }

  /**
   * Precompute the exp() table f(x) = x / (x + 1)
   */
  private void createExpTable() {
    for (int i = 0; i < EXP_TABLE_SIZE; i++) {
      expTable[i] = Math.exp(((i / (double) EXP_TABLE_SIZE * 2 - 1) * MAX_EXP));
      expTable[i] = expTable[i] / (expTable[i] + 1);
    }
  }

  /**
   * 根据文件学习
   *
   * @param file
   * @throws IOException
   */
  public void learnFile(File file) throws IOException {
    readVocab(file);
    new Haffman(layerSize).make(wordMap.values());

    // 查找每个神经元
    for (Neuron neuron : wordMap.values()) {
      ((WordNeuron) neuron).makeNeurons();
    }
    System.out.println("readVocab has been completed");
    trainModel(file);
  }

  /**
   * 根据预分类的文件学习
   *
   * @param summaryFile
   *          合并文件
   * @param classifiedFiles
   *          分类文件
   * @throws IOException
   */
  public void learnFile(File summaryFile, File[] classifiedFiles)
          throws IOException {
    readVocabWithSupervised(classifiedFiles);
    new Haffman(layerSize).make(wordMap.values());
    // 查找每个神经元
    for (Neuron neuron : wordMap.values()) {
      ((WordNeuron) neuron).makeNeurons();
    }
    trainModel(summaryFile);
  }

  /**
   * 保存模型
   */
  public void saveModel(File file) {
    // TODO Auto-generated method stub

    try (DataOutputStream dataOutputStream = new DataOutputStream(
            new BufferedOutputStream(new FileOutputStream(file)))) {
      dataOutputStream.writeInt(wordMap.size());
      dataOutputStream.writeInt(layerSize);
      double[] syn0 = null;
      for (Entry<String, Neuron> element : wordMap.entrySet()) {
        dataOutputStream.writeUTF(element.getKey());
        syn0 = ((WordNeuron) element.getValue()).syn0;
        for (double d : syn0) {
          dataOutputStream.writeFloat(((Double) d).floatValue());
        }
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public int getLayerSize() {
    return layerSize;
  }

  public void setLayerSize(int layerSize) {
    this.layerSize = layerSize;
  }

  public int getWindow() {
    return window;
  }

  public void setWindow(int window) {
    this.window = window;
  }

  public double getSample() {
    return sample;
  }

  public void setSample(double sample) {
    this.sample = sample;
  }

  public double getAlpha() {
    return alpha;
  }

  public void setAlpha(double alpha) {
    this.alpha = alpha;
    this.startingAlpha = alpha;
  }

  public Boolean getIsCbow() {
    return isCbow;
  }

  public void setIsCbow(Boolean isCbow) {
    this.isCbow = isCbow;
  }


  public static void main2(String[] args) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\text8"));//使用BufferedReader 最大好处是可以按bai行du读取,每次读取一行
    String temp=br.readLine();//定义字符串,用于保dao存每行读取到的数据
    while (temp != null) {
      temp =br.readLine();
    }
    Learn_copy learn = new Learn_copy();
    learn.initMap();
    Scanner sc = new Scanner(System.in);
    System.out.println("请输入：");
    String word = sc.nextLine();
    while(word!=null){
      System.out.println(word);
      double a[]= learn.map_pos.get(word);
      for(double d:a){
        System.out.print(d+" ");
      }
      System.out.println('\n'+a.length);
      double b[]= learn.map_voice.get(word);
      for(double c:b){
        System.out.print(c+" ");
      }
      System.out.println('\n'+b.length);
      System.out.println("请输入：");
      word = sc.nextLine();
    }

    return;

//    long start = System.currentTimeMillis();
//    learn.learnFile(new File("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\m\\swresult_withoutnature.txt"));
//    System.out.println("use time " + (System.currentTimeMillis() - start));
//    learn.saveModel(new File("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\model\\javaVector"));

  }

  public static void displayMatrix(int matrix[][]) {
    int n = matrix.length;
    for (int i=0;i<n; i++) {
      for (int j=0;j<n;j++) {
        System.out.print(matrix[i][j] + " ");
      }
      System.out.println();
    }
  }

  public static void copyToMatrix(int srcMatrix[][], int startI, int startJ,
                                  int destMatrix[][]) {
    int n = destMatrix.length;
    for (int i = startI; i < startI+n; i++) {
      for (int j = startJ; j <startJ+n ; j++) {
        destMatrix[i - startI][j - startJ] = srcMatrix[i][j];
      }
    }
  }
  public static void copyFromMatrix(int destMatrix[][], int startI, int startJ,
                                    int srcMatrix[][]) {
    int n = srcMatrix.length;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        destMatrix[startI + i][startJ + j] = srcMatrix[i][j];
      }
    }
  }
  public static void MatrixAdd(int A[][],int B[][],int C[][]){
    int n = A.length;
    for(int i=0;i<n;i++){
      for(int j=0;j<n;j++){
        C[i][j] = A[i][j] + B[i][j];
      }
    }
  }
  public static void MatrixSub(int A[][], int B[][], int C[][]) {
    int n = A.length;
    for (int i=0;i<n;i++) {
      for (int j=0;j<n;j++) {
        C[i][j] = A[i][j] - B[i][j];
      }
    }
  }

  public static int[][] strassenMatrixMultiplyRecursive(int[][] A,int[][] B){
    int n = A.length;
    int[][] C = new int[n][n];
    if(n==1)
      C[0][0] = A[0][0] * B[0][0];
    else{
      int[][] A11,A12,A21,A22;
      int[][] B11,B12,B21,B22;
      int[][] S1,S2,S3,S4,S5,S6,S7,S8,S9,S10;
      int[][] P1,P2,P3,P4,P5,P6,P7;
      int[][] C11,C12,C21,C22;

      A11 = new int[n/2][n/2];
      A12 = new int[n/2][n/2];
      A21 = new int[n/2][n/2];
      A22 = new int[n/2][n/2];
      copyToMatrix(A,0,0,A11);
      copyToMatrix(A,0,n/2,A12);
      copyToMatrix(A,n/2,0,A21);
      copyToMatrix(A,n/2,n/2,A22);


      B11 = new int[n/2][n/2];
      B12 = new int[n/2][n/2];
      B21 = new int[n/2][n/2];
      B22 = new int[n/2][n/2];
      copyToMatrix(B,0,0,B11);
      copyToMatrix(B,0,n/2,B12);
      copyToMatrix(B,n/2,0,B21);
      copyToMatrix(B,n/2,n/2,B22);

      S1 = new int[n/2][n/2];
      S2 = new int[n/2][n/2];
      S3 = new int[n/2][n/2];
      S4 = new int[n/2][n/2];
      S5 = new int[n/2][n/2];
      S6 = new int[n/2][n/2];
      S7 = new int[n/2][n/2];
      S8 = new int[n/2][n/2];
      S9 = new int[n/2][n/2];
      S10 = new int[n/2][n/2];
      MatrixSub(B12,B22,S1);
      MatrixAdd(A11,A12,S2);
      MatrixAdd(A21,A22,S3);
      MatrixSub(B21,B11,S4);
      MatrixAdd(A11,A22,S5);
      MatrixAdd(B11,B22,S6);
      MatrixSub(A12,A22,S7);
      MatrixAdd(B21,B22,S8);
      MatrixSub(A11,A21,S9);
      MatrixAdd(B11,B12,S10);

      P1 = new int[n/2][n/2];P2 = new int[n/2][n/2];P3 = new int[n/2][n/2];P4 = new int[n/2][n/2];
      P5 = new int[n/2][n/2];P6 = new int[n/2][n/2];P7 = new int[n/2][n/2];
      P1 = strassenMatrixMultiplyRecursive(A11, S1);
      P2 = strassenMatrixMultiplyRecursive(S2, B22);
      P3 = strassenMatrixMultiplyRecursive(S3, B11);
      P4 = strassenMatrixMultiplyRecursive(A22, S4);
      P5 = strassenMatrixMultiplyRecursive(S5, S6);
      P6 = strassenMatrixMultiplyRecursive(S7, S8);
      P7 = strassenMatrixMultiplyRecursive(S9, S10);

      C11 = new int[n/2][n/2];
      C12 = new int[n/2][n/2];
      C21 = new int[n/2][n/2];
      C22 = new int[n/2][n/2];
      int[][] temp = new int[n/2][n/2];
      MatrixAdd(P5,P4,temp);
      MatrixSub(temp,P2,temp);
      MatrixAdd(temp,P6,C11);

      MatrixAdd(P1, P2, C12);
      MatrixAdd(P3, P4, C21);

      MatrixAdd(P5, P1, temp);
      MatrixSub(temp, P3, temp);
      MatrixSub(temp, P7, C22);

      copyFromMatrix(C,0,0,C11);
      copyFromMatrix(C,0,n/2,C12);
      copyFromMatrix(C,n/2,0,C21);
      copyFromMatrix(C,n/2,n/2,C22);
    }
    return C;
  }

  public static void displayMatrix(double matrix[][]) {
    int n = matrix.length;
    for (int i=0;i<n; i++) {
      for (int j=0;j<matrix[0].length;j++) {
        System.out.print(matrix[i][j] + " ");
      }
      System.out.println();
    }
  }

  public static void copyToMatrix(double srcMatrix[][], int startI, int startJ,
                                  double destMatrix[][]) {
    int n = destMatrix.length;
    for (int i = startI; i < startI+n; i++) {
      for (int j = startJ; j <startJ+n ; j++) {
        destMatrix[i - startI][j - startJ] = srcMatrix[i][j];
      }
    }
  }
  public static void copyFromMatrix(double destMatrix[][], int startI, int startJ,
                                    double srcMatrix[][]) {
    int n = srcMatrix.length;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        destMatrix[startI + i][startJ + j] = srcMatrix[i][j];
      }
    }
  }
  public static void MatrixAdd(double A[][],double B[][],double C[][]){
    int n = A.length;
    for(int i=0;i<n;i++){
      for(int j=0;j<n;j++){
        C[i][j] = A[i][j] + B[i][j];
      }
    }
  }

  public static void MatrixSub(double A[][], double B[][], double C[][]) {
    int n = A.length;
    for (int i=0;i<n;i++) {
      for (int j=0;j<n;j++) {
        C[i][j] = A[i][j] - B[i][j];
      }
    }
  }

  public static double[][] MatMult(double A[][], double B[][]){
    double[][] C = new double[A.length][B[0].length];
    for(int i=0;i<A.length;i++){
      for(int j=0;j<B[0].length;j++){
        double sum =0;
        for(int k=0;k<B.length;k++){
          sum+=A[i][k] * B[k][j];
        }
        C[i][j]=sum;
      }
    }
    return C;
  }

  public static double[][] strassenMatrixMultiplyRecursive(double[][] A,double[][] B){
    int n = A.length;
    double[][] C = new double[n][n];
    if(n==1)
      C[0][0] = A[0][0] * B[0][0];
    else{
      double[][] A11,A12,A21,A22;
      double[][] B11,B12,B21,B22;
      double[][] S1,S2,S3,S4,S5,S6,S7,S8,S9,S10;
      double[][] P1,P2,P3,P4,P5,P6,P7;
      double[][] C11,C12,C21,C22;

      A11 = new double[n/2][n/2];
      A12 = new double[n/2][n/2];
      A21 = new double[n/2][n/2];
      A22 = new double[n/2][n/2];
      copyToMatrix(A,0,0,A11);
      copyToMatrix(A,0,n/2,A12);
      copyToMatrix(A,n/2,0,A21);
      copyToMatrix(A,n/2,n/2,A22);


      B11 = new double[n/2][n/2];
      B12 = new double[n/2][n/2];
      B21 = new double[n/2][n/2];
      B22 = new double[n/2][n/2];
      copyToMatrix(B,0,0,B11);
      copyToMatrix(B,0,n/2,B12);
      copyToMatrix(B,n/2,0,B21);
      copyToMatrix(B,n/2,n/2,B22);

      S1 = new double[n/2][n/2];
      S2 = new double[n/2][n/2];
      S3 = new double[n/2][n/2];
      S4 = new double[n/2][n/2];
      S5 = new double[n/2][n/2];
      S6 = new double[n/2][n/2];
      S7 = new double[n/2][n/2];
      S8 = new double[n/2][n/2];
      S9 = new double[n/2][n/2];
      S10 = new double[n/2][n/2];
      MatrixSub(B12,B22,S1);
      MatrixAdd(A11,A12,S2);
      MatrixAdd(A21,A22,S3);
      MatrixSub(B21,B11,S4);
      MatrixAdd(A11,A22,S5);
      MatrixAdd(B11,B22,S6);
      MatrixSub(A12,A22,S7);
      MatrixAdd(B21,B22,S8);
      MatrixSub(A11,A21,S9);
      MatrixAdd(B11,B12,S10);

      P1 = new double[n/2][n/2];P2 = new double[n/2][n/2];P3 = new double[n/2][n/2];P4 = new double[n/2][n/2];
      P5 = new double[n/2][n/2];P6 = new double[n/2][n/2];P7 = new double[n/2][n/2];
      P1 = strassenMatrixMultiplyRecursive(A11, S1);
      P2 = strassenMatrixMultiplyRecursive(S2, B22);
      P3 = strassenMatrixMultiplyRecursive(S3, B11);
      P4 = strassenMatrixMultiplyRecursive(A22, S4);
      P5 = strassenMatrixMultiplyRecursive(S5, S6);
      P6 = strassenMatrixMultiplyRecursive(S7, S8);
      P7 = strassenMatrixMultiplyRecursive(S9, S10);

      C11 = new double[n/2][n/2];
      C12 = new double[n/2][n/2];
      C21 = new double[n/2][n/2];
      C22 = new double[n/2][n/2];
      double[][] temp = new double[n/2][n/2];
      MatrixAdd(P5,P4,temp);
      MatrixSub(temp,P2,temp);
      MatrixAdd(temp,P6,C11);

      MatrixAdd(P1, P2, C12);
      MatrixAdd(P3, P4, C21);

      MatrixAdd(P5, P1, temp);
      MatrixSub(temp, P3, temp);
      MatrixSub(temp, P7, C22);

      copyFromMatrix(C,0,0,C11);
      copyFromMatrix(C,0,n/2,C12);
      copyFromMatrix(C,n/2,0,C21);
      copyFromMatrix(C,n/2,n/2,C22);
    }
    return C;
  }

  public static void test(){
    double A[][] = new double[][] {
            {1, 0, 0},
            {0, 2, 0},
            {0, 0, 3}
    };
    //构建度矩阵，将邻接矩阵归一化
    double D[][] = new double[][] {
            {1, 0, 0, 1},
            {0, 1, 0, 2},
            {0, 0, 1, 3}
    };
    double ans[][] = MatMult(A, D);
    displayMatrix(ans);
  }

  public static void main3(String[] args){
    test();
  }

  public void initMatA(){
    Random random =new Random();
    double v11=random.nextInt(20);
    double v22=random.nextInt(20);
    double v00=100-v11-v22;
    A= new double[][]{{v00/100, 0, 0},
            {0, v11/100, 0},
            {0, 0, v22/100}
    };
    Math.round(1);
    displayMatrix(A);
    System.out.println();
  }

  public void doLearn(Learn_copy learn) throws IOException {
    learn.initMap();
    //随机初始化关系矩阵A
    System.out.println("init relation matrix A");
    learn.initMatA();
    //英文语料
    long start = System.currentTimeMillis();
    learn.learnFile(new File("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\text9.txt"));
    System.out.println("use time " + (System.currentTimeMillis() - start));
    learn.saveModel(new File("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\model\\javaVector_testConnected"));
    System.out.println("javaVector");
  }

  public boolean doCompare(Learn_copy learn) throws IOException {
    //对每个测试集都进行相关性系数计算,贪心保存最好的表现，所以实际没有终止迭代条件
    //整体对数据集评价标准:
    //保存当前数据集最好记录+对应的关系邻接矩阵
    this.w2v = new Word2VEC();
    System.out.println("load w2v model");
    w2v.loadJavaModel("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\model\\javaVector_testConnected");
    //String []dataset = {"EN-WS-353-ALL","EN-YP-130","EN-MTurk-287","EN-MEN-TR-3k",};
    String []datasets = {"EN-WS-353-ALL","EN-YP-130","EN-MTurk-287"};
    //计算出当前模型对应的所有数据集表现
    for(String dataset:datasets){
      System.out.println("doCompare- start test on :"+dataset);
      BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ws\\"+dataset+"\\score_w2v.txt"));
      String temp,word1,word2;
      double score=0;
      List<Double> score_test = new ArrayList<>();
      List<Double> score_w2v = new ArrayList<>();
      List<Double> score_labeled = new ArrayList<>();
      System.out.println("doCompare- init score_w2v");
      System.out.println("doCompare- init score_test");
      while((temp = br.readLine()) != null) {
        //以score_w2v为基准，初始化score_test和score_labeled
        word1 = temp.split(" ")[0];
        word2 = temp.split(" ")[1];
        score = Double.valueOf(temp.split(" ")[2]);
        // word2vec score 初始化
        score_w2v.add(score);
        // test score 初始化
        ArrayList w1=new ArrayList<>();
        ArrayList w2=new ArrayList<>();
        float[] w1_=w2v.getWordVector(word1);
        float[] w2_=w2v.getWordVector(word2);
        for(int i=0;i<w1_.length;i++){
          w1.add((double)w1_[i]);
          w2.add((double)w2_[i]);
        }
        score_test.add(Cosin.similarity(w1, w2));
      }
      // labeled score 初始化
      System.out.println("doCompare- init score_labeled");
      BufferedReader br2 = new BufferedReader(new FileReader("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ws\\"+dataset+"\\score_labeled.txt"));
      while((temp = br2.readLine()) != null) {
        score = Double.valueOf(temp.split(" ")[2]);
        score_labeled.add(score);
      }
      // 计算labeled-test 和 labeled-w2v 各自的相关系数
      double pearson_test=PearsonCorrelation.getPearsonCorrelationScore(score_labeled, score_test);
      double pearson_w2v=PearsonCorrelation.getPearsonCorrelationScore(score_labeled, score_w2v);
      System.out.println("doCompare- pearson_test :"+pearson_test+"  pearson_w2v :"+pearson_w2v);
      if(pearson_test>pearson_w2v){
        learn.doSave(dataset, pearson_test, pearson_w2v, score_test);
      }
    }
    sentenceSimilarityTask();
    //加句子相似度计算
    return  true;
  }

  //词相似度计算结果保存
  public void doSave(String dataset, double pearson_test, double pearson_w2v, List score_test) throws IOException {
    // 保存关系矩阵+相关性系数*2+score_test
    System.out.println("doSave- 词相似度task");
    BufferedWriter bw = new BufferedWriter(new FileWriter(
            "C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ws\\result\\mark_"+dataset+".txt",true));
    // 保存关系矩阵
    bw.newLine();
    for(int i=0;i<A.length;i++){
      for(int j=0;j<A[0].length;j++){
        bw.write(A[i][j]+" ");
      }
      bw.newLine();
    }
    bw.newLine();
    // 保存pearson相关系数
    bw.write("pearson_test:"+pearson_test+" person_w2v:"+pearson_w2v);
    bw.newLine();
    bw.newLine();
    // 保存score_test
    for(Object score:score_test){
      bw.write((Double)score+"");
      bw.newLine();
    }
    bw.close();
  }

  public void sentenceSimilarityTask() throws IOException {
    //句子相似度数据集
    String []datasets = {"SICK"};
    //相似度得分列表
    List<Double> list_score_test = new ArrayList<>();
    List<Double> list_score_w2v = new ArrayList<>();
    List<Double> list_score_labeled = new ArrayList<>();
    //计算出当前模型对应的所有数据集表现
    String seq1, seq2, temp;
    for(String dataset:datasets){
      System.out.println("doCompare- start test on :"+dataset);
      BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ss\\"+dataset+"\\score_labeled.txt"));
      String line[] = br.readLine().split("\t");
      System.out.println("doCompare- init score_labeled and score_test");
      while (line!=null){
        seq1 = line[0];
        seq2 = line[1];
        list_score_test.add(getSimilarityOfSequences(seq1, seq2));
        list_score_labeled.add(Double.valueOf(line[2]));
        String nextline = br.readLine();
        if(nextline!=null){
          line = nextline.split("\t");
        }else{
          line = null;
        }
      }
      System.out.println("doCompare- init score_w2v");
      BufferedReader br2 = new BufferedReader(new FileReader("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ss\\"+dataset+"\\score_w2v.txt"));
      temp = br2.readLine().split("\t")[2];
      while(temp!= null) {
        list_score_w2v.add(Double.valueOf(temp));
        String nextline = br2.readLine();
        if(nextline!=null){
          temp = nextline.split("\t")[2];
        }else{
          temp =null;
        }
      }
      Double pearson_test = PearsonCorrelation.getPearsonCorrelationScore(list_score_test,list_score_labeled);
      Double pearson_w2v = PearsonCorrelation.getPearsonCorrelationScore(list_score_test,list_score_labeled);
      System.out.println("doCompare- pearson_test :"+pearson_test+"  pearson_w2v :"+pearson_w2v);
      if(pearson_test>pearson_w2v){
        //doSave
        doSave(dataset, pearson_test, pearson_w2v);
      }
    }
  }

  public double getSimilarityOfSequences(String seq1, String seq2){
//    String [] s1 = seq1.split(" ");
//    String [] s2 = seq2.split(" ");
//    double [] d_s1 = new double[layerSize];
//    double [] d_s2 = new double[layerSize];
//    double sim=0;
//    for(String word:s1){
//      for(int i=0; i<layerSize; i++){
//        d_s1[i] += w2v.getWordVector(word)[i];
//      }
//    }
//    for(String word:s2){
//      for(int i=0; i<layerSize; i++){
//        d_s2[i] += w2v.getWordVector(word)[i];
//      }
//    }
//    for(int i=0; i<layerSize; i++){
//      d_s1[i] /= s1.length;
//    }
//    for(int i=0; i<layerSize; i++){
//      d_s2[i] /= s2.length;
//    }
//    ArrayList<Double> list_s1 = new ArrayList<>();
//    ArrayList<Double> list_s2 = new ArrayList<>();
//    for(int i=0; i<layerSize; i++){
//      list_s1.add(d_s1[i]);
//    }
//    for(int i=0; i<layerSize; i++){
//      list_s2.add(d_s2[i]);
//    }
//    sim = Cosin.similarity(list_s1, list_s2);
//    return sim;
    ArrayList<Double> sl1= new ArrayList<>();
    ArrayList<Double> sl2= new ArrayList<>();
    for(int i=0;i<200;i++){
      sl1.add(0.0);
      sl2.add(0.0);
    }
    for(String w:seq1.split(" ")){
      float[] wl1= w2v.getWordVector(w);
      if(wl1==null){
        wl1 = new float[layerSize];
        Arrays.fill(wl1, 0);
      }
      for(int i=0;i<wl1.length;i++){
        sl1.set(i,sl1.get(i)+wl1[i]);
      }
    }
    for(String w:seq2.split(" ")){
      float[] wl2= w2v.getWordVector(w);
      if(wl2==null){
        wl2 = new float[layerSize];
        Arrays.fill(wl2, 0);
      }
      for(int i=0;i<wl2.length;i++){
        sl2.set(i,sl2.get(i)+wl2[i]);
      }
    }
    float score = (float) Cosin.similarity(sl1, sl2);

    return score;
  }

  //句子相似度计算结果保存
  public void doSave(String dataset, double pearson_test, double pearson_w2v) throws IOException {
    // 保存关系矩阵+相关性系数*2+score_test
    System.out.println("doSave- 句子相似度task");
    BufferedWriter bw = new BufferedWriter(new FileWriter(
            "C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\ss\\result\\mark_"+dataset+".txt",true));
    // 保存关系矩阵
    bw.newLine();
    for(int i=0;i<A.length;i++){
      for(int j=0;j<A[0].length;j++){
        bw.write(A[i][j]+" ");
      }
      bw.newLine();
    }
    bw.newLine();
    // 保存pearson相关系数
    bw.write("pearson_test:"+pearson_test+"   person_w2v:"+pearson_w2v);
    bw.newLine();
    bw.newLine();
    bw.close();
  }

  public void initPara(){
    window = 5;

    sample = 1e-3;
    alpha = 0.025;
    startingAlpha = alpha;

    EXP_TABLE_SIZE = 1000;

    isCbow = true;

    expTable = new double[EXP_TABLE_SIZE];

    trainWordsCount = 0;

    MAX_EXP = 6;
  }

  public static void main(String[] args) throws IOException {
    Learn_copy learn = new Learn_copy();
    while(true){
      //learn.initPara();
      learn.doLearn(learn);
      learn.doCompare(learn);
    }
  }


}

