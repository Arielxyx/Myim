package com.example.im.service;
import com.example.im.model.Chat;
import com.example.im.model.User;
import java.util.List;

public interface IUserService {

    //根据手机号和密码查询用户
    User selectByPhoneAndPwd(User user);

    //根据用户名查询所有用户
    int selectByName(User user);

    //根据手机号查询所有用户
    int selectByPhone(User user);

    //根据用户名查询用户
    User selectTargetId(User user);

    //添加新用户
    int insertUser(User user);

    //根据用户id修改密码
    int updatePsd(User user);

    //根据用户id修改基本信息
    int updateInfo(User user);

    //根据用户id修改用户头像
    int updateHead(User user);

    //根据用户id查询用户
    User selectUserById(int id);

    //根据用户名查询用户
    User selectUserByName(String name);

    //获取数据库中所有用户列表
    List<User> getUserList();

    //插入用户间的聊天记录
    int insertChat(Chat chat);

    //根据用户id和聊天对象的id返回聊天记录列表
    List<Chat> selectChat(int userId,int id);

    //返回聊天室的聊天记录列表
    List<Chat> selectChatByRevId(int rev_id);

    User selectById(User user);
}
