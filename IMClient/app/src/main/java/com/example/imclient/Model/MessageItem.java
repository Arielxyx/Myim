package com.example.imclient.Model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class MessageItem extends DataSupport implements Serializable {
    private int id;
    private String content; //发送内容
    private String time; //发送时间
    private String sendName; //发送方姓名
    private String revName; //接收方姓名
    private int sendId;
    private int revId;
    private String status; //群聊或私聊

    public MessageItem(int id, String content, String time, String sendName, String revName, int sendId, int revId, String status) {
        this.id = id;
        this.content = content;
        this.time = time;
        this.sendName = sendName;
        this.revName = revName;
        this.sendId = sendId;
        this.revId = revId;
        this.status = status;
    }

    public MessageItem(String content, String time, String sendName, String revName, int sendId, int revId, String status) {
        this.content = content;
        this.time = time;
        this.sendName = sendName;
        this.revName = revName;
        this.sendId = sendId;
        this.revId = revId;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSendId() {
        return sendId;
    }

    public void setSendId(int sendId) {
        this.sendId = sendId;
    }

    public int getRevId() {
        return revId;
    }

    public void setRevId(int revId) {
        this.revId = revId;
    }

    public MessageItem() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public String getRevName() {
        return revName;
    }

    public void setRevName(String revName) {
        this.revName = revName;
    }
}
