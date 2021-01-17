package com.example.imclient.Model;

public class News {
    String title; //新闻标题
    String href; //新闻链接
    String time; //时间
    String content;//内容
    String photoUrl;//图片地址链接


    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public News(String title, String content) {
        this.title=title;
        this.content=content;
    }
    public News(String title, String href, String time) {
        this.title = title;
        this.href = href;
        this.time=time;
    }
    public News(String title, String href, String time, String photoUrl) {
        this.title = title;
        this.href = href;
        this.time = time;
        this.content = content;
        this.photoUrl = photoUrl;
    }
}
