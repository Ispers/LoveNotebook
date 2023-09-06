package com.example.lovenotebook_back.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lovenotebook_back.dao.OrderDao;
import com.example.lovenotebook_back.entity.Order;
import com.example.lovenotebook_back.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderDao, Order> implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Override
    public List<Order> getAllOrderByLikeCount(int page, int pageSize) {
        return orderDao.selectAllOrderByLikeCount((page - 1) * pageSize, pageSize);
    }

    @Override
    public List<Order> getAllOrderByCreateTime(int page, int pageSize) {
        return orderDao.selectAllOrderByCreateTime((page - 1) * pageSize, pageSize);
    }
}
