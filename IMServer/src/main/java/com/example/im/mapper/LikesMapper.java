package com.example.im.mapper;

import com.example.im.model.Likes;

public interface LikesMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table likes
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer likeId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table likes
     *
     * @mbggenerated
     */
    int insert(Likes record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table likes
     *
     * @mbggenerated
     */
    int insertSelective(Likes record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table likes
     *
     * @mbggenerated
     */
    Likes selectByPrimaryKey(Integer likeId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table likes
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(Likes record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table likes
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Likes record);
}