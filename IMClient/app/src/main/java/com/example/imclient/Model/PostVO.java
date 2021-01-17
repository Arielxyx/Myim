package com.example.imclient.Model;

public class PostVO {

    Post post;
    String post_name;
    String likes_name;
    String postUserHead;
    boolean isLiked;

    public PostVO(){};

    public PostVO(Post post, String post_name) {
        this.post = post;
        this.post_name = post_name;
    }

    public PostVO(Post post, String post_name, String likes_name,boolean isLiked) {
        this.post = post;
        this.post_name = post_name;
        this.likes_name = likes_name;
        this.isLiked = isLiked;
    }

    public PostVO(Post post, String post_name, String likes_name, String postUserHead, boolean isLiked) {
        this.post = post;
        this.post_name = post_name;
        this.likes_name = likes_name;
        this.postUserHead = postUserHead;
        this.isLiked = isLiked;
    }

    public String getPostUserHead() {
        return postUserHead;
    }

    public void setPostUserHead(String postUserHead) {
        this.postUserHead = postUserHead;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getLikes_name() {
        return likes_name;
    }

    public void setLikes_name(String likes_name) {
        this.likes_name = likes_name;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getPost_name() {
        return post_name;
    }

    public void setPost_name(String post_name) {
        this.post_name = post_name;
    }


}