package com.example.lovenotebook_back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lovenotebook_back.entity.Order;

import java.util.List;

public interface OrderService extends IService<Order> {

    List<Order> getAllOrderByLikeCount(int page, int pageSize);

    List<Order> getAllOrderByCreateTime(int page, int pageSize);
}
