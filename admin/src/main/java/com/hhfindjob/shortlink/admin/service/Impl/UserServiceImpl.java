package com.hhfindjob.shortlink.admin.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhfindjob.shortlink.admin.common.constant.RedisCacheConstant;
import com.hhfindjob.shortlink.admin.common.convention.exception.ClientException;
import com.hhfindjob.shortlink.admin.dao.entity.UserDO;
import com.hhfindjob.shortlink.admin.dao.mapper.UserMapper;
import com.hhfindjob.shortlink.admin.dto.request.user.UserLoginRequestDTO;
import com.hhfindjob.shortlink.admin.dto.request.user.UserRegistRequestDTO;
import com.hhfindjob.shortlink.admin.dto.request.user.UserRequestDTO;
import com.hhfindjob.shortlink.admin.dto.response.user.UserLoginResponseDTO;
import com.hhfindjob.shortlink.admin.dto.response.user.UserResponseDTO;
import com.hhfindjob.shortlink.admin.service.GroupService;
import com.hhfindjob.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static com.hhfindjob.shortlink.admin.common.convention.errorcode.BaseErrorCode.USER_NAME_EXIST_ERROR;
import static com.hhfindjob.shortlink.admin.common.convention.errorcode.BaseErrorCode.USER_REGISTER_ERROR;

/**
 * 用户接口实现层
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final GroupService groupService;


    @Override
    public UserResponseDTO getUserByUsername(String username) {
        UserDO userDO = getUserDO(username);
        if (userDO == null){
            throw new ClientException("用户操作错误");
        }
        return UserResponseDTO.copy(userDO);
    }

    @Override
    public boolean nameNotUsed(String username) {
        //布隆过滤器方案
        return !userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    @Transactional
    public Boolean newUser(UserRegistRequestDTO dto) {
        String username = dto.getUsername();
        if (!nameNotUsed(username)) {
            throw new ClientException(USER_NAME_EXIST_ERROR);
        }
        RLock lock = redissonClient.getLock(
                RedisCacheConstant.LOCK_USER_REGISTER_LOCK + username
        );
        if (! lock.tryLock()) {
            throw new ClientException(USER_REGISTER_ERROR);
        }
        try {
            if (!save(BeanUtil.toBean(dto, UserDO.class))) {
                throw new ClientException(USER_REGISTER_ERROR);
            }
            userRegisterCachePenetrationBloomFilter.add(username);
            groupService.saveGruop(username,"默认分组");
        } finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public void update(UserRequestDTO dto) {
        //验证当前用户是否为登录用户

        LambdaQueryWrapper<UserDO> wrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, dto.getUsername());
        baseMapper.update(BeanUtil.toBean(dto,UserDO.class),wrapper);

    }

    @Override
    public UserLoginResponseDTO login(UserLoginRequestDTO dto) {
        //TODO 通过token查看是否已经登录

        LambdaQueryWrapper<UserDO> wrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, dto.getUsername())
                .eq(UserDO::getPassword, dto.getPassword())
                .eq(UserDO::getDelFlag,0);
        UserDO userDO = baseMapper.selectOne(wrapper);
        if (userDO == null){
            throw new ClientException("用户不存在");
        }

        Boolean hasLogin=stringRedisTemplate.opsForHash().hasKey("login_",userDO.getId()+"");
        if (hasLogin){
            throw new ClientException("用户已登录");
        }

        stringRedisTemplate.opsForHash().put(
                "login_"+userDO.getUsername(),
                userDO.getId()+"",
                JSON.toJSONString(userDO));
        stringRedisTemplate.expire("login_"+userDO.getUsername(),30,TimeUnit.DAYS);

        return new UserLoginResponseDTO(userDO.getId()+"");
    }

    @Override
    public Boolean loginCheck(String username,String token) {
        return  stringRedisTemplate.opsForHash().hasKey("login_"+username, token);
    }

    @Override
    public Boolean unlogin(String token) {
        return stringRedisTemplate.delete(RedisCacheConstant.LOGIN_USER_KEY + token);
    }

    private UserDO getUserDO(String username) {
        LambdaQueryWrapper<UserDO> wrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        return baseMapper.selectOne(wrapper);
    }
}
//    @Override
//    public UserActualResponseDTO userIssue(String username) {
//        UserDO userDO = getUserDO(username);
//        return UserActualResponseDTO.copy(userDO);
//    }