package com.yxt.aipl.fluency.score;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.yxt.aipl.fluency.score.AsrWord;



public class FluencyUtils {

     public  static String readFile(String filePath ) {
         try {
             FileInputStream fin = new FileInputStream(filePath);
             InputStreamReader reader = new InputStreamReader(fin);
             BufferedReader buffReader = new BufferedReader(reader);
             String strTmp = "";
             StringBuffer sb = new StringBuffer();
             while ((strTmp = buffReader.readLine()) != null) {
                 sb.append(strTmp);
             }
             buffReader.close();
             return sb.toString();
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }
     }



     public static List<AsrWord> getWordList(JSONArray arr){
         StringBuffer sb = new StringBuffer();
         List<AsrWord>wordList = new ArrayList<>();
         for(int i=0;i<arr.length();i++){
             JSONObject o  = arr.getJSONObject(i);
             JSONArray wordArr = o.getJSONObject("data").getJSONObject("result").getJSONArray("ws");
//             System.out.println(wordArr);
             for(int j=0;j<wordArr.length();j++){
                 JSONObject word = wordArr.getJSONObject(j);
//                 System.out.println(word);
                 String text = word.getJSONArray("cw").getJSONObject(0).getString("w");
                 int begin = word.getInt("bg");
                 sb.append(text);
                 AsrWord asrWord = new AsrWord(begin,text);
                 wordList.add(asrWord);
             }
         }
//         System.out.println(sb.toString());
         return wordList;
     }

     public static void run(){
         String s = readFile("/home/liuchu/fluency/src/main/java/com/yxt/aipl/fluency/score/full_data.json");
         JSONObject obj = new JSONObject(s);
//          = obj.getJSONArray("洗衣机语料1-正常说话-语速偏快-2.wav");
         Iterator<String> iterator = obj.keys();
         while(iterator.hasNext()){
             String key = iterator.next();
             System.out.println(key);
             JSONArray arr = obj.getJSONArray(key);
             calculate(arr);
         }
     }


     public static void calculate(JSONArray arr){
         List<AsrWord> wordList = getWordList(arr);
         AsrContent asrContent = new AsrContent(wordList);
         float cv = FluencyUtils.cvOf(asrContent.getSpeedList());
         System.out.println(asrContent);
         System.out.println(cv);
         //整体水平是否平稳
         float val1 = 100*(1-cv);
         System.out.println(100*(1-cv));
         float intervalCv = FluencyUtils.cvOf(asrContent.getSentenceIntervalList());
         //停顿是否忽长忽短
         float val2 = 100*(1-intervalCv);
         System.out.println(100*(1-intervalCv));
         //平均语速是否在正常说话范围内
         float val3 = 100*(1-Math.abs(asrContent.getAverageSpeed()-280)/280);
         System.out.println(100*(1-Math.abs(asrContent.getAverageSpeed()-280)/280));
         float val = (float) (0.6*val1+0.2*val2+0.2*val3);
         System.out.println("total_score --> "+val);
         System.out.println("---------------------------------------------------------------------");

     }





     public static float cvOf(List<Float>numList){
         if(numList.size() == 0){
             return 0;
         }
         //离散系数
         float sum = 0;
         for(Float val : numList){
             sum += val;
         }
         float average = sum / numList.size();
         float std = 0;
         for(Float val : numList){
             std += Math.pow(val - average,2);
         }
         std = (float) Math.sqrt(std/numList.size());
         return std/average;
     }

















    public static void main(String[] args){

         run();
//        AsrContent asrContent = new AsrContent(FluencyUtils.getExample());
//        System.out.println(asrContent);
//        float cv = FluencyUtils.cvOf(asrContent.getSpeedList());
//        System.out.println(cv);

    }

}
