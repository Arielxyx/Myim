package com.example.im.service.Impl;

import com.example.im.mapper.UserMapper;
import com.example.im.model.Chat;
import com.example.im.model.User;
import com.example.im.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/*  对于接口的实现  */
/* 自动把service注入到controller */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectByPhoneAndPwd(User user){
        try {
            User result = userMapper.selectByPhoneAndPwd(user);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int selectByName(User user){
        try {
            int cnt = userMapper.selectByName(user);
            return cnt;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int selectByPhone(User user){
        try {
            int cnt = userMapper.selectByPhone(user);
            return cnt;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public User selectTargetId(User user){
        try {
            User result = userMapper.selectTargetId(user);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int insertUser(User user) {
        try {
            int cnt = userMapper.insertUser(user);
            return cnt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int updatePsd(User user){
        try {
            int cnt = userMapper.updatePsd(user);
            return cnt;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int updateInfo(User user){
        try {
            int cnt = userMapper.updateInfo(user);
            return cnt;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int updateHead(User user){
        try {
            int cnt = userMapper.updateHead(user);
            return cnt;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public User selectUserById(int id){
        try {
            User result = userMapper.selectUserById(id);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User selectUserByName(String name){
        try {
            User user = userMapper.selectUserByName(name);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public List<User> getUserList() {
        try {
            List<User> users = userMapper.getUserList();
            return  users;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int insertChat(Chat chat) {
        try {
            int cnt = userMapper.insertChat(chat);
            return cnt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Chat> selectChat(int userId,int id){
        try {
            List<Chat> chatList= userMapper.selectChat(userId, id);
            return chatList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Chat> selectChatByRevId(int rev_id){
        try {
            List<Chat> chatList= userMapper.selectChatByRevId(rev_id);
            return chatList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User selectById(User user){
        try {
            User result = userMapper.selectById(user);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
