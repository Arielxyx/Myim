package com.example.im;
import com.example.im.common.Constant;
import com.example.im.model.User;
import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ClientManager {
    private static Socket socket;
    private static ServerSocket serverSocket;
    public static List<String> nameList = new ArrayList<>(); //存储上线用户
    public static LinkedList<MessageItem> messageList = new LinkedList<>(); //存储向客户端发送消息
    public static Map<Socket, User> clientList = new HashMap<>(); //每个socket都对应一个user

    static Object lock = new Object();

    public ClientManager() {
        try {
            serverSocket = new ServerSocket(1234);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //接收线程
        new AcceptThread().start();
        //发送线程
        new SendThread().start();
        System.out.println("服务器已启动...");
    }

    public static class AcceptThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (isAlive()) {
                try {
                    socket = serverSocket.accept();// 使服务端处于监听状态，一直等待客户端的连接
                    //System.out.println("服务器已连接......");
                    clientList.put(socket,new User()); //将每个连接的客户端都存进集合中
                    new ReceiveThread(socket).start();// 为每一个客户端开设一个单个线程
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //接收客户端发来的信息
    public static class ReceiveThread extends Thread {
        Socket socket;
        User user;
        BufferedReader br;
        //在构造函数中接受用户的上线消息；
        public ReceiveThread(Socket socket) {
            super();
            this.socket = socket;
            try {
                br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));// 将客户端对象封装一下
                String result = br.readLine();
                user =  new Gson().fromJson(result, User.class);
                online(socket,user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //实时接收聊天记录、刷新在线用户列表
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            String receiveMessage = null;
            // 获取客户端传递过来的数据
            while (true) {
                try {
                    //有用户断开连接则结束该子线程
                    if (isServerClose(socket)) { //通过给客户端发送1个字节的紧急数据，来判断用户是否退出登录；
                       offline(socket,user);
                        break;
                    }
                    //接收到客户端的聊天消息
                    if ((receiveMessage = br.readLine()) != null) { //还有接收客户端之间的聊天记录
                        MessageItem messageItem = new Gson().fromJson(receiveMessage, MessageItem.class);
                        addMessage(messageItem);
                        //System.out.println("收到客户端的聊天消息：" + new Gson().toJson(messageItem));
                    }
                    sleep(250);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    //判断某个客户端是否退出登录
    private static Boolean isServerClose(Socket socket) {
        try {
            socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        } catch (Exception se) {
            return true;
        }
    }

    //接收到消息后通过addMessage函数加入消息列表中，从而唤醒服务器端的发送线程。
    public static void addMessage(MessageItem messageItem){
        synchronized (lock){
            messageList.add(messageItem);
            lock.notify();
        }
    }

    public static void online(Socket socket,User user){
        //将上线的用户名存入nameList中
        nameList.add(user.getName());
        System.out.println(user.getName()+"：已上线"); //+new Gson().toJson(nameList)
        //给当前socket的value赋值
        clientList.put(socket, user);
        //有人上线，新的用户名拼接加入messageList中
        String str = "";
        for (String s : nameList) {
            str = str + s + " ";
        }
        MessageItem messageItem = new MessageItem(str, "", user.getName(), "",-1,-1, Constant.ONLINE);
        addMessage(messageItem);
    }

    public static void offline(Socket socket,User user){
        nameList.remove(user.getName());
        clientList.remove(socket);
        //有人下线，新的用户名拼接加入messageList中
        String str = "";
        for (String s : nameList) {
            str = str + s + " ";
        }
        MessageItem messageItem = new MessageItem(str, "", user.getName(), "",-1,-1, Constant.OFFLINE);
        addMessage(messageItem);
        System.out.println(user.getName() + "：已下线");
    }

    //向客户端发送信息 根据消息messageItem的接收方是谁及其消息状态来决定转发给哪些客户端。
    public static class SendThread extends Thread {
        PrintWriter pw;
        @Override
        public void run() {
            while (true) {
                try {
                    synchronized (lock){
                        if(messageList.isEmpty()){
                            lock.wait();
                        }
                        MessageItem messageItem = messageList.removeFirst();
                        String sendMessage = new Gson().toJson(messageItem);
                        //System.out.println("发送给客户端的消息：" + sendMessage);
                        String status = messageItem.getStatus();

                        //如果是有刷新列表类型或者群聊，都需要将消息转发给所有客户端，如果消息状态是和自己聊就只转发给自己，如果状态是和别人聊就转发给对方和自己。
                        if (status.equals(Constant.ONLINEUPDARE)||status.equals(Constant.ONLINE) || status.equals(Constant.OFFLINE) || status.equals(Constant.GROUPCHAT)||status.equals(Constant.HEADUPDARE)) { //刷新列表或群聊，消息要转发给所有客户端
                            if(status.equals(Constant.GROUPCHAT))
                                System.out.println("群聊消息：" + sendMessage);
                            for (Socket client : clientList.keySet()) {
                                pw = new PrintWriter(client.getOutputStream());
                                pw.println(sendMessage);
                                pw.flush();
                            }
                        } else if (messageItem.getStatus().equals(Constant.OTHERS)) {
                            System.out.println("私聊（和别人聊）："+sendMessage);
                            for (Socket client : clientList.keySet()) {
                                if (clientList.get(client).getId()==messageItem.getSendId() || clientList.get(client).getId()==messageItem.getRevId()) { //发给自己和对方
                                    pw = new PrintWriter(client.getOutputStream());
                                    pw.println(sendMessage);
                                    pw.flush();
                                }
                            }
                        }else{
                            System.out.println("私聊（和自己聊）"+sendMessage);
                            for (Socket client : clientList.keySet()) {
                                if (clientList.get(client).getId()==messageItem.getSendId()) { //发给自己
                                    pw = new PrintWriter(client.getOutputStream());
                                    pw.println(sendMessage);
                                    pw.flush();
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


//    private static Map<String,Socket> clientList = new HashMap<>();
//    private static ServerThread serverThread = null;
//
//    private static class ServerThread implements Runnable {
//
//        private int port = 10010;
//        private boolean isExit = false;
//        private ServerSocket server;
//
//        public ServerThread() {
//            try {
//                server = new ServerSocket(port);
//                System.out.println("启动服务成功" + "port:" + port);
//            } catch (IOException e) {
//                System.out.println("启动server失败，错误原因：" + e.getMessage());
//            }
//        }
//
//        @Override
//        public void run() {
//            try {
//                while (!isExit) {
//                    // 进入等待环节
//                    System.out.println("等待手机的连接... ... ");
//                    final Socket socket = server.accept();
//                    // 获取手机连接的地址及端口号
//                    final String address = socket.getRemoteSocketAddress().toString();
//                    System.out.println("连接成功，连接的手机为：" + address);
//
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                // 单线程索锁
//                                synchronized (this){
//                                    // 放进到Map中保存
//                                    clientList.put(address,socket);
//                                }
//                                // 定义输入流
//                                InputStream inputStream = socket.getInputStream();
//                                byte[] buffer = new byte[1024];
//                                int len;
//                                while ((len = inputStream.read(buffer)) != -1){
//                                    String text = new String(buffer,0,len);
//                                    System.out.println("收到的数据为：" + text);
//                                    // 在这里群发消息
//                                    sendMsgAll(text);
//                                }
//
//                            }catch (Exception e){
//                                System.out.println("错误信息为：" + e.getMessage());
//                            }finally {
//                                synchronized (this){
//                                    System.out.println("关闭链接：" + address);
//                                    clientList.remove(address);
//                                }
//                            }
//                        }
//                    }).start();
//
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void Stop(){
//            isExit = true;
//            if (server != null){
//                try {
//                    server.close();
//                    System.out.println("已关闭server");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public static ServerThread startServer(){
//        System.out.println("开启服务");
//        if (serverThread != null){
//            showDown();
//        }
//        serverThread = new ServerThread();
//        new Thread(serverThread).start();
//        System.out.println("开启服务成功");
//        return serverThread;
//    }
//
//    // 关闭所有server socket 和 清空Map
//    public static void showDown(){
//        for (Socket socket : clientList.values()) {
//            try {
//                socket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        serverThread.Stop();
//        clientList.clear();
//    }
//
//    // 群发的方法
//    public static boolean sendMsgAll(String msg){
//        try {
//            for (Socket socket : clientList.values()) {
//                OutputStream outputStream = socket.getOutputStream();
//                outputStream.write(msg.getBytes("utf-8"));
//            }
//            return true;
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return false;
//    }
}