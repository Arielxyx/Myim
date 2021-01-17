package com.example.im.controller;

import com.example.im.ClientManager;
import com.example.im.MessageItem;
import com.example.im.common.Constant;
import com.example.im.model.Chat;
import com.example.im.model.User;
import com.example.im.service.Impl.UserServiceImpl;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.im.ClientManager.addMessage;

@RestController
@RequestMapping(path = "/usercontroller")//地址映射
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
        new ClientManager();
    }

    @PostMapping("/login")
    public String login(User user) {
        User result = userService.selectByPhoneAndPwd(user);
        if (result == null) { //密码或手机号错误，没有查询到
            return Constant.LOGINERROR;
        } else if (ClientManager.nameList.contains(result.getName())) { //已经有用户登录
            return Constant.LOGINALREADY;
        } else {
            return new Gson().toJson(result);//这个就是把user对象转成json字符串，参数是Object类型，就意味着我们可以吧任意object对象转为json字符串
            // 还有一个用法就是把json转为java对象的
//                new Gson().fromJson(String,User.class)  前面的string就是json字符串，后面的User.class就是把字符串转成User对象
        }
    }

    @PostMapping("/register")
    public String register(User user) {
        int namecnt = userService.selectByName(user);
        int phonecnt = userService.selectByPhone(user);
        if (namecnt > 0) {
            return Constant.NAMEREGISTERED;
        }
        if (phonecnt > 0) {
            return Constant.PHONEREGISTERED;
        }
        userService.insertUser(user);
        return Constant.SUCCESS;
    }

    @PostMapping("/updatePsd")
    public String updatePsd(User user) {
        if (user == null || user.getId() == null || user.getPassword() == null)
            return Constant.INFOLACK;
        else {
            int cnt = userService.updatePsd(user);
            if (cnt != -1) {
                return Constant.SUCCESS;
            } else {
                return Constant.FAILED;
            }
        }
    }

    @PostMapping("/updateInfo")
    public String updateInfo(User user) {
        if (user == null)
            return Constant.INFOLACK;
        else {
            //分割出原字符串中的原用户名和新用户名
            String name = user.getName();
            String length = name.split("-")[0];
            String oldName = name.substring(length.length() + 1, length.length() + 1 + Integer.parseInt(length));
            String newName = name.substring(length.length() + 2 + Integer.parseInt(length), name.length());
            user.setName(newName);
            System.out.println(oldName + "：改名为 "+ newName);
            //更新数据库
            int cnt = userService.updateInfo(user);
            if (cnt != -1) { //更新成功
                //Socket管理端转发消息通知其他用户有人改名字了
                MessageItem messageItem = new MessageItem(newName, "", oldName, "", -1, -1, Constant.ONLINEUPDARE);
                ClientManager.nameList.remove(oldName);
                ClientManager.nameList.add(newName);
                for (Socket s : ClientManager.clientList.keySet()) { //更新该客户端socket对应user的内容
                    if (ClientManager.clientList.get(s).getName().equals(messageItem.getSendName())) {
                        User u = ClientManager.clientList.get(s);
                        u.setName(messageItem.getContent());
                        ClientManager.clientList.put(s, u);
                    }
                }
                String str = "";
                for (String s : ClientManager.nameList) {
                    str = str + s + " ";
                }
                messageItem.setContent(str);
                addMessage(messageItem);
                return Constant.SUCCESS;
            } else {
                return Constant.FAILED;
            }
        }
    }

    @PostMapping("/updateHead")
    public String updateHead(User user) {
        int cnt = userService.updateHead(user);
        if (cnt > 0) { //更新成功

            //Socket管理端转发消息通知其他用户有人改名字了
            MessageItem messageItem = new MessageItem("", "", "", "", -1, -1, Constant.HEADUPDARE);
            addMessage(messageItem);
            System.out.println(user.getId()+" 头像更新成功");
            return Constant.SUCCESS;
        } else {
            return Constant.FAILED;
        }
    }

    @PostMapping("/selectTargetId")
    public String selectTargetId(User user) {
        User result = userService.selectTargetId(user);
        if (result == null)
            return Constant.FAILED;
        else {
            return result.getId() + "";
        }
    }

    @PostMapping("/getUserListByNameList")
    public List<User> getUserListByNameList(String nameListString) {
        String names[] = nameListString.split(" ");
        List<User> userList = new ArrayList<>();
        System.out.println("getUserListByNameList接收到的名字: " + new Gson().toJson(Arrays.asList(names)));
        for (String name : names) {
            System.out.println(name);
            if(!name.equals("聊天室")){
                User user = userService.selectUserByName(name);
                userList.add(user);
            }
        }
        String message = new Gson().toJson(userList);
        System.out.println("getUserListByNameList 返回的用户名：" + message);
        return userList;
    }


    @PostMapping("/insertChat")
    public String insertChat(Chat chat) {
        System.out.println(new Gson().toJson(chat));
        int cnt = userService.insertChat(chat);
        if (cnt > 0) { //插入成功
            return Constant.SUCCESS;
        } else {
            return Constant.FAILED;
        }
    }

    @PostMapping("/getChatList")
    public List<Chat> getChatList(int userId,String userNameList) {
        String names[] = userNameList.split(" ");
        List<Chat> chatList = new ArrayList<>();

        System.out.println("getChatList 接收到的名字：" + new Gson().toJson(Arrays.asList(names)));
        for (String name : names) {
            List<Chat> chats = new ArrayList<>();
            if(!name.equals("聊天室")){
                User user = userService.selectUserByName(name);
                chats = userService.selectChat(userId, user.getId());
            }else{
                chats = userService.selectChatByRevId(-1);
            }
            if(chats!=null&&chats.size()>0)
                chatList.add(chats.get(0));
            else
                chatList.add(new Chat());
        }
        String message = new Gson().toJson(chatList);
        System.out.println("getChatList 返回最近的聊天记录：" + message);
        return chatList;
    }


    @RequestMapping("/getUserList")
    public List<User> getUserList() {
        return userService.getUserList();
    }

    @RequestMapping("/")
    public String HelloWorld() {
        return "HelloWorld";
    }
}