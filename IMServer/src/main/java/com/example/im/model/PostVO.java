package com.example.im.model;

public class PostVO {

    Post post;
    String post_name;
    String likes_name;
    String postUserHead;
    boolean isLiked;

    public PostVO(Post post, String post_name) {
        this.post = post;
        this.post_name = post_name;
    }

    public PostVO(Post post, String post_name, String likes_name) {
        this.post = post;
        this.post_name = post_name;
        this.likes_name = likes_name;
    }

    public PostVO(Post post, String post_name, String likes_name,String postUserHead, boolean isLiked) {
        this.post = post;
        this.post_name = post_name;
        this.likes_name = likes_name;
        this.isLiked = isLiked;
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