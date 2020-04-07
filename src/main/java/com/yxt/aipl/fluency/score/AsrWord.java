package com.yxt.aipl.fluency.score;

public class AsrWord {
    private int begin;
    private String text;

    public int getBegin() {
        return begin;
    }

    public String getText() {
        return text;
    }

    public AsrWord(int begin, String text) {
        this.begin = begin;
        this.text = text;
    }


    public String toString() {
        return "{\"begin:\"" + this.begin + ",\"text\":" + this.text + "}";
    }

    public static void main(String[] args) {
        AsrWord word = new AsrWord(11, "111");
        System.out.println(word);
    }

}
