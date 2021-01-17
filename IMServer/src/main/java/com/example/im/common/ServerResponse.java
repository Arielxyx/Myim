package com.example.im.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/*这里是通用的数据端的响应对象   实现对象序列化  可以保存单个对象
用泛型（要封装的数据对象）来声明这个类
泛型的好处是：返回的时候可以指定/不指定泛型里面的内容
构造方法是私有的，外部不可以new
 */


/*这个注解是对于   返回的json数据含有null值进行过滤
  如没用data传入的时候（此时有null值，有key的空节点）会过滤掉msg/data
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证序列化json的时候，如果是null的对象，key也会消失
public class ServerResponse <T> implements Serializable {
    private int status;
    private String msg;
    private T data;

    /* 以下都是构造器 */
    private ServerResponse(int status){
        this.status = status;
    }
    private ServerResponse(int status,T data){
        this.status = status;
        this.data = data;
    }
    private ServerResponse(int status,String msg){
        this.status = status;
        this.msg = msg;
    }
    private ServerResponse(int status,String msg,T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    @JsonIgnore
    //使之不在json序列化结果当中
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }


    public int getStatus(){
        return status;
    }
    public T getData(){
        return data;
    }
    public String getMsg(){
        return msg;
    }

    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }
    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    public static <T> ServerResponse<T> createBySuccess(String msg,T data) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg,data);
    }

    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());//这个时候只有error对应的code，没有相应的错误描述
    }
    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);//这个时候的code就只是error对应的code
    }
    public static <T>ServerResponse<T> createByErrorCodeMessage(int errorCode,String errormessage){
        return new ServerResponse<T>(errorCode,errormessage);//这种情况需要强制登录，传进来的参数是ResponseCode.NEED_LOGIN.getCode()

    }
}
