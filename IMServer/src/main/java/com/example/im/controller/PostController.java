package com.example.im.controller;

import com.example.im.model.Likes;
import com.example.im.model.Post;
import com.example.im.model.PostVO;
import com.example.im.model.User;
import com.example.im.service.Impl.PostServiceImpl;
import com.example.im.service.Impl.UserServiceImpl;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.im.common.Constant.FAILED;
import static com.example.im.common.Constant.SUCCESS;

@RestController
@RequestMapping(path = "/postcontroller")//地址映射
public class PostController {

    private PostServiceImpl postServiceImpl;
    private UserServiceImpl userServiceImpl;

    public PostController(PostServiceImpl postServiceImpl,UserServiceImpl userServiceImpl) {
        this.postServiceImpl = postServiceImpl;
        this.userServiceImpl = userServiceImpl;
    }

    @RequestMapping("/publish")
    public String publish(Post post) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        post.setTime(dateFormat.format(date));
        System.out.println("publish user id："+post.getUser_id());
        System.out.println("publish content："+post.getContent());
        System.out.println("publish time："+post.getTime());
        int cnt = postServiceImpl.insertPost(post);
        if (cnt>0){
            return SUCCESS;
        }
        return FAILED;
    }

    @RequestMapping("/getPostList")
    public String getPostList() {
        List<Post> posts = postServiceImpl.getPostList();
        List<PostVO> postVOS = new ArrayList<>();
        for (Post post: posts) {
            User user = userServiceImpl.selectUserById(post.getUser_id());

            PostVO postVO = new PostVO(post,user.getName(),getLikesListByPostId(post.getPost_id()), user.getHead(),false);
            postVOS.add(postVO);
        }
        String message = new Gson().toJson(postVOS);
//        System.out.println("message：" + message);
        return message;
    }

    @RequestMapping("/likePost")
    public String likePost(Likes likes) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        likes.setLike_time(dateFormat.format(date));
        int cnt = postServiceImpl.insertLikes(likes);
        if(cnt >0)
            return SUCCESS;
        return FAILED;
    }

    @RequestMapping("/dontLikePost")
    public String dontLikePost(Likes likes) {
        int cnt = postServiceImpl.deleteLikes(likes);
        if(cnt >0)
            return SUCCESS;
        return FAILED;
    }

    @RequestMapping("/getLikesListByPostId")
    public String getLikesListByPostId(int post_id) {
        List<Likes> likesList = postServiceImpl.getLikesListByPostId(post_id);
        List<String> userNames = new ArrayList<>();
        for (Likes likes1 : likesList) {
            userNames.add(userServiceImpl.selectUserById(likes1.getUser_id()).getName());
        }
        return new Gson().toJson(userNames);
    }

    @RequestMapping("/deletePost")
    public String deletePost(int post_id) {
        int cnt = postServiceImpl.deletePost(post_id);
        String result = deleteLikesByPostId(post_id);
        if(cnt>0 && result.equals(SUCCESS))
            return SUCCESS;
        return FAILED;
    }

    public String deleteLikesByPostId(int post_id) {
        int cnt = postServiceImpl.deleteLikesByPostId(post_id);
        if(cnt >0)
            return SUCCESS;
        return FAILED;
    }

    @RequestMapping("/updatePost")
    public String updatePost(Post post) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        post.setTime(dateFormat.format(date));
        int cnt = postServiceImpl.updatePost(post);
        if (cnt>0){
            return SUCCESS;
        }
        return FAILED;
    }
}