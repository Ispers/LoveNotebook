package com.example.lovenotebook_back.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lovenotebook_back.dao.OrderLikeDao;
import com.example.lovenotebook_back.entity.OrderLike;
import com.example.lovenotebook_back.service.OrderLikeService;
import org.springframework.stereotype.Service;

@Service
public class OrderLikeServiceImpl extends ServiceImpl<OrderLikeDao, OrderLike> implements OrderLikeService {
}
