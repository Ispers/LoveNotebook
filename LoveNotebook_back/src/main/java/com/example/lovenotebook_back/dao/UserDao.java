package com.example.lovenotebook_back.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.lovenotebook_back.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends BaseMapper<User> {
}
