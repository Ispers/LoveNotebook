package com.example.lovenotebook_back.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.lovenotebook_back.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDao extends BaseMapper<Order> {

    @Select(" SELECT o.*, ol.likeCount " +
            " FROM tb_orders o " +
            " LEFT OUTER JOIN(SELECT ol.order_id,count(ol.order_like_user_id) likeCount " +
            " FROM tb_order_likes ol " +
            " GROUP BY ol.order_id) ol " +
            " ON o.order_id = ol.order_id " +
            " WHERE o.order_is_share = 1 " +
            " ORDER BY ol.likeCount DESC " +
            " LIMIT #{current}, #{pageSize}")
    List<Order> selectAllOrderByLikeCount(int current, int pageSize);


    @Select(" SELECT o.*,ol.likeCount " +
            " FROM tb_orders o " +
            " LEFT OUTER JOIN(SELECT ol.order_id,count(ol.order_like_user_id) likeCount " +
            " FROM tb_order_likes ol " +
            " GROUP BY ol.order_id) ol " +
            " ON o.order_id = ol.order_id " +
            " WHERE o.order_is_share = 1 " +
            " ORDER BY o.order_create_time DESC " +
            " LIMIT #{current}, #{pageSize}")
    List<Order> selectAllOrderByCreateTime(int current, int pageSize);
}
