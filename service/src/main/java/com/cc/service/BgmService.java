package com.cc.service;

import com.imooc.pojo.Bgm;

import java.util.List;

/**
 * @author chenchen
 * @date 2019/5/7 11:03
 * @Content:
 */
public interface BgmService {

    List <Bgm> queryBgmList();

    Bgm queryBgmById(String BgmId);
}
