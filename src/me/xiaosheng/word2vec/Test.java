package me.xiaosheng.word2vec;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.ansj.vec.Learn;
import com.ansj.vec.domain.WordEntry;

import me.xiaosheng.util.Segment;

public class Test {

    public static void main(String[] args) throws Exception {
//        Learn learn =new Learn();
//        learn.learnFile();
        System.out.println("ssssssssss");
        me.xiaosheng.word2vec.Word2Vec vec = new me.xiaosheng.word2vec.Word2Vec();
        vec.loadJavaModel("C:\\Users\\jayjosby.liu@sap.com\\Desktop\\Temp\\yuliao\\model\\javaVector_cn");
        test_cn(vec);
//        try {
//            vec.loadGoogleModel("data/wiki_chinese_word2vec(Google).model");
//            //vec.loadJavaModel();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        try {
//            Word2Vec.trainJavaModel("data/train.txt", "data/test.model");
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    public static void test_cn(Word2Vec vec) throws Exception {
        //计算词语相似度
        System.out.println("-----词语相似度-----");
        System.out.println(vec.getWordVector("自行车"));//自动调用了toString
        System.out.println(vec.getWordVector("汽车"));
        System.out.println(vec.getWordVector("猫"));
        System.out.println(vec.getWordVector("苹果"));
        System.out.println("automobile|car: " + vec.wordSimilarity("摩托车", "汽车"));
        System.out.println("apple|pear: " + vec.wordSimilarity("苹果", "梨"));
        System.out.println("apple|school: " + vec.wordSimilarity("苹果", "学校"));
        System.out.println("apple|computer: " + vec.wordSimilarity("苹果", "电脑"));
        System.out.println("apple|phone: " + vec.wordSimilarity("苹果", "手机"));
        System.out.println("computer|laptop: " + vec.wordSimilarity("电脑", "笔记本"));
        System.out.println("computer|people: " + vec.wordSimilarity("电脑", "人类"));
        //获取相似的词语
        Set<WordEntry> similarWords = vec.getSimilarWords("美丽", 10);
        System.out.println("与 [美丽] 语义相似的词语:");
        for(WordEntry word : similarWords) {
            System.out.println(word.name + " : " + word.score);
        }
        //计算句子相似度
        System.out.println("-----句子相似度-----");
        String s1 = "在苏州 许多高速公路正在建设中 导致一些地区的汽车行驶非常缓慢";
        String s2 = "最近 苏州的几条高速公路正在建设中 造成了一些地区的交通拥堵 使汽车难以通过 ";
        String s3 = "苏州是一个美丽的城市 四季分明 雨量充沛 ";
        System.out.println("s1: " + s1);
        System.out.println("s2: " + s2);
        System.out.println("s3: " + s3);
        //分词，获取词语列表
        List<String> wordList1 = Segment.getWords(s1);
        List<String> wordList2 = Segment.getWords(s2);
        List<String> wordList3 = Segment.getWords(s3);
        //快速句子相似度
        System.out.println("快速句子相似度:");
        System.out.println("s1|s1: " + vec.fastSentenceSimilarity(wordList1, wordList1));
        System.out.println("s1|s2: " + vec.fastSentenceSimilarity(wordList1, wordList2));
        System.out.println("s1|s3: " + vec.fastSentenceSimilarity(wordList1, wordList3));
        //句子相似度(所有词语权值设为1)
        System.out.println("句子相似度:");
        System.out.println("s1|s1: " + vec.sentenceSimilarity(wordList1, wordList1));
        System.out.println("s1|s2: " + vec.sentenceSimilarity(wordList1, wordList2));
        System.out.println("s1|s3: " + vec.sentenceSimilarity(wordList1, wordList3));
        //句子相似度(名词、动词权值设为1，其他设为0.8)
        System.out.println("句子相似度(名词、动词权值设为1，其他设为0.8):");
        float[] weightArray1 = Segment.getPOSWeightArray(Segment.getPOS(s1));
        float[] weightArray2 = Segment.getPOSWeightArray(Segment.getPOS(s2));
        float[] weightArray3 = Segment.getPOSWeightArray(Segment.getPOS(s3));
        System.out.println("s1|s1: " + vec.sentenceSimilarity(wordList1, wordList1, weightArray1, weightArray1));
        System.out.println("s1|s2: " + vec.sentenceSimilarity(wordList1, wordList2, weightArray1, weightArray2));
        System.out.println("s1|s3: " + vec.sentenceSimilarity(wordList1, wordList3, weightArray1, weightArray3));
    }

    public void test_en(Word2Vec vec) throws Exception {
        //计算词语相似度
        System.out.println("-----词语相似度-----");
        System.out.println(vec.getWordVector("automobile"));//自动调用了toString
        System.out.println(vec.getWordVector("car"));
        System.out.println(vec.getWordVector("cat"));
        System.out.println(vec.getWordVector("s"));
        System.out.println("automobile|car: " + vec.wordSimilarity("automobile", "car"));
        System.out.println("apple|pear: " + vec.wordSimilarity("apple", "pear"));
        System.out.println("apple|school: " + vec.wordSimilarity("apple", "school"));
        System.out.println("apple|computer: " + vec.wordSimilarity("apple", "computer"));
        System.out.println("apple|phone: " + vec.wordSimilarity("apple", "phone"));
        System.out.println("computer|laptop: " + vec.wordSimilarity("computer", "laptop"));
        System.out.println("computer|people: " + vec.wordSimilarity("computer", "people"));
        //获取相似的词语
        Set<WordEntry> similarWords = vec.getSimilarWords("pretty", 10);
        System.out.println("与 [pretty] 语义相似的词语:");
        for(WordEntry word : similarWords) {
            System.out.println(word.name + " : " + word.score);
        }
        //计算句子相似度
        System.out.println("-----句子相似度-----");
        String s1 = "A number of highways are under construction in Suzhou, causing cars to move very slowly in some areas.";
        String s2 = "Recently, several highways are under construction in Suzhou, causing traffic congestion in some areas and making it difficult for cars to pass.";
        String s3 = "Suzhou is a beautiful city with four distinct seasons and abundant rainfall.";
        System.out.println("s1: " + s1);
        System.out.println("s2: " + s2);
        System.out.println("s3: " + s3);
        //分词，获取词语列表
        List<String> wordList1 = Segment.getWords(s1);
        List<String> wordList2 = Segment.getWords(s2);
        List<String> wordList3 = Segment.getWords(s3);
        //快速句子相似度
        System.out.println("快速句子相似度:");
        System.out.println("s1|s1: " + vec.fastSentenceSimilarity(wordList1, wordList1));
        System.out.println("s1|s2: " + vec.fastSentenceSimilarity(wordList1, wordList2));
        System.out.println("s1|s3: " + vec.fastSentenceSimilarity(wordList1, wordList3));
        //句子相似度(所有词语权值设为1)
        System.out.println("句子相似度:");
        System.out.println("s1|s1: " + vec.sentenceSimilarity(wordList1, wordList1));
        System.out.println("s1|s2: " + vec.sentenceSimilarity(wordList1, wordList2));
        System.out.println("s1|s3: " + vec.sentenceSimilarity(wordList1, wordList3));
        //句子相似度(名词、动词权值设为1，其他设为0.8)
        System.out.println("句子相似度(名词、动词权值设为1，其他设为0.8):");
        float[] weightArray1 = Segment.getPOSWeightArray(Segment.getPOS(s1));
        float[] weightArray2 = Segment.getPOSWeightArray(Segment.getPOS(s2));
        float[] weightArray3 = Segment.getPOSWeightArray(Segment.getPOS(s3));
        System.out.println("s1|s1: " + vec.sentenceSimilarity(wordList1, wordList1, weightArray1, weightArray1));
        System.out.println("s1|s2: " + vec.sentenceSimilarity(wordList1, wordList2, weightArray1, weightArray2));
        System.out.println("s1|s3: " + vec.sentenceSimilarity(wordList1, wordList3, weightArray1, weightArray3));
    }
}
