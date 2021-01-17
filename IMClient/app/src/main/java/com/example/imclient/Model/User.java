package com.example.imclient.Model;

public class User {
    private Integer id;
    private String phone;
    private String name;
    private String password;
    private String sex;
    private Integer age;
    private String head;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public User(Integer id, String phone, String name, String password, String sex, Integer age) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.password = password;
        this.sex = sex;
        this.age = age;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }


    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }


    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }


    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }
}