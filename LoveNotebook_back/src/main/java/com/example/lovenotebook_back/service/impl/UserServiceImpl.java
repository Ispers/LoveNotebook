package com.example.lovenotebook_back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lovenotebook_back.dao.UserDao;
import com.example.lovenotebook_back.entity.User;
import com.example.lovenotebook_back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User selectOneByOpenId(String openid) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUserOpenId, openid);
        return userDao.selectOne(userLambdaQueryWrapper);
    }

    @Override
    public boolean addUser(User user) {
        return save(user);
    }

}
