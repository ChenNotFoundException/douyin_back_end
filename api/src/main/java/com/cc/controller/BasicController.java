package com.cc.controller;

import com.imooc.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenchen
 * @date 2019/5/7 10:13
 * @Content:
 */
@RestController
public class BasicController {
    @Autowired
    public RedisOperator redis;

    public static final String USER_REDIS_SESSION = "user-redis-session";
    //文件保存的命名空间
    public static final String FILE_SPACE = "E:\\imooc";

    public static final String ffmpegEXE = "D:\\ffmpeg\\bin\\ffmpeg.exe";
    public static final  Integer PAGE_SIZE = 5;


}
