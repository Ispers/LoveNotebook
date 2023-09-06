package com.example.lovenotebook_back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lovenotebook_back.entity.OrderComment;

import java.util.List;

public interface OrderCommentService extends IService<OrderComment> {

    List<OrderComment> getOrderCommentByOrderId(Long orderId, int page, int pageSize);
}
