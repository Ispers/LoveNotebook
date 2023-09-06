package com.example.lovenotebook_back.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.lovenotebook_back.entity.FoodCategory;
import com.example.lovenotebook_back.entity.Restaurant;
import com.example.lovenotebook_back.entity.User;
import com.example.lovenotebook_back.service.FoodCategoryService;
import com.example.lovenotebook_back.service.RestaurantService;
import com.example.lovenotebook_back.service.UserService;
import com.example.lovenotebook_back.utils.Info;
import com.example.lovenotebook_back.utils.JwtHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/restaurant")
@Api(tags = "餐厅相关接口", description = "请求接口必须携带token")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private FoodCategoryService foodCategoryService;

    @Autowired
    private UserService userService;

    /**
     * 检查用户是否创建餐厅 --如创建返回当前用户餐厅信息
     *
     * @param token: 用户唯一凭证
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/22 22:22
     */
    @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true)
    @ApiOperation(value = "检查用户是否创建餐厅", notes = "如创建返回当前用户餐厅信息")
    @GetMapping("/checkUserIsCreateRestaurant")
    public Info CheckUserIsCreateRestaurant(@RequestHeader String token) {
        User user = JwtHelper.getUserByToken(token);

        Restaurant restaurant = null;

        QueryWrapper<Restaurant> wrapper = new QueryWrapper<>();
        wrapper.eq("restaurant_boss_id", user.getUserId());

        restaurant = restaurantService.getOne(wrapper);
        if (restaurant == null) {
            QueryWrapper<Restaurant> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("restaurant_customer_id", user.getUserId());

            restaurant = restaurantService.getOne(wrapper1);

            if (restaurant == null) {
                return new Info(false, "当前用户未开店");
            } else {
                return new Info(true, restaurant);
            }

        }
        return new Info(true, restaurant);
    }

    /**
     * 更新餐厅名称
     *
     * @param restaurant: 餐馆对象
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/23 16:18
     */
    @ApiImplicitParam(name = "restaurant", value = "餐厅对象", dataType = "餐厅表", required = true)
    @ApiOperation(value = "更新餐厅名称", notes = "更新餐厅名称")
    @PostMapping("/updateRestaurantName")
    public Info UpdateRestaurantName(@RequestBody Restaurant restaurant) throws IOException {

        boolean b = restaurantService.updateById(restaurant);
        if (b) {
            return new Info(true, "用户更新餐厅名称成功");
        } else {
            return new Info(false, "用户更新餐厅名称失败");
        }
    }

    /**
     * 创建餐厅
     *
     * @param restaurant: 餐馆对象
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/23 20:52
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "restaurant", value = "餐厅对象", dataType = "餐厅表", required = true)
    })
    @ApiOperation(value = "创建餐厅", notes = "创建餐厅")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/createRestaurant")
    public Info CreateRestaurant(@RequestHeader String token, @RequestBody Restaurant restaurant) {
        User user = JwtHelper.getUserByToken(token);

        Restaurant res = null;

        QueryWrapper<Restaurant> wrapper = new QueryWrapper<>();
        wrapper.eq("restaurant_boss_id", user.getUserId());

        res = restaurantService.getOne(wrapper);
        if (res == null) {
            QueryWrapper<Restaurant> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("restaurant_customer_id", user.getUserId());

            res = restaurantService.getOne(wrapper1);

            if (res == null) {
                restaurant.setRestaurantGrade(1);
                // 默认名称
                restaurant.setRestaurantName("这是个神秘的小店");
                boolean b = restaurantService.save(restaurant);
                if (b) {
                    LambdaQueryWrapper<Restaurant> lqw = new LambdaQueryWrapper<>();
                    lqw.eq(Restaurant::getRestaurantBossId, restaurant.getRestaurantBossId());
                    Restaurant rest = restaurantService.getOne(lqw);

                    LambdaQueryWrapper<User> userlqw = new LambdaQueryWrapper<>();
                    userlqw.eq(User::getUserId, restaurant.getRestaurantBossId());

                    User user1 = new User();
                    user1.setUserRestaurantLever(1);
                    userService.update(user1, userlqw);

                    LambdaQueryWrapper<User> userlqw1 = new LambdaQueryWrapper<>();
                    userlqw1.eq(User::getUserId, restaurant.getRestaurantCustomerId());

                    User user2 = new User();
                    user2.setUserRestaurantLever(0);
                    userService.update(user2, userlqw1);

                    // 提供两个默认类别
                    FoodCategory foodCategory = new FoodCategory();
                    foodCategory.setFoodCategoryName("默认分类");
                    foodCategory.setRestaurantId(rest.getRestaurantId());
                    foodCategoryService.save(foodCategory);

                    FoodCategory foodCategory1 = new FoodCategory();
                    foodCategory1.setFoodCategoryName("好吃的菜");
                    foodCategory1.setRestaurantId(rest.getRestaurantId());
                    foodCategoryService.save(foodCategory1);

                    return new Info(true, "创建成功");
                } else {
                    return new Info(false, "创建失败");
                }
            } else {
                return new Info(true, "伴侣已选择");
            }
        }
        return new Info(true, "伴侣已选择");
    }
}
