package com.example.lovenotebook_back.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lovenotebook_back.dao.OrderCommentLikeDao;
import com.example.lovenotebook_back.entity.OrderCommentLike;
import com.example.lovenotebook_back.service.OrderCommentLikeService;
import org.springframework.stereotype.Service;

@Service
public class OrderCommentLikeServiceImpl extends ServiceImpl<OrderCommentLikeDao, OrderCommentLike> implements OrderCommentLikeService {
}
