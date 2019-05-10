package com.cc.controller;

import com.cc.service.UserService;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author chenchen
 * @date 2019/5/7 10:13
 * @Content:
 */
@RestController
@Api(value = "用户注册、登录接口",tags = "注册和登录的controller")
public class RegistLoginController extends BasicController{
    @Autowired
    private UserService userService;
    /**
     * 注册
     *
     * @param user
     * @return
     */
    @ApiOperation(value = "用户注册",notes = "注册接口")
    @PostMapping("/regist")
    public IMoocJSONResult regist(@RequestBody Users user) throws Exception {
        System.out.println(user.getUsername());
        System.out.println(user.getPassword());
        //1.用户名密码不为空
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return IMoocJSONResult.errorMsg("用户名/密码为空！");
        }
        //2、是否存在
        boolean flag = userService.queryUserNameIsExist(user.getUsername());
        //3、保存
        if (!flag) {
            user.setNickname(user.getUsername());
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            user.setFansCounts(0);
            user.setReceiveLikeCounts(0);
            user.setFollowCounts(0);
            userService.saveUser(user);
            user.setPassword(null);
            UsersVO usersVO = setUserRedisSessionToken(user);
            return IMoocJSONResult.ok(usersVO);
        } else {
            return IMoocJSONResult.errorMsg("用户名已存在！！");
        }

    }

    @ApiOperation(value = "用户登录",notes = "登录接口")
    @PostMapping("/login")
    public IMoocJSONResult login(@RequestBody Users users) throws Exception {
        System.out.println(users.getUsername());
        System.out.println(users.getPassword());
//        Thread.sleep(2000);
        //1.用户名密码不为空
        if (StringUtils.isBlank(users.getUsername()) || StringUtils.isBlank(users.getPassword())) {
            return IMoocJSONResult.errorMsg("用户名/密码为空！");
        }
        //2、是否存在
        Users checkUser = userService.checkUser(users.getUsername(), MD5Utils.getMD5Str(users.getPassword()));

        if (checkUser != null) {
            checkUser.setPassword("");
            UsersVO usersVO = setUserRedisSessionToken(checkUser);
            return IMoocJSONResult.ok(usersVO);
        } else {
            return IMoocJSONResult.errorMsg("用户名/密码不正确，请重试...");
        }
    }

    @ApiOperation(value = "用户注销", notes = "注销接口")
    @ApiImplicitParam(name = "userId",value = "用户ID",required = true,dataType = "String",paramType = "query")
    @PostMapping("/logout")
    public IMoocJSONResult logout(String userId) throws Exception {
        redis.del(USER_REDIS_SESSION+":"+userId);
        return IMoocJSONResult.ok();

    }

    private UsersVO setUserRedisSessionToken(Users userModel) {
        String uniqueToken = UUID.randomUUID().toString();
        redis.set(USER_REDIS_SESSION + ":" + userModel.getId(), uniqueToken, 1000 * 60 * 30);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userModel, usersVO);
        usersVO.setUserToken(uniqueToken);
        return usersVO;
    }
}
