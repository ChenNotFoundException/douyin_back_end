package com.cc.service.impl;

import com.cc.service.BgmService;
import com.imooc.mapper.BgmMapper;
import com.imooc.pojo.Bgm;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author chenchen
 * @date 2019/5/7 11:05
 * @Content:
 */
@Service
public class BgmServiceImpl implements BgmService {
    @Autowired
    private BgmMapper bgmMapper;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Bgm queryBgmById(String BgmId) {
        return bgmMapper.selectByPrimaryKey(BgmId);
    }

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List <Bgm> queryBgmList() {
        return bgmMapper.selectAll();
    }
}
