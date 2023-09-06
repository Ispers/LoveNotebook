package com.example.lovenotebook_back.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lovenotebook_back.dao.OrderDetailDao;
import com.example.lovenotebook_back.entity.OrderDetail;
import com.example.lovenotebook_back.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailDao, OrderDetail> implements OrderDetailService {
}
