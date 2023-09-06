package com.example.lovenotebook_back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lovenotebook_back.entity.User;

public interface UserService extends IService<User> {
    /**
     * 根据openid查询用户信息
     *
     * @param openid: 微信OpenId
     * @return com.example.lovenotebook_back.entity.User
     * @author sun0316
     * @date 2023/5/16 12:13
     */
    User selectOneByOpenId(String openid);

    /**
     * 添加微信用户
     *
     * @param user :  User对象
     * @return void
     * @author sun0316
     * @date 2023/5/16 12:13
     */
    boolean addUser(User user);
}
