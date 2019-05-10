package com.cc.controller;

import com.cc.service.BgmService;
import com.cc.service.VideoService;
import com.imooc.enums.VideoStatusEnum;
import com.imooc.pojo.Bgm;
import com.imooc.pojo.Videos;
import com.imooc.utils.FetchVideoCover;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MergeVideoMp3;
import com.imooc.utils.PagedResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * @author chenchen
 * @date 2019/5/7 10:13
 * @Content:
 */
@RestController
@Api(value = "视频相关业务", tags = "视频设置")
@RequestMapping("/video")
public class VideoController extends BasicController {

    @Autowired
    private BgmService bgmService;

    @Autowired
    private VideoService videoService;

    @ApiOperation(value = "上传视频", notes = "上传视频接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "bgmId", value = "背景音乐ID", required = false, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds", value = "视频时长", required = true, dataType = "double", paramType = "form"),
            @ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true, dataType = "int", paramType = "form"),
            @ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true, dataType = "int", paramType = "form"),
            @ApiImplicitParam(name = "desc", value = "视频描述", required = false, dataType = "String", paramType = "form"),
    })
    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    public IMoocJSONResult uploadFace(String userId, String bgmId,
                                      double videoSeconds, int videoWidth, int videoHeight,
                                      String desc,
                                      @ApiParam(value = "短视频文件", required = true)
                                              MultipartFile file) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("用户id不能为空。。。");
        }

        //相对路径
        String uploadPathDB = "/" + userId + "/video";
        String coverPathDB = "/" + userId + "/video";
        FileOutputStream fo = null;
        InputStream is = null;
        String finalPath = "";
        try {
            if (file != null) {

                String filename = file.getOriginalFilename();
                String filenamePrefix = filename.split("\\.")[0];


                if (StringUtils.isNoneBlank(filename)) {
                    //文件上传最终保存路径
                    finalPath = FILE_SPACE + uploadPathDB + "/" + filename;
                    //System.out.println(finalPath);
                    //数据库保存的路径
                    uploadPathDB += ("/" + filename);
                    coverPathDB = coverPathDB + "/" + filenamePrefix + ".jpg";
                    //System.out.println(uploadPathDB);
                    File outFile = new File(finalPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fo = new FileOutputStream(outFile);
                    is = file.getInputStream();
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
        //判断bgmId是否为空 否则合并
        if (StringUtils.isNotBlank(bgmId)) {
            Bgm bgm = bgmService.queryBgmById(bgmId);
            String mp3InputPath = FILE_SPACE + bgm.getPath();

            String inputPath = finalPath;
            MergeVideoMp3 tool = new MergeVideoMp3(ffmpegEXE);
            String videoOutputName = UUID.randomUUID().toString() + ".mp4";
            uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
            finalPath = FILE_SPACE + uploadPathDB;
            tool.convertor(inputPath, mp3InputPath, videoSeconds, finalPath);
        }

        System.out.println("uploadPathDB:" + uploadPathDB);
        System.out.println("finalPath:" + finalPath);

        //对视频截图
        FetchVideoCover videoInfo = new FetchVideoCover(ffmpegEXE);

        videoInfo.getCover(finalPath, FILE_SPACE + coverPathDB);


        //保存视频信息到数据库
        Videos video = new Videos();
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setVideoSeconds((float) videoSeconds);
        video.setVideoWidth(videoWidth);
        video.setVideoHeight(videoHeight);
        video.setVideoDesc(desc);
        video.setCoverPath(coverPathDB);
        video.setVideoPath(uploadPathDB);
        video.setStatus(VideoStatusEnum.SUCCESS.value);
        video.setCreateTime(new Date());

        String id = videoService.saveVidoe(video);
        return IMoocJSONResult.ok(id);
    }

    @ApiOperation(value = "上传封面", notes = "上传封面的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoId", value = "视频主键id", required = true,
                    dataType = "String", paramType = "form")
    })
    @PostMapping(value = "/uploadCover", headers = "content-type=multipart/form-data")
    public IMoocJSONResult uploadCover(String userId,
                                       String videoId,
                                       @ApiParam(value = "视频封面", required = true)
                                               MultipartFile file) throws Exception {

        if (StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("视频主键id和用户id不能为空...");
        }

        // 文件保存的命名空间
//		String fileSpace = "C:/imooc_videos_dev";
        // 保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        // 文件上传的最终保存路径
        String finalCoverPath = "";
        try {
            if (file != null) {

                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {

                    finalCoverPath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    // 设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);

                    File outFile = new File(finalCoverPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }

            } else {
                return IMoocJSONResult.errorMsg("上传出错...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错...");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        System.out.println("uploadPathDB:" + uploadPathDB);
        videoService.updateVideo(videoId, uploadPathDB);

        return IMoocJSONResult.ok();
    }

    @PostMapping(value = "/showAll")
    public IMoocJSONResult uploadCover(Integer page) throws Exception {

        if (page == null) {
            page = 1;
        }
        PagedResult videos = videoService.getAllVideos(page, PAGE_SIZE);
        return IMoocJSONResult.ok(videos);
    }
}
