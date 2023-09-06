package com.example.lovenotebook_back.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.lovenotebook_back.entity.OrderComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderCommentDao extends BaseMapper<OrderComment> {

    @Select(" SELECT oc.*,ocl.likeCount " +
            " FROM tb_order_comments oc " +
            " LEFT OUTER JOIN(SELECT ocl.order_comment_id,count(ocl.order_comment_like_user_id) likeCount " +
            " FROM tb_order_comment_likes ocl " +
            " GROUP BY ocl.order_comment_id) ocl " +
            " ON oc.order_comment_id = ocl.order_comment_id " +
            " WHERE oc.order_id = #{orderId} " +
            " ORDER BY oc.order_comment_create_time DESC " +
            " LIMIT #{current}, #{pageSize}")
    List<OrderComment> selectOrderCommentByOrderId(Long orderId, int current, int pageSize);
}
