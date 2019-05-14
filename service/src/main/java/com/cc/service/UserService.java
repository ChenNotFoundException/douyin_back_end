package com.cc.service;

import com.imooc.pojo.Users;

/**
 * @author chenchen
 * @date 2019/5/7 11:03
 * @Content:
 */
public interface UserService {
    boolean queryUserNameIsExist(String username);

    void saveUser(Users user);

    Users checkUser(String username,String password);

    void updateUserInfo(Users users);

    Users queryUserInfo(String userId);

    boolean isUserLikeVideo(String userId, String videoId);

    void saveUserFanRelation(String userId, String fanId);

    void deleteUserFanRelation(String userId, String fanId);
}
