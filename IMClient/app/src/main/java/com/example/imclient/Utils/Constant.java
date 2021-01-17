package com.example.imclient.Utils;

public class Constant {
    private final static String BASE_URL = "http://192.168.43.221:8080/"; //192.168.1.102 "http://192.168.43.221:8080/usercontroller/"
    private final static String USERCONTROLLER = "usercontroller/";
    private final static String POSTCONTROLLER = "postcontroller/";

    public final static String LOGIN = BASE_URL+USERCONTROLLER+"login";
    public final static String UPDATEPSD = BASE_URL+USERCONTROLLER+"updatePsd";
    public final static String UPDATEINFO = BASE_URL+USERCONTROLLER+"updateInfo";
    public final static String UPDATEHEAD = BASE_URL+USERCONTROLLER+"updateHead";
    public final static String REGISTER = BASE_URL+USERCONTROLLER+"register";
    public final static String SELECTTARGETID = BASE_URL+USERCONTROLLER+"selectTargetId";
    public final static String GETUSERLIST = BASE_URL+USERCONTROLLER+"getUserList";
    public final static String GETUSERLISTBYNAMELIST = BASE_URL+USERCONTROLLER+"getUserListByNameList";
    public final static String INSERTCHAT = BASE_URL+USERCONTROLLER+"insertChat";
    public final static String GETCHATLIST = BASE_URL+USERCONTROLLER+"getChatList";

    public final static String PUBLISH = BASE_URL+POSTCONTROLLER+"publish";
    public final static String GETPOSTLIST = BASE_URL+POSTCONTROLLER+"getPostList";
    public final static String LIKEPOST = BASE_URL+POSTCONTROLLER+"likePost";
    public final static String DONTLIKEPOST = BASE_URL+POSTCONTROLLER+"dontLikePost";
    public final static String GETLIKESLISTBYPOSTID = BASE_URL+POSTCONTROLLER+"getLikesListByPostId";
    public final static String DELETEPOST = BASE_URL+POSTCONTROLLER+"deletePost";
    public final static String UPDATEPOST = BASE_URL+POSTCONTROLLER+"updatePost";

    public static final String ONLINE = "online";
    public static final String OFFLINE = "offline";
    public static final String GROUPCHAT = "聊天室";
    public static final String MYSELF = "myself";
    public static final String OTHERS = "others";
    public static final String ONLINEUPDARE = "onlineUpdate";
    public static final String HEADUPDARE = "headUpdate";

    public static final String LOGINERROR = "账号或密码错误";
    public static final String LOGINALREADY = "已有用户登录";

    public static final String NAMEREGISTERED = "用户名已被注册";
    public static final String PHONEREGISTERED = "手机号已存在";

    public static final String INFOLACK = "信息填写不完整";
    public static final String FAILED = "failed";
    public static final String SUCCESS = "success";

    public final static String GETALL = BASE_URL+"getAll";
    public final static String DELETE = BASE_URL+"delete";
    public final static String MODIFY = BASE_URL+"modify";
}
