package com.example.im.mapper;

import com.example.im.model.Chat;
import com.example.im.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserMapper {
    @Select("select * from user order by id desc")
    List<User> getUserList();

    @Insert("insert into user(phone,name,password,sex,age) values(#{phone},#{name},#{password},#{sex},#{age})")
    int insertUser(User user);

    @Select("select * from user where phone= #{phone} and password= #{password}")
    User selectByPhoneAndPwd(User user);

    @Update("update user set password= #{password} where id= #{id}")
    int updatePsd(User user);

    @Update("update user set phone= #{phone},name= #{name},age= #{age},sex= #{sex} where id= #{id}")
    int updateInfo(User user);

    @Update("update user set head= #{head} where id = #{id}")
    int updateHead(User user);

    @Select("select * from user where id= #{id}")
    User selectById(User user);

    @Select("select count(*) from user where name= #{name}")
    int selectByName(User user);

    @Select("select count(*) from user where phone= #{phone}")
    int selectByPhone(User user);

    @Select("select * from user where name= #{name}")
    User selectTargetId(User user);

    @Select("select * from user where id= #{id}")
    User selectUserById(int id);

    @Select("select * from user where name= #{name}")
    User selectUserByName(String name);

    @Insert("insert into chat(send_id,rev_id,content,time) values(#{send_id},#{rev_id},#{content},#{time})")
    int insertChat(Chat chat);

    @Select("select * from chat where send_id = #{userId} and rev_id = #{id} or send_id = #{id} and rev_id = #{userId} order by time desc")
    List<Chat> selectChat(@Param("userId") int userId, @Param("id")int id);

    @Select("select * from chat where rev_id = #{rev_id} order by time desc")
    List<Chat> selectChatByRevId(int rev_id);

    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated
     */
    int insert(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated
     */
    int insertSelective(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated
     */
    User selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(User record);
}