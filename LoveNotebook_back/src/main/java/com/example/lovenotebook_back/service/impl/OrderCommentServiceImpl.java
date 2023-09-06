package com.example.lovenotebook_back.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lovenotebook_back.dao.OrderCommentDao;
import com.example.lovenotebook_back.entity.OrderComment;
import com.example.lovenotebook_back.service.OrderCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderCommentServiceImpl extends ServiceImpl<OrderCommentDao, OrderComment> implements OrderCommentService {
    @Autowired
    private OrderCommentDao orderCommentDao;

    @Override
    public List<OrderComment> getOrderCommentByOrderId(Long orderId, int page, int pageSize) {
        return orderCommentDao.selectOrderCommentByOrderId(orderId, (page - 1) * pageSize, pageSize);
    }
}
