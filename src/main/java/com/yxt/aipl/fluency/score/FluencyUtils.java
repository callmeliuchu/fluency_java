package com.yxt.aipl.fluency.score;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;




public class FluencyUtils {

     private static String readFile(String filePath ) {
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
             for(int j=0;j<wordArr.length();j++){
                 JSONObject word = wordArr.getJSONObject(j);
                 String text = word.getJSONArray("cw").getJSONObject(0).getString("w");
                 int begin = word.getInt("bg");
                 sb.append(text);
                 AsrWord asrWord = new AsrWord(begin,text);
                 wordList.add(asrWord);
             }
         }
         return wordList;
     }

     public static void test(){
         String s = readFile("/home/liuchu/fluency/src/main/java/com/yxt/aipl/fluency/score/full_data.json");
         JSONObject obj = new JSONObject(s);
         Iterator<String> iterator = obj.keys();
         while(iterator.hasNext()){
             String key = iterator.next();
             System.out.println(key);
             JSONArray arr = obj.getJSONArray(key);
             float score = scoreOf(arr);
             System.out.println(score);
         }
     }


     public static float scoreOf(JSONArray arr){
         float normalSpeed = 280;
         List<AsrWord> wordList = getWordList(arr);
         AsrContent asrContent = new AsrContent(wordList);
         float cv =  cvOf(asrContent.getSpeedList());
         //整体水平是否平稳
         float val1 = 100*(1-cv);
         float intervalCv = cvOf(asrContent.getSentenceIntervalList());
         //停顿是否忽长忽短
         float val2 = 100*(1-intervalCv);
         //平均语速是否在正常说话范围内
         float val3 = 100*(1-Math.abs(asrContent.getAverageSpeed()-normalSpeed)/normalSpeed);
         float val = (float) (0.6*val1+0.2*val2+0.2*val3);
         return val;
     }

     public static float scoreOf(String jsonStr){
         JSONArray arr = new JSONArray(jsonStr);
         return scoreOf(arr);
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
         float val = scoreOf("[{'code': 0, 'message': 'success', 'sid': 'iat00070033@dx170ecbd2db8a493802', 'data': {'status': 0, 'result': {'ls': False, 'bg': 0, 'ed': 0, 'vad': {'ws': [{'bg': 47, 'ed': 1129, 'eg': 63.37}]}, 'ws': [{'bg': 52, 'cw': [{'sc': 0, 'w': '您'}]}, {'bg': 96, 'cw': [{'sc': 0, 'w': '看'}]}, {'bg': 116, 'cw': [{'sc': 0, 'w': '你'}]}, {'bg': 132, 'cw': [{'sc': 0, 'w': '这样'}]}, {'bg': 160, 'cw': [{'sc': 0, 'w': '说'}]}, {'bg': 180, 'cw': [{'sc': 0, 'w': '的'}]}, {'bg': 200, 'cw': [{'sc': 0, 'w': '，'}]}, {'bg': 200, 'cw': [{'sc': 0, 'w': '我'}]}, {'bg': 212, 'cw': [{'sc': 0, 'w': '这'}]}, {'bg': 228, 'cw': [{'sc': 0, 'w': '什么'}]}, {'bg': 256, 'cw': [{'w': '都', 'sc': 0}]}, {'bg': 268, 'cw': [{'sc': 0, 'w': '不'}]}, {'bg': 284, 'cw': [{'sc': 0, 'w': '给'}]}, {'bg': 296, 'cw': [{'sc': 0, 'w': '你'}]}, {'bg': 308, 'cw': [{'sc': 0, 'w': '都'}]}, {'bg': 328, 'cw': [{'w': '不好意思', 'sc': 0}]}, {'bg': 408, 'cw': [{'w': '，', 'sc': 0}]}, {'bg': 408, 'cw': [{'sc': 0, 'w': '这样'}]}, {'bg': 448, 'cw': [{'sc': 0, 'w': '我'}]}, {'cw': [{'w': '觉得', 'sc': 0}], 'bg': 464}, {'bg': 496, 'cw': [{'sc': 0, 'w': '你'}]}, {'bg': 512, 'cw': [{'sc': 0, 'w': '这么'}]}, {'bg': 536, 'cw': [{'sc': 0, 'w': '会'}]}, {'bg': 556, 'cw': [{'sc': 0, 'w': '持家'}]}, {'cw': [{'w': '的', 'sc': 0}], 'bg': 588}, {'bg': 596, 'cw': [{'w': '人', 'sc': 0}]}, {'bg': 608, 'cw': [{'sc': 0, 'w': '，'}]}, {'cw': [{'sc': 0, 'w': '家'}], 'bg': 608}, {'bg': 624, 'cw': [{'sc': 0, 'w': '里面'}]}, {'bg': 648, 'cw': [{'sc': 0, 'w': '这些'}]}, {'bg': 688, 'cw': [{'sc': 0, 'w': '锅碗瓢盆'}]}, {'cw': [{'sc': 0, 'w': '你'}], 'bg': 748}, {'bg': 768, 'cw': [{'sc': 0, 'w': '肯定'}]}, {'bg': 808, 'cw': [{'sc': 0, 'w': '也'}]}, {'bg': 824, 'cw': [{'sc': 0, 'w': '有'}]}, {'bg': 864, 'cw': [{'sc': 0, 'w': '在'}]}, {'cw': [{'sc': 0, 'w': '说'}], 'bg': 884}, {'bg': 904, 'cw': [{'sc': 0, 'w': '，'}]}, {'bg': 904, 'cw': [{'sc': 0, 'w': '证明'}]}, {'bg': 936, 'cw': [{'w': '你', 'sc': 0}]}, {'bg': 944, 'cw': [{'sc': 0, 'w': '也'}]}, {'cw': [{'sc': 0, 'w': '知道'}], 'bg': 960}, {'bg': 1004, 'cw': [{'sc': 0, 'w': '也'}]}, {'bg': 1024, 'cw': [{'sc': 0, 'w': '不会'}]}, {'bg': 1052, 'cw': [{'sc': 0, 'w': '太'}]}, {'bg': 1068, 'cw': [{'sc': 0, 'w': '好'}]}, {'bg': 1084, 'cw': [{'sc': 0, 'w': '是'}]}, {'bg': 1096, 'cw': [{'w': '吧', 'sc': 0}]}], 'sn': 1}}}, {'code': 0, 'message': 'success', 'sid': 'iat00070033@dx170ecbd2db8a493802', 'data': {'result': {'sn': 2, 'ls': False, 'bg': 0, 'ed': 0, 'vad': {'ws': [{'bg': 1132, 'ed': 1302, 'eg': 62.69}]}, 'ws': [{'bg': 1133, 'cw': [{'w': '？', 'sc': 0}]}, {'cw': [{'sc': 0, 'w': '我'}], 'bg': 1133}, {'bg': 1165, 'cw': [{'sc': 0, 'w': '给'}]}, {'bg': 1177, 'cw': [{'sc': 0, 'w': '您'}]}, {'bg': 1189, 'cw': [{'sc': 0, 'w': '个'}]}, {'bg': 1213, 'cw': [{'sc': 0, 'w': '更'}]}, {'bg': 1233, 'cw': [{'sc': 0, 'w': '值钱'}]}]}, 'status': 1}}, {'code': 0, 'message': 'success', 'sid': 'iat00070033@dx170ecbd2db8a493802', 'data': {'result': {'sn': 3, 'ls': False, 'bg': 0, 'ed': 0, 'vad': {'ws': [{'bg': 1313, 'ed': 1487, 'eg': 64.81}]}, 'ws': [{'bg': 1330, 'cw': [{'sc': 0, 'w': '，'}]}, {'bg': 1330, 'cw': [{'sc': 0, 'w': '你'}]}, {'bg': 1346, 'cw': [{'sc': 0, 'w': '以后'}]}, {'bg': 1378, 'cw': [{'sc': 0, 'w': '能够'}]}, {'bg': 1406, 'cw': [{'sc': 0, 'w': '用'}]}, {'bg': 1426, 'cw': [{'w': '得到', 'sc': 0}]}, {'bg': 1454, 'cw': [{'sc': 0, 'w': '的'}]}]}, 'status': 1}}, {'code': 0, 'message': 'success', 'sid': 'iat00070033@dx170ecbd2db8a493802', 'data': {'result': {'sn': 4, 'ls': False, 'bg': 0, 'ed': 0, 'vad': {'ws': [{'bg': 1487, 'ed': 1983, 'eg': 62.75}]}, 'ws': [{'bg': 1492, 'cw': [{'sc': 0, 'w': '洗衣机'}]}, {'bg': 1540, 'cw': [{'sc': 0, 'w': '，'}]}, {'bg': 1540, 'cw': [{'sc': 0, 'w': '其实'}]}, {'cw': [{'sc': 0, 'w': '最'}], 'bg': 1568}, {'bg': 1588, 'cw': [{'sc': 0, 'w': '主要'}]}, {'bg': 1612, 'cw': [{'sc': 0, 'w': '的'}]}, {'bg': 1628, 'cw': [{'sc': 0, 'w': '是'}]}, {'bg': 1640, 'cw': [{'w': '电机', 'sc': 0}]}, {'bg': 1672, 'cw': [{'w': '，', 'sc': 0}]}, {'bg': 1672, 'cw': [{'sc': 0, 'w': '你'}]}, {'bg': 1684, 'cw': [{'sc': 0, 'w': '说是'}]}, {'cw': [{'sc': 0, 'w': '吧'}], 'bg': 1716}, {'bg': 1748, 'cw': [{'w': '，', 'sc': 0}]}, {'bg': 1748, 'cw': [{'w': '一个', 'sc': 0}]}, {'bg': 1780, 'cw': [{'sc': 0, 'w': '点击'}]}, {'bg': 1816, 'cw': [{'sc': 0, 'w': '1000'}]}, {'bg': 1844, 'cw': [{'sc': 0, 'w': '多'}]}, {'bg': 1880, 'cw': [{'sc': 0, 'w': '，'}]}, {'bg': 1880, 'cw': [{'sc': 0, 'w': '现在'}]}, {'bg': 1916, 'cw': [{'sc': 0, 'w': '国家'}]}]}, 'status': 1}}, {'code': 0, 'message': 'success', 'sid': 'iat00070033@dx170ecbd2db8a493802', 'data': {'result': {'sn': 5, 'ls': False, 'bg': 0, 'ed': 0, 'vad': {'ws': [{'bg': 1983, 'ed': 2302, 'eg': 63.6}]}, 'ws': [{'bg': 2000, 'cw': [{'sc': 0, 'w': '规定'}]}, {'bg': 2044, 'cw': [{'sc': 0, 'w': '电机'}]}, {'cw': [{'sc': 0, 'w': '保修'}], 'bg': 2072}, {'bg': 2108, 'cw': [{'sc': 0, 'w': '13年'}]}, {'bg': 2164, 'cw': [{'sc': 0, 'w': '，'}]}, {'bg': 2164, 'cw': [{'sc': 0, 'w': '我'}]}, {'bg': 2184, 'cw': [{'sc': 0, 'w': '给'}]}, {'bg': 2204, 'cw': [{'w': '你', 'sc': 0}]}, {'bg': 2220, 'cw': [{'sc': 0, 'w': '赠'}]}, {'bg': 2240, 'cw': [{'sc': 0, 'w': '一个'}]}]}, 'status': 1}}, {'code': 0, 'message': 'success', 'sid': 'iat00070033@dx170ecbd2db8a493802', 'data': {'result': {'sn': 6, 'ls': False, 'bg': 0, 'ed': 0, 'vad': {'ws': [{'eg': 58.57, 'bg': 2313, 'ed': 2393}]}, 'ws': [{'bg': 2326, 'cw': [{'sc': 0, 'w': '金卡'}]}]}, 'status': 1}}, {'code': 0, 'message': 'success', 'sid': 'iat00070033@dx170ecbd2db8a493802', 'data': {'result': {'ws': [{'bg': 2427, 'cw': [{'sc': 0, 'w': '，'}]}, {'cw': [{'sc': 0, 'w': '这个'}], 'bg': 2427}, {'bg': 2495, 'cw': [{'sc': 0, 'w': '可以'}]}, {'cw': [{'sc': 0, 'w': '链接'}], 'bg': 2527}, {'bg': 2551, 'cw': [{'sc': 0, 'w': '12'}]}, {'bg': 2603, 'cw': [{'sc': 0, 'w': '年'}]}, {'bg': 2651, 'cw': [{'sc': 0, 'w': '保修'}]}, {'bg': 2683, 'cw': [{'sc': 0, 'w': '金卡'}]}, {'bg': 2727, 'cw': [{'sc': 0, 'w': '，'}]}, {'bg': 2727, 'cw': [{'sc': 0, 'w': '说'}]}, {'bg': 2747, 'cw': [{'sc': 0, 'w': '句'}]}, {'bg': 2759, 'cw': [{'sc': 0, 'w': '不'}]}, {'bg': 2771, 'cw': [{'sc': 0, 'w': '好听'}]}, {'bg': 2799, 'cw': [{'sc': 0, 'w': '的'}]}, {'bg': 2819, 'cw': [{'sc': 0, 'w': '，'}]}, {'bg': 2819, 'cw': [{'sc': 0, 'w': '万一'}]}, {'cw': [{'sc': 0, 'w': '坏'}], 'bg': 2847}, {'bg': 2867, 'cw': [{'sc': 0, 'w': '了'}]}], 'sn': 7, 'ls': False, 'bg': 0, 'ed': 0, 'vad': {'ws': [{'ed': 2909, 'eg': 62.86, 'bg': 2414}]}}, 'status': 1}}, {'code': 0, 'message': 'success', 'sid': 'iat00070033@dx170ecbd2db8a493802', 'data': {'result': {'ls': False, 'bg': 0, 'ed': 0, 'vad': {'ws': [{'bg': 2943, 'ed': 3175, 'eg': 63.37}]}, 'ws': [{'bg': 2960, 'cw': [{'w': '，', 'sc': 0}]}, {'bg': 2960, 'cw': [{'sc': 0, 'w': '有'}]}, {'bg': 2988, 'cw': [{'w': '这个', 'sc': 0}]}, {'bg': 3024, 'cw': [{'sc': 0, 'w': '能'}]}, {'bg': 3040, 'cw': [{'w': '给', 'sc': 0}]}, {'bg': 3056, 'cw': [{'sc': 0, 'w': '您'}]}, {'bg': 3068, 'cw': [{'sc': 0, 'w': '省'}]}, {'bg': 3092, 'cw': [{'sc': 0, 'w': '1000'}]}, {'bg': 3124, 'cw': [{'sc': 0, 'w': '多'}]}, {'cw': [{'sc': 0, 'w': '呢'}], 'bg': 3136}], 'sn': 8}, 'status': 1}}, {'code': 0, 'message': 'success', 'sid': 'iat00070033@dx170ecbd2db8a493802', 'data': {'result': {'vad': {'ws': [{'bg': 3195, 'ed': 3363, 'eg': 63.35}]}, 'ws': [{'cw': [{'sc': 0, 'w': '，'}], 'bg': 3208}, {'bg': 3208, 'cw': [{'sc': 0, 'w': '这个'}]}, {'bg': 3240, 'cw': [{'sc': 0, 'w': '是不是'}]}, {'bg': 3276, 'cw': [{'sc': 0, 'w': '更'}]}, {'bg': 3292, 'cw': [{'sc': 0, 'w': '有用'}]}, {'bg': 3324, 'cw': [{'sc': 0, 'w': '呢'}]}], 'sn': 9, 'ls': False, 'bg': 0, 'ed': 0}, 'status': 1}}, {'code': 0, 'message': 'success', 'sid': 'iat00070033@dx170ecbd2db8a493802', 'data': {'result': {'sn': 10, 'ls': True, 'bg': 0, 'ed': 0, 'ws': [{'bg': 0, 'cw': [{'sc': 0, 'w': '？'}]}]}, 'status': 2}}]\n");
         System.out.println(val);
    }

}
