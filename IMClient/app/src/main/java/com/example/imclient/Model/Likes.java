package com.example.imclient.Model;

public class Likes {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column like.like_id
     *
     * @mbggenerated
     */
    private Integer likes_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column like.user_id
     *
     * @mbggenerated
     */
    private Integer user_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column like.post_id
     *
     * @mbggenerated
     */
    private Integer post_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column like.like_time
     *
     * @mbggenerated
     */
    private String like_time;

    public Integer getLikes_id() {
        return likes_id;
    }

    public void setLikes_id(Integer likes_id) {
        this.likes_id = likes_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getPost_id() {
        return post_id;
    }

    public void setPost_id(Integer post_id) {
        this.post_id = post_id;
    }

    public String getLike_time() {
        return like_time;
    }

    public void setLike_time(String like_time) {
        this.like_time = like_time;
    }
}
