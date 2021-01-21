//package com.ansj.vec.util;
//
//
//import org.datavec.api.util.ClassPathResource;
//import org.deeplearning4j.models.glove.Glove;
//import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
//import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
//import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
//import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
//import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.Arrays;
//import java.util.Collection;
//
//public class GloVeTrainer {
//    public static void main(String[] args) throws FileNotFoundException {
//        String filePath = "/Users/shuubiasahi/Desktop/bayies/deeplearning/part-00000";
//        SentenceIterator iter = new BasicLineIterator(new File(filePath));
//        TokenizerFactory t = new DefaultTokenizerFactory();
//        t.setTokenPreProcessor(new CommonPreprocessor());
//        Glove glove = new Glove.Builder().iterate(iter).tokenizerFactory(t)
//                .alpha(0.75).learningRate(0.1)
//                .epochs(25)
//                .xMax(100)
//                .batchSize(1000)
//                .shuffle(true)
//                .symmetric(true).build();
//
//        glove.fit();
//        glove.save();
//
//        System.out.println("和微信最接近的10个词汇:" + glove.wordsNearest("微信", 10));
//        System.out.println(Arrays.toString(glove.getWordVector("微信")));
//        System.out.println("微信和qq的相似度为：" + glove.similarity("微信", "腾讯聊天账号"));
//        System.out.println("和美女最接近的10个词汇:" + glove.wordsNearest("腾讯聊天账号", 10));
//
//        System.exit(0);
//    }
//
//}
