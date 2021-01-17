package com.example.im.service;

import com.example.im.model.Likes;
import com.example.im.model.Post;

import java.util.List;

public interface IPostService {

    /* 这里返回的是一个泛型，因为返回的内容里面会有泛型data */
    //新增动态
    public int insertPost(Post post);

    //获取动态列表
    public List<Post> getPostList();

    //点赞动态
    public int insertLikes(Likes likes);

    //取消赞动态
    public int deleteLikes(Likes likes);

    //根据动态id获取点赞记录列表
    public List<Likes> getLikesListByPostId(int post_id);

    //删除动态
    public int deletePost(int post_id);

    //根据动态id删除相关的所有点赞记录
    public int deleteLikesByPostId(int post_id);

    //更新动态
    public int updatePost(Post post);
}
