package com.sz1358.transpixel;


public class Tuple {
    public String word;
    public String uri;
    public String langString;

    public Tuple(String word, String uri, String langString) {
        this.word = word;
        this.uri = uri;
        this.langString = langString;
    }
    public String getWord() {
        return word;
    }

    public void setWord(String temp) {
        word = temp;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String temp) {
        uri = temp;
    }
}
