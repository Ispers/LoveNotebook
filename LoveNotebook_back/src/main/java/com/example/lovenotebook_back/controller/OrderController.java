package com.example.lovenotebook_back.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.lovenotebook_back.common.Constants;
import com.example.lovenotebook_back.common.OrderStatusEnum;
import com.example.lovenotebook_back.controller.vo.FoodVO;
import com.example.lovenotebook_back.controller.vo.OrderCommentVO;
import com.example.lovenotebook_back.controller.vo.OrderVO;
import com.example.lovenotebook_back.entity.*;
import com.example.lovenotebook_back.service.*;
import com.example.lovenotebook_back.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/order")
@Api(tags = "订单相关接口", description = "请求接口必须携带token")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private FoodService foodService;

    @Autowired
    private CartService cartService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private OrderLikeService orderLikeService;

    @Autowired
    private OrderCommentService orderCommentService;

    @Autowired
    private OrderCommentLikeService orderCommentLikeService;

    /**
     * 用户提交订单
     *
     * @param orderVO: 订单vo
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/28 19:21
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "orderVO", value = "订单VO", dataType = "订单VO", required = true)
    })
    @ApiOperation(value = "用户提交订单", notes = "用户提交订单")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/submitOrder")
    public Info SubmitOrder(@RequestHeader String token, @RequestBody OrderVO orderVO) {
        try {
            User jsonUser = JwtHelper.getUserByToken(token);

            // 雪花算法生成订单编号（订单ID）
            SnowflakeIdUtils idWorker = new SnowflakeIdUtils(1, 2);
            long orderId = idWorker.nextId();

            Order order = new Order();
            OrderDetail orderDetail = new OrderDetail();

            // 写订单表
            order.setOrderId(orderId);
            order.setOrderUserId(orderVO.getOrderUserId());
            order.setOrderTotalFoodHeartCount(orderVO.getOrderTotalFoodHeartCount());
            order.setOrderTotalFoodTypeCount(orderVO.getOrderTotalFoodTypeCount());
            order.setOrderNote(orderVO.getOrderNote());
            order.setOrderTitle(orderVO.getOrderTitle());

            order.setOrderStatus(OrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            order.setOrderIsDelete(0);
            order.setOrderIsShare(0);
            order.setOrderBossIsRead(0);

            String date = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
            order.setOrderCreateTime(DateUtils.toDate(date, "yyyy-MM-dd HH:mm:ss"));
            orderService.save(order);

            // 写订单详情表
            for (FoodVO foodVO : orderVO.getFoodList()) {
                orderDetail.setOrderId(orderId);
                orderDetail.setOrderDetailsFoodId(foodVO.getFoodId());
                orderDetail.setOrderDetailsFoodName(foodVO.getFoodName());
                orderDetail.setOrderDetailsFoodImg(foodVO.getFoodImg());
                orderDetail.setOrderDetailsFoodHeartCount(foodVO.getFoodHeartCount());
                orderDetail.setOrderDetailsFoodCount(foodVO.getCartCount());
                orderDetail.setOrderDetailsCreateTime(DateUtils.toDate(date, "yyyy-MM-dd HH:mm:ss"));
                orderDetailService.save(orderDetail);
            }

            // 扣除当前用户对应的爱心数量
            LambdaQueryWrapper<User> userlqw = new LambdaQueryWrapper<>();
            userlqw.eq(User::getUserId, orderVO.getOrderUserId());

            User user = new User();
            user.setUserHeartCount(jsonUser.getUserHeartCount() - orderVO.getOrderTotalFoodHeartCount());
            userService.update(user, userlqw);

            // 增加当前用户伴侣对应的爱心数量
            LambdaQueryWrapper<User> loverlqw = new LambdaQueryWrapper<>();
            loverlqw.eq(User::getUserId, jsonUser.getLover().getUserId());

            User lover = new User();
            lover.setUserHeartCount(jsonUser.getLover().getUserHeartCount() + orderVO.getOrderTotalFoodHeartCount());
            userService.update(lover, loverlqw);

            // 增加对应食品销量
            for (FoodVO foodVO : orderVO.getFoodList()) {
                LambdaQueryWrapper<Food> foodlqw = new LambdaQueryWrapper<>();
                foodlqw.eq(Food::getFoodId, foodVO.getFoodId());

                Food food = new Food();
                food.setFoodSellCount(foodVO.getFoodSellCount() + foodVO.getCartCount());
                foodService.update(food, foodlqw);
            }

            // 清空购物车
            LambdaQueryWrapper<Cart> cartlqw = new LambdaQueryWrapper<>();
            cartlqw.eq(Cart::getCartUserId, jsonUser.getUserId());

            cartService.remove(cartlqw);

            // 下单成功，重新下发token
            jsonUser.setUserHeartCount(jsonUser.getUserHeartCount() - orderVO.getOrderTotalFoodHeartCount());
            String newToken = JwtHelper.generateToken("user", jsonUser);
            JedisUtils.set("token:" + user.getUserId(), newToken, Constants.REDIS_CACHE_SECONDS);

            HashMap<String, Object> map = new HashMap<>();
            map.put("token", newToken);

            return new Info(true, map, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Info(false, "下单失败");
        }
    }

    /**
     * 检查 并 获取老板未读订单数量
     *
     * @param token: token
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/29 19:51
     */
    @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true)
    @ApiOperation(value = "检查并获取老板未读订单数量", notes = "是顾客返回0")
    @GetMapping("/checkAndGetBossOrderNotReadNum")
    public Info CheckAndGetBossOrderNotReadNum(@RequestHeader String token) {
        User user = JwtHelper.getUserByToken(token);

        Restaurant restaurant = null;

        LambdaQueryWrapper<Restaurant> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Restaurant::getRestaurantBossId, user.getUserId());

        restaurant = restaurantService.getOne(lqw);
        if (restaurant == null) {
            // 当前用户是---顾客
            return new Info(true, 0);
        } else {
            // 当前用户是---老板
            Long loverId = user.getLover().getUserId();
            LambdaQueryWrapper<Order> orderlqw = new LambdaQueryWrapper<>();
            orderlqw.eq(Order::getOrderUserId, loverId);
            orderlqw.eq(Order::getOrderBossIsRead, 0);
            orderlqw.eq(Order::getOrderIsDelete, 0);

            List<Order> orders = orderService.getBaseMapper().selectList(orderlqw);
            return new Info(true, orders.size());
        }
    }

    /**
     * 按用户 ID 获取订单和订单详细信息
     *
     * @param token: token
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/31 14:14
     */
    @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true)
    @ApiOperation(value = "按用户 ID 获取订单和订单详细信息", notes = "按用户 ID 获取订单和订单详细信息")
    @GetMapping("/getOrderAndOrderDetailsByUserId")
    public Info GetOrderAndOrderDetailsByUserId(@RequestHeader String token) {
        User user = JwtHelper.getUserByToken(token);

        if (user.getUserRestaurantLever() == 0) {
            // 当前用户是---顾客
            LambdaQueryWrapper<Order> orderlqw = new LambdaQueryWrapper<>();
            orderlqw.eq(Order::getOrderUserId, user.getUserId());
            orderlqw.eq(Order::getOrderIsDelete, 0);
            orderlqw.orderByDesc(Order::getOrderCreateTime);
            List<Order> orderList = orderService.getBaseMapper().selectList(orderlqw);

            for (Order order : orderList) {
                LambdaQueryWrapper<OrderDetail> orderDetaillqw = new LambdaQueryWrapper<>();
                orderDetaillqw.eq(OrderDetail::getOrderId, order.getOrderId());
                List<OrderDetail> orderDetailList = orderDetailService.getBaseMapper().selectList(orderDetaillqw);

                order.setOrderDetails(orderDetailList);
            }

            return new Info(true, orderList);
        } else {
            // 当前用户是---老板
            LambdaQueryWrapper<Order> orderlqw = new LambdaQueryWrapper<>();
            orderlqw.eq(Order::getOrderUserId, user.getLover().getUserId());
            orderlqw.eq(Order::getOrderIsDelete, 0);
            orderlqw.orderByDesc(Order::getOrderCreateTime);
            List<Order> orderList = orderService.getBaseMapper().selectList(orderlqw);

            for (Order order : orderList) {
                LambdaQueryWrapper<OrderDetail> orderDetaillqw = new LambdaQueryWrapper<>();
                orderDetaillqw.eq(OrderDetail::getOrderId, order.getOrderId());
                List<OrderDetail> orderDetailList = orderDetailService.getBaseMapper().selectList(orderDetaillqw);

                order.setOrderDetails(orderDetailList);
            }

            return new Info(true, orderList);
        }
    }

    /**
     * 按订单 ID 获取订单和订单详细信息
     *
     * @param orderId: 订单id
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/31 17:44
     */
    @ApiImplicitParam(name = "orderId", value = "订单id", dataType = "long", required = true)
    @ApiOperation(value = "按订单 ID 获取订单和订单详细信息", notes = "按订单 ID 获取订单和订单详细信息")
    @GetMapping("/getOrderAndOrderDetailsByOrderId/{orderId}")
    public Info GetOrderAndOrderDetailsByOrderId(@PathVariable Long orderId) {
        LambdaQueryWrapper<Order> orderlqw = new LambdaQueryWrapper<>();
        orderlqw.eq(Order::getOrderId, orderId);
        Order order = orderService.getOne(orderlqw);

        LambdaQueryWrapper<OrderDetail> orderDetaillqw = new LambdaQueryWrapper<>();
        orderDetaillqw.eq(OrderDetail::getOrderId, orderId);
        List<OrderDetail> orderDetailList = orderDetailService.getBaseMapper().selectList(orderDetaillqw);

        order.setOrderDetails(orderDetailList);

        return new Info(true, order);
    }

    /**
     * 老板读订单
     *
     * @param orderId: 订单id
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/6/1 16:12
     */
    @ApiImplicitParam(name = "orderId", value = "订单id", dataType = "long", required = true)
    @ApiOperation(value = "老板读订单", notes = "老板读订单")
    @PostMapping("/bossReadOrder/{orderId}")
    public Info BossReadOrder(@PathVariable Long orderId) {
        LambdaQueryWrapper<Order> orderlqw = new LambdaQueryWrapper<>();
        orderlqw.eq(Order::getOrderId, orderId);

        Order order = new Order();
        order.setOrderBossIsRead(1);
        boolean b = orderService.update(order, orderlqw);
        if (b)
            return new Info(true, "修改成功");

        return new Info(false, "修改失败");
    }

    /**
     * 按订单 ID 删除订单
     *
     * @param orderId: 订单id
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/6/1 16:11
     */
    @ApiImplicitParam(name = "orderId", value = "订单id", dataType = "long", required = true)
    @ApiOperation(value = "按订单ID删除订单", notes = "按订单ID删除订单")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/deleteOrderByOrderId/{orderId}")
    public Info DeleteOrderByOrderId(@PathVariable Long orderId) {
        try {
            LambdaQueryWrapper<OrderCommentLike> orderCommentLikelqw = new LambdaQueryWrapper<>();
            orderCommentLikelqw.eq(OrderCommentLike::getOrderId, orderId);
            orderCommentLikeService.remove(orderCommentLikelqw);

            LambdaQueryWrapper<OrderComment> orderCommentlqw = new LambdaQueryWrapper<>();
            orderCommentlqw.eq(OrderComment::getOrderId, orderId);
            orderCommentService.remove(orderCommentlqw);

            LambdaQueryWrapper<OrderLike> orderLikelqw = new LambdaQueryWrapper<>();
            orderLikelqw.eq(OrderLike::getOrderId, orderId);
            orderLikeService.remove(orderLikelqw);

            LambdaQueryWrapper<OrderDetail> orderDetaillqw = new LambdaQueryWrapper<>();
            orderDetaillqw.eq(OrderDetail::getOrderId, orderId);
            orderDetailService.remove(orderDetaillqw);

            orderService.removeById(orderId);

            return new Info(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Info(false, "删除失败");
        }
    }

    /**
     * 取消分享订单
     *
     * @param orderId: 订单ID
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/6/9 13:46
     */
    @ApiImplicitParam(name = "orderId", value = "订单id", dataType = "long", required = true)
    @ApiOperation(value = "取消分享订单", notes = "取消分享订单")
    @PostMapping("/cancelShareOrder/{orderId}")
    public Info CancelShareOrder(@PathVariable Long orderId) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderIsShare(0);
        boolean b = orderService.updateById(order);
        if (b) {
            return new Info(true, "取消分享成功");
        }
        return new Info(false, "取消分享失败");
    }

    /**
     * 分享订单
     *
     * @param orderId: 订单ID
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/6/9 13:46
     */
    @ApiImplicitParam(name = "orderId", value = "订单id", dataType = "long", required = true)
    @ApiOperation(value = "分享订单", notes = "分享订单")
    @PostMapping("/shareOrder/{orderId}")
    public Info ShareOrder(@PathVariable Long orderId) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderIsShare(1);
        boolean b = orderService.updateById(order);
        if (b) {
            return new Info(true, "分享成功");
        }
        return new Info(false, "分享失败");
    }

    /**
     * 获取全部订单---按时间降序
     *
     * @param pageNum: 页码
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/6/1 17:51
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "int", required = true)
    })
    @ApiOperation(value = "获取全部订单---按时间降序", notes = "获取全部订单---按时间降序")
    @GetMapping("/getAllOrderTimePage/{pageNum}")
    public Info GetAllOrderTimePage(@RequestHeader String token, @PathVariable int pageNum) {
        User jsonUser = JwtHelper.getUserByToken(token);

        List<Order> orders = orderService.getAllOrderByCreateTime(pageNum, Constants.SQUARE_ORDERS_PAGE_LIMIT);
        Restaurant restaurant = null;

        if (orders.size() == 0) {
            return new Info(true, null);
        }

        for (Order order : orders) {
            LambdaQueryWrapper<OrderDetail> odlqw = new LambdaQueryWrapper<>();
            odlqw.eq(OrderDetail::getOrderId, order.getOrderId());
            List<OrderDetail> orderDetailList = orderDetailService.getBaseMapper().selectList(odlqw);

            LambdaQueryWrapper<Restaurant> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Restaurant::getRestaurantBossId, order.getOrderUserId());

            restaurant = restaurantService.getOne(lqw);
            if (restaurant == null) {
                LambdaQueryWrapper<Restaurant> rlqw = new LambdaQueryWrapper<>();
                rlqw.eq(Restaurant::getRestaurantCustomerId, order.getOrderUserId());

                restaurant = restaurantService.getOne(rlqw);
            }

            LambdaQueryWrapper<OrderLike> orderLikelqw = new LambdaQueryWrapper<>();
            orderLikelqw.eq(OrderLike::getOrderId, order.getOrderId());
            orderLikelqw.eq(OrderLike::getOrderLikeUserId, jsonUser.getUserId());

            Integer count = orderLikeService.getBaseMapper().selectCount(orderLikelqw);
            if (count == null || count == 0) {
                order.setUserIsLike(0);
            } else {
                order.setUserIsLike(1);
            }

            User user = userService.getById(order.getOrderUserId());

            order.setUserInfo(user);
            order.setRestaurantName(restaurant.getRestaurantName());
            order.setOrderDetails(orderDetailList);
        }

        return new Info(true, orders);
    }

    /**
     * 获取全部订单---按点赞量降序
     *
     * @param pageNum: 页码
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/6/1 17:51
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "int", required = true)
    })
    @ApiOperation(value = "获取全部订单---按点赞量降序", notes = "获取全部订单---按点赞量降序")
    @GetMapping("/getAllOrderHotPage/{pageNum}")
    public Info GetAllOrderHotPage(@RequestHeader String token, @PathVariable int pageNum) {
        User jsonUser = JwtHelper.getUserByToken(token);

        List<Order> orders = orderService.getAllOrderByLikeCount(pageNum, Constants.SQUARE_ORDERS_PAGE_LIMIT);
        Restaurant restaurant = null;

        if (orders.size() == 0) {
            return new Info(true, null);
        }

        for (Order order : orders) {
            LambdaQueryWrapper<OrderDetail> odlqw = new LambdaQueryWrapper<>();
            odlqw.eq(OrderDetail::getOrderId, order.getOrderId());
            List<OrderDetail> orderDetailList = orderDetailService.getBaseMapper().selectList(odlqw);

            LambdaQueryWrapper<Restaurant> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Restaurant::getRestaurantBossId, order.getOrderUserId());

            restaurant = restaurantService.getOne(lqw);
            if (restaurant == null) {
                LambdaQueryWrapper<Restaurant> rlqw = new LambdaQueryWrapper<>();
                rlqw.eq(Restaurant::getRestaurantCustomerId, order.getOrderUserId());

                restaurant = restaurantService.getOne(rlqw);
            }

            LambdaQueryWrapper<OrderLike> orderLikelqw = new LambdaQueryWrapper<>();
            orderLikelqw.eq(OrderLike::getOrderId, order.getOrderId());
            orderLikelqw.eq(OrderLike::getOrderLikeUserId, jsonUser.getUserId());

            Integer count = orderLikeService.getBaseMapper().selectCount(orderLikelqw);
            if (count == null || count == 0) {
                order.setUserIsLike(0);
            } else {
                order.setUserIsLike(1);
            }

            User user = userService.getById(order.getOrderUserId());

            order.setUserInfo(user);
            order.setRestaurantName(restaurant.getRestaurantName());
            order.setOrderDetails(orderDetailList);
        }

        return new Info(true, orders);
    }

    /**
     * 用户点赞订单
     *
     * @param token:   用户token
     * @param orderId: 订单id
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/6/4 14:17
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "orderId", value = "订单id", dataType = "long", required = true)
    })
    @ApiOperation(value = "用户点赞订单", notes = "用户点赞订单")
    @PostMapping("/likeOrder/{orderId}")
    public Info LikeOrder(@RequestHeader String token, @PathVariable Long orderId) {
        User user = JwtHelper.getUserByToken(token);

        OrderLike orderLike = new OrderLike();
        orderLike.setOrderId(orderId);
        orderLike.setOrderLikeUserId(user.getUserId());

        boolean b = orderLikeService.save(orderLike);

        if (b) {
            return new Info(true, "用户点赞成功");
        }
        return new Info(false, "用户点赞失败");
    }

    /**
     * 用户取消点赞订单
     *
     * @param token:   用户token
     * @param orderId: 订单id
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/6/4 14:17
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "orderId", value = "订单id", dataType = "long", required = true)
    })
    @ApiOperation(value = "用户取消点赞订单", notes = "用户取消点赞订单")
    @PostMapping("/cancelLikeOrder/{orderId}")
    public Info CancelLikeOrder(@RequestHeader String token, @PathVariable Long orderId) {
        User user = JwtHelper.getUserByToken(token);

        LambdaQueryWrapper<OrderLike> orderLikelqw = new LambdaQueryWrapper<>();
        orderLikelqw.eq(OrderLike::getOrderId, orderId);
        orderLikelqw.eq(OrderLike::getOrderLikeUserId, user.getUserId());

        boolean b = orderLikeService.remove(orderLikelqw);

        if (b) {
            return new Info(true, "用户取消点赞成功");
        }
        return new Info(false, "用户取消点赞失败");
    }

    /**
     * 获取订单评论信息
     *
     * @param orderId: 订单ID
     * @param pageNum: 页码
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/6/7 16:17
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "orderId", value = "订单id", dataType = "long", required = true),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "int", required = true)
    })
    @ApiOperation(value = "获取订单评论信息", notes = "获取订单评论信息")
    @GetMapping("/getOrderCommentInfo/{orderId}/{pageNum}")
    public Info GetOrderCommentInfo(@RequestHeader String token, @PathVariable Long orderId, @PathVariable int pageNum) {
        User jsonUser = JwtHelper.getUserByToken(token);

        OrderCommentVO orderCommentVO = new OrderCommentVO();

        LambdaQueryWrapper<OrderComment> orderCommentlqw = new LambdaQueryWrapper<>();
        orderCommentlqw.eq(OrderComment::getOrderId, orderId);
        // 获取订单评论数量
        int orderCommentCount = orderCommentService.count(orderCommentlqw);
        orderCommentVO.setOrderCommentCount(orderCommentCount);

        // 分页查询订单评论信息
        List<OrderComment> orderCommentList = orderCommentService.getOrderCommentByOrderId(orderId, pageNum, Constants.SQUARE_ORDERS_PAGE_LIMIT);

        for (OrderComment orderComment : orderCommentList) {
            User user = userService.getById(orderComment.getOrderUserId());
            orderComment.setOrderCommentUserInfo(user);

            LambdaQueryWrapper<OrderCommentLike> orderCommentLikelqw = new LambdaQueryWrapper<>();
            orderCommentLikelqw.eq(OrderCommentLike::getOrderCommentId, orderComment.getOrderCommentId());
            orderCommentLikelqw.eq(OrderCommentLike::getOrderCommentLikeUserId, jsonUser.getUserId());

            int count = orderCommentLikeService.count(orderCommentLikelqw);

            if (count == 0) {
                orderComment.setUserIsLike(0);
            } else {
                orderComment.setUserIsLike(1);
            }
        }

        orderCommentVO.setOrderComments(orderCommentList);
        return new Info(true, orderCommentVO);
    }

    /**
     * 添加订单评论
     *
     * @param token:        token
     * @param orderComment: 订单评论对象
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/6/7 18:30
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "orderComment", value = "订单评论对象", dataType = "订单评论表", required = true)
    })
    @ApiOperation(value = "添加订单评论", notes = "添加订单评论")
    @PostMapping("/addOrderCommentInfo")
    public Info AddOrderCommentInfo(@RequestHeader String token, @RequestBody OrderComment orderComment) throws ParseException {
        User user = JwtHelper.getUserByToken(token);

        orderComment.setOrderUserId(user.getUserId());

        String date = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        orderComment.setOrderCommentCreateTime(DateUtils.toDate(date, "yyyy-MM-dd HH:mm:ss"));

        boolean b = orderCommentService.save(orderComment);
        if (b) {
            return new Info(true, "添加成功");
        }
        return new Info(false, "添加失败");
    }

    /**
     * 用户点赞订单评论
     *
     * @param token:          用户token
     * @param orderCommentId: 订单评论id
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/6/4 14:17
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "orderCommentId", value = "订单评论id", dataType = "int", required = true),
            @ApiImplicitParam(name = "orderId", value = "订单id", dataType = "long", required = true)
    })
    @ApiOperation(value = "用户点赞订单评论", notes = "用户点赞订单评论")
    @PostMapping("/likeOrderComment/{orderCommentId}/{orderId}")
    public Info LikeOrderComment(@RequestHeader String token, @PathVariable int orderCommentId, @PathVariable Long orderId) {
        User user = JwtHelper.getUserByToken(token);

        OrderCommentLike orderCommentLike = new OrderCommentLike();
        orderCommentLike.setOrderCommentId(orderCommentId);
        orderCommentLike.setOrderCommentLikeUserId(user.getUserId());
        orderCommentLike.setOrderId(orderId);

        boolean b = orderCommentLikeService.save(orderCommentLike);

        if (b) {
            return new Info(true, "用户点赞成功");
        }
        return new Info(false, "用户点赞失败");
    }

    /**
     * 用户取消点赞订单评论
     *
     * @param token:          用户token
     * @param orderCommentId: 订单评论id
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/6/4 14:17
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "orderCommentId", value = "订单评论id", dataType = "int", required = true)
    })
    @ApiOperation(value = "用户取消点赞订单评论", notes = "用户取消点赞订单评论")
    @PostMapping("/cancelLikeOrderComment/{orderCommentId}")
    public Info CancelLikeOrderComment(@RequestHeader String token, @PathVariable int orderCommentId) {
        User user = JwtHelper.getUserByToken(token);

        LambdaQueryWrapper<OrderCommentLike> orderCommentLikelqw = new LambdaQueryWrapper<>();
        orderCommentLikelqw.eq(OrderCommentLike::getOrderCommentId, orderCommentId);
        orderCommentLikelqw.eq(OrderCommentLike::getOrderCommentLikeUserId, user.getUserId());

        boolean b = orderCommentLikeService.remove(orderCommentLikelqw);

        if (b) {
            return new Info(true, "用户取消点赞成功");
        }
        return new Info(false, "用户取消点赞失败");
    }

    /**
     * 按订单 ID 获取用户信息和餐厅信息
     *
     * @param orderId: 订单ID
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/6/11 18:12
     */
    @ApiImplicitParam(name = "orderId", value = "订单id", dataType = "long", required = true)
    @ApiOperation(value = "按订单ID获取用户信息和餐厅信息", notes = "按订单ID获取用户信息和餐厅信息")
    @GetMapping("/getUserInfoAndRestaurantInfoByOrderId/{orderId}")
    public Info GetUserInfoAndRestaurantInfoByOrderId(@PathVariable Long orderId) {
        Order order = orderService.getById(orderId);

        User user = userService.getById(order.getOrderUserId());
        if (user.getUserIsLove() == 1) {
            User lover = userService.getById(user.getUserLoveId());
            user.setLover(lover);
        }

        Restaurant restaurant = new Restaurant();

        LambdaQueryWrapper<Restaurant> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Restaurant::getRestaurantBossId, user.getUserId());
        restaurant = restaurantService.getOne(lqw);

        if (restaurant == null) {
            LambdaQueryWrapper<Restaurant> lqw1 = new LambdaQueryWrapper<>();
            lqw1.eq(Restaurant::getRestaurantCustomerId, user.getUserId());
            restaurant = restaurantService.getOne(lqw1);
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("restaurant", restaurant);

        return new Info(true, map);
    }
}
