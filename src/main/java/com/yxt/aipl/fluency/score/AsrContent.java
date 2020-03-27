package com.yxt.aipl.fluency.score;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class AsrContent {
    private List<AsrWord>wordList;
    private List<List<AsrWord>>sentenceList;
    private final String punctuation = "，？！。,.?";
    private List<Float> speedList;
    private List<Float> sentenceIntervalList;

    public List<Float> getSpeedList() {
        return speedList;
    }

    public List<Float> getSentenceIntervalList() {
        return sentenceIntervalList;
    }

    public String getText() {
        return text;
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    private String text;
    private float averageSpeed;

    public AsrContent(List<AsrWord>wordList){
        this.wordList = wordList;
        this.text = "";
        this.averageSpeed = 0;
        this.sentenceList = new ArrayList<>();
        this.generateSentenceListAndText();
        this.speedList = new ArrayList<>();
        this.generateSpeed();
        this.sentenceIntervalList = new ArrayList<>();
        this.generateSentenceInterval();
    }


    private void generateSentenceListAndText(){
        List<AsrWord> sentence = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        for(AsrWord word: this.wordList){
            sb.append(word.getText());
            if(this.punctuation.contains(word.getText())){
                this.sentenceList.add(sentence);
                sentence = new ArrayList<>();
            }else{
                sentence.add(word);
            }
        }
        this.text = sb.toString();
    }

    private String getSentenceText(List<AsrWord> sentence){
        StringBuffer sb = new StringBuffer();
        for(AsrWord word : sentence){
            sb.append(word.getText());
        }
        return sb.toString();
    }


    private void  generateSpeed(){
        int totalLen = 0;
        int totalTime = 0;
        for(List<AsrWord> sentence : this.sentenceList){
            int sentenceSize = sentence.size();
            //如果一个句子只有一个词，无法计算语速，因为不知道它的结束时间，不能把下一个句子的开始时间作为这个句子的结束时间，因为在一句话说完后会有停顿
            if(sentenceSize <= 1){
                continue;
            }
            //因为每一个词只有开始时间，所以最后一个词的开始时间减去第一个词的开始时间作为这个句子的时长，并且在这个时长内句子的长度为整个句子长度减去最后一个词长度
            String sentenceText = this.getSentenceText(sentence.subList(0,sentenceSize-1));
            //时间单位为厘秒
            int speechTime = sentence.get(sentenceSize-1).getBegin() -  sentence.get(0).getBegin();
            //讯飞换语音部分有bug,每一个词的开始时间有时候存在为0的情况,目前主要是下载的音频经过实时语音转文本后偶尔有的音频会出现
            if(speechTime <= 0){
                continue;
            }
            totalLen += sentenceText.length();
            totalTime += speechTime;
            //语速计算(字/分钟)
            float speed = sentenceText.length() / (float)speechTime * 6000;
            System.out.println(sentenceText+":"+speechTime+":"+speed);
            this.speedList.add(speed);
        }
        this.averageSpeed = (float)totalLen / totalTime * 6000;
    }


    private void generateSentenceInterval(){
        int sentenceListSize = this.sentenceList.size();
        for(int i=1;i<sentenceListSize;i++){
            List<AsrWord> firstSentence = sentenceList.get(i-1);
            List<AsrWord> secondSentence = sentenceList.get(i);
            if(firstSentence.size() <= 0 || secondSentence.size() <= 0){
                continue;
            }
            //两个句子之间的时间差是通过第二个句子的第一个词开始时间减去第一个句子的最后一个词的开始时间来计算的,这样会把第一个句子的最后一个词的时间多算进去，但是一般时间很短，可以忽略
            int firstTime = firstSentence.get(firstSentence.size()-1).getBegin();
            int secondTime = secondSentence.get(0).getBegin();
            //讯飞换语音部分有bug,每一个词的开始时间有时候存在为0的情况,目前主要是下载的音频经过实时语音转文本后偶尔有的音频会出现
            if(firstTime == 0 || secondTime == 0){
                continue;
            }
            //单位(厘秒)
            int interval = secondTime - firstTime;
            this.sentenceIntervalList.add(Float.valueOf(interval));
            System.out.println(interval);
        }
    }



    public String toString(){
        return String.format("text-->%s\naverage-->%f\nsentenceSpeed-->[%s]\nsentenceInterval-->[%s]", this.text,this.averageSpeed,this.speedList.stream().map(String::valueOf)
                .collect(Collectors.joining(",")),this.sentenceIntervalList.stream().map(String::valueOf)
                .collect(Collectors.joining(",")));
    }




    public static void main(String[] args){


    }

}
;