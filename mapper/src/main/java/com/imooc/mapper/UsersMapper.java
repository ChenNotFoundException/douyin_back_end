package com.imooc.mapper;

import com.imooc.pojo.Users;
import com.imooc.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {
    /**
     * 获得点赞
     *
     * @param userId
     */
    void addReceiveLikeCount(String userId);

    /**
     * 取消点赞
     *
     * @param userId
     */
    void reduceReceiveLikeCount(String userId);

    /**
     * 增加粉丝数
     *
     * @param userId
     */
    void addFans(String userId);

    /**
     * 减少粉丝数
     *
     * @param userId
     */
    void reduceFans(String userId);

    void addFollers(String userId);

    void reduceFollers(String userId);
}