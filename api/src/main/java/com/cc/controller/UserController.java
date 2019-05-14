package com.cc.controller;

import com.cc.service.UserService;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.PublisherVO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.utils.IMoocJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author chenchen
 * @date 2019/5/7 10:13
 * @Content:
 */
@RestController
@Api(value = "用户相关业务接口", tags = "用户相关业务接口")
@RequestMapping("/user")
public class UserController extends BasicController {
    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户上传头像", notes = "用户上传头像")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String", paramType = "query")
    @PostMapping("/uploadFace")
    public IMoocJSONResult uploadFace(String userId,
                                      @RequestParam("file") MultipartFile[] files) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("用户id不能为空。。。");
        }
        //文件保存的命名空间
        String fileSpace = "E:/imooc";
        //相对路径
        String uploadPathDB = "/" + userId + "/face";

        FileOutputStream fo = null;
        InputStream is = null;
        try {
            if (files != null && files.length > 0) {

                String filename = files[0].getOriginalFilename();
                if (StringUtils.isNoneBlank(filename)) {
                    //文件上传最终保存路径
                    String finalPath = fileSpace + uploadPathDB + "/" + filename;
                    //System.out.println(finalPath);
                    //数据库保存的路径
                    uploadPathDB += ("/" + filename);
                    //System.out.println(uploadPathDB);
                    File outFile = new File(finalPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fo = new FileOutputStream(outFile);
                    is = files[0].getInputStream();
                    IOUtils.copy(is, fo);
                }
            } else {
                return IMoocJSONResult.errorMsg("上传出错...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fo != null) {
                fo.flush();
                fo.close();
            }
        }
        Users users = new Users();
        users.setId(userId);
        users.setFaceImage(uploadPathDB);
        userService.updateUserInfo(users);


        return IMoocJSONResult.ok(uploadPathDB);

    }




    @ApiOperation(value = "用户信息查询", notes = "用户信息查询")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String", paramType = "query")
    @PostMapping("/query")
    public IMoocJSONResult query(String userId) throws Exception {
        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("用户ID不能为空。。");
        }
        Users userInfo = userService.queryUserInfo(userId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userInfo, usersVO);

        return IMoocJSONResult.ok(usersVO);

    }

    @PostMapping("/queryPublisher")
    public IMoocJSONResult queryPublisher(String LoginUserId, String VideoId, String PublisherUserId) throws Exception {
        if (StringUtils.isBlank(PublisherUserId)) {
            return IMoocJSONResult.errorMsg(" ");
        }
        Users userInfo = userService.queryUserInfo(PublisherUserId);
        UsersVO publisher = new UsersVO();
        BeanUtils.copyProperties(userInfo, publisher);

        //查询点赞关联关系
        boolean userLikeVideo = userService.isUserLikeVideo(LoginUserId, VideoId);
        PublisherVO bean = new PublisherVO();
        bean.setPubliser(publisher);
        bean.setUserLikeVideo(userLikeVideo);

        return IMoocJSONResult.ok(bean);

    }

    @PostMapping("/beyourfans")
    public IMoocJSONResult beyourfans(String userId, String fanId) throws Exception {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return IMoocJSONResult.errorMsg("");
        }

        userService.saveUserFanRelation(userId, fanId);
        return IMoocJSONResult.ok("关注成功");
        
    }

    @PostMapping("/dontbeyourfans")
    public IMoocJSONResult dontbeyourfans(String userId, String fanId) throws Exception {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return IMoocJSONResult.errorMsg("");
        }

        userService.deleteUserFanRelation(userId, fanId);
        return IMoocJSONResult.ok("取消关注");

    }
}
