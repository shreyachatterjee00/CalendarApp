package com.example.appredo;

public class Events {
    private String title;
    private Long time;

    Events(String titleParam, Long timeParam) {
        title = titleParam;
        time = timeParam;
    }

    public Long getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }
}
