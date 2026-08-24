package com.hhfindjob.shortlink.project.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhfindjob.shortlink.project.dao.entity.ShortLinkDO;
import com.hhfindjob.shortlink.project.dao.entity.ShortLinkGotoDO;
import com.hhfindjob.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import com.hhfindjob.shortlink.project.dao.mapper.ShortLinkMapper;
import com.hhfindjob.shortlink.project.dto.req.PageSelectRecycleReqDTO;
import com.hhfindjob.shortlink.project.dto.req.RecycleReqDTO;
import com.hhfindjob.shortlink.project.dto.resp.PageSelectRespDTO;
import com.hhfindjob.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.hhfindjob.shortlink.project.common.constant.RedisCacheConstant.SHORT_LINK_GOTO_KEY;

@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl
        extends ServiceImpl<ShortLinkMapper, ShortLinkDO>
        implements RecycleBinService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RBloomFilter<String> shortUriRegisterCachePenetrationBloomFilter;

    private final ShortLinkMapper shortLinkMapper;
    private final ShortLinkGotoMapper gotoMapper;

    @Override
    public Boolean saveRecycleBin(RecycleReqDTO dto) {
        LambdaQueryWrapper<ShortLinkDO> wrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, dto.getGid())
                .eq(ShortLinkDO::getFullShortUrl, dto.getFullShortUrl())
                .eq(ShortLinkDO::getEnableStatus,0)
                .eq(ShortLinkDO::getDelFlag,0);
        ShortLinkDO DO=new ShortLinkDO();
        DO.setEnableStatus(1);
        //删缓存
        //shortUriRegisterCachePenetrationBloomFilter.add(dto.getFullShortUrl());
        stringRedisTemplate.delete(
                SHORT_LINK_GOTO_KEY +dto.getFullShortUrl()
        );
        return 0 != baseMapper.update(DO, wrapper);
    }

    @Override
    public Boolean recoverRecycleBin(RecycleReqDTO dto) {
        LambdaQueryWrapper<ShortLinkDO> wrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, dto.getGid())
                .eq(ShortLinkDO::getFullShortUrl, dto.getFullShortUrl())
                .eq(ShortLinkDO::getEnableStatus,1)
                .eq(ShortLinkDO::getDelFlag,0);

        ShortLinkDO DO=new ShortLinkDO();
        DO.setEnableStatus(0);
        return 0 != baseMapper.update(DO, wrapper);
    }

    @Override
    @Transactional
    public Boolean removeRecycleBin(RecycleReqDTO dto) {
        LambdaQueryWrapper<ShortLinkDO> wrapper1 = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, dto.getGid())
                .eq(ShortLinkDO::getFullShortUrl, dto.getFullShortUrl())
                .eq(ShortLinkDO::getEnableStatus,1)
                .eq(ShortLinkDO::getDelFlag,0);

        LambdaQueryWrapper<ShortLinkGotoDO> wrapper2 = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                .eq(ShortLinkGotoDO::getFullShortUrl, dto.getFullShortUrl())
                .eq(ShortLinkGotoDO::getGid, dto.getGid());
        //TODO 目前想到要做的事:删link表数据，删goto表数据，删redis缓存
        shortLinkMapper.delete(wrapper1);
        gotoMapper.delete(wrapper2);
        //stringRedisTemplate.delete();
        //布隆过滤器好像没法从中删除，(回收站中不考虑软删除)
        return true;
    }

    @Override
    public IPage<PageSelectRespDTO> pageSelect(PageSelectRecycleReqDTO dto) {
        LambdaQueryWrapper<ShortLinkDO> wrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                //应该加上gid，防止全表扫描 .in(ShortLinkDO::getGid,dto.getGidList())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 1)
                .orderByDesc(ShortLinkDO::getCreateTime);

        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(dto, wrapper);
        IPage<PageSelectRespDTO> convert = resultPage.convert(
                e -> BeanUtil.toBean(e, PageSelectRespDTO.class)
        );
        return convert;
    }
}
