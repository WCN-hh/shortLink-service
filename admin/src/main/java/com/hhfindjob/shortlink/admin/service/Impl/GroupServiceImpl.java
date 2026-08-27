package com.hhfindjob.shortlink.admin.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhfindjob.shortlink.admin.common.biz.user.UserContext;
import com.hhfindjob.shortlink.admin.common.convention.exception.ClientException;
import com.hhfindjob.shortlink.admin.dao.entity.GroupDO;
import com.hhfindjob.shortlink.admin.dao.mapper.GroupMapper;
import com.hhfindjob.shortlink.admin.dto.request.group.GroupSortDTO;
import com.hhfindjob.shortlink.admin.dto.request.group.GroupUpdateRequestDTO;
import com.hhfindjob.shortlink.admin.dto.response.group.GroupResponseDTO;
import com.hhfindjob.shortlink.admin.remote.dto.ShortLinkRemoteService;
import com.hhfindjob.shortlink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.hhfindjob.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    ShortLinkRemoteService service=new ShortLinkRemoteService() {
    };

    private final RedissonClient redissonClient;

    @Value("${group.max-num}")
    private int groupMaxNum;

    @Override
    public Boolean saveGruop(String groupName) {
        return saveGruop(UserContext.getUsername(),groupName);
    }

    @Override
    public Boolean saveGruop(String userName, String groupName) {
        String redisson_lock_key="redisson:group:user" + userName;
        //达到上限不允许创建
        RLock rLock = redissonClient.getLock(redisson_lock_key);
        rLock.lock();
        try {
            LambdaQueryWrapper<GroupDO> count = Wrappers.lambdaQuery(GroupDO.class)
                    .eq(GroupDO::getUsername, userName)
                    .eq(GroupDO::getDelFlag,0);
            List<GroupDO> list = baseMapper.selectList(count);
            if (CollUtil.isNotEmpty(list) && list.size() >= groupMaxNum){
                throw new ClientException("最多允许创建"+groupMaxNum+"个分组");
            }

            String gid ;
            do {
                gid = RandomStringUtils.random(6, true, true);
                LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                        .eq(GroupDO::getGId, gid)
                        .eq(GroupDO::getUsername,userName);
                GroupDO groupDO = baseMapper.selectOne(wrapper);
                if (groupDO == null)
                    break;
            } while (true);

            GroupDO groupDO = GroupDO.builder()
                    .gId(gid)
                    .gName(groupName)
                    .username(userName)
                    .sortOrder(0)
                    .build();

            return baseMapper.insert(groupDO) == 1;
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public List<GroupResponseDTO> getGroup() {

        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag,0)
                .orderByDesc(GroupDO::getSortOrder,GroupDO::getUpdateTime);

        List<GroupDO> groupDOList = baseMapper.selectList(wrapper);
        List<GroupResponseDTO> respList = BeanUtil.copyToList(groupDOList, GroupResponseDTO.class);

        //下方操作为从数据库中查出对应组别短链接总数并添加进respList
        List<String> gids = respList.stream()
                .map(GroupResponseDTO::getGId)
                .toList();

        List<ShortLinkGroupCountQueryRespDTO> gidCounts = service.listGroupShortLinkCount(gids).getData();

        if (gidCounts == null){
            return null;
        }

        Map<String, Integer> map = gidCounts.stream().collect(Collectors.toMap(
                ShortLinkGroupCountQueryRespDTO::getGid,
                ShortLinkGroupCountQueryRespDTO::getShortLinkCount
        ));

        respList.forEach(
                e -> e.setShortLinkCount(map.get(e.getGId()))
        );

        return respList;
    }

    @Override
    public Boolean updateGroup(GroupUpdateRequestDTO dto) {
        String realName = UserContext.getUsername();
        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getGId, dto.getGId())
                .eq(GroupDO::getUsername, UserContext.getUsername());
        GroupDO groupDO = GroupDO.builder().gName(dto.getGName()).build();
        int update = baseMapper.update(groupDO, wrapper);
        return update == 1;
    }

    @Override
    @Transactional
    public Boolean sortGroup(List<GroupSortDTO> list) {
        try {
            list.forEach( o -> {
                GroupDO groupDO = GroupDO.builder().sortOrder(o.getSortOrder()).build();
                LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                        .eq(GroupDO::getDelFlag, 0)
                        .eq(GroupDO::getGId, o.getGId())
                        .eq(GroupDO::getUsername,UserContext.getUsername());
                baseMapper.update(groupDO,wrapper);
            });
        } catch (Exception e) {
            throw new ClientException("排序更新失败");
        }
        return true;
    }

    @Override
    public Boolean delete(String gid) {
        //软删除
        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGId, gid);
        GroupDO groupDO=GroupDO.builder().delFlag(1).build();
        int delete = baseMapper.update(groupDO,wrapper);
        return delete == 1;
    }

}
