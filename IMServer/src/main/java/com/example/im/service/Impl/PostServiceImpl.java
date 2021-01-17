package com.example.im.service.Impl;

import com.example.im.mapper.PostMapper;
import com.example.im.model.Likes;
import com.example.im.model.Post;
import com.example.im.model.User;
import com.example.im.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/*  对于接口的实现  */
/* 自动把service注入到controller */
@Service("iPostService")
public class PostServiceImpl implements IPostService {

    @Autowired
    private PostMapper postMapper;

    @Override
    public int insertPost(Post post) {
        try {
            int cnt = postMapper.insertPost(post);
            return cnt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Post> getPostList(){
        try {
            List<Post> posts = postMapper.getPostList();
            return  posts;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int insertLikes(Likes likes){
        try {
            int cnt = postMapper.insertLikes(likes);
            if(cnt>0)
                return 1;
            else
                return 0;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int deleteLikes(Likes likes){
        try {
            int cnt = postMapper.deleteLikes(likes);
            if(cnt>0)
                return 1;
            else
                return 0;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public List<Likes> getLikesListByPostId(int post_id){
        try {
            List<Likes> likes = postMapper.getLikesListByPostId(post_id);
            return  likes;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int deletePost(int post_id){
        try {
            int cnt = postMapper.deletePost(post_id);
            if(cnt>0)
                return 1;
            else
                return 0;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int deleteLikesByPostId(int post_id){
        try {
            int cnt = postMapper.deleteLikesByPostId(post_id);
            if(cnt>0)
                return 1;
            else
                return 0;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int updatePost(Post post){
        try {
            int cnt = postMapper.updatePost(post);
            return cnt;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
