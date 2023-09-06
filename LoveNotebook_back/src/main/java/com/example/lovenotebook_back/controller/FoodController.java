package com.example.lovenotebook_back.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.lovenotebook_back.controller.vo.FoodVO;
import com.example.lovenotebook_back.entity.Cart;
import com.example.lovenotebook_back.entity.Food;
import com.example.lovenotebook_back.entity.User;
import com.example.lovenotebook_back.service.CartService;
import com.example.lovenotebook_back.service.FoodService;
import com.example.lovenotebook_back.utils.Info;
import com.example.lovenotebook_back.utils.JwtHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/food")
@Api(tags = "食物相关接口", description = "请求接口必须携带token")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @Autowired
    private CartService cartService;

    /**
     * 通过餐厅ID获取食物信息
     *
     * @param restaurantId: 餐厅 ID
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/23 22:14
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "restaurantId", value = "餐厅 ID", dataType = "int", required = true)
    })
    @ApiOperation(value = "通过餐厅ID获取食物信息", notes = "通过餐厅ID获取食物信息")
    @GetMapping("/getFoodInfoByRestaurantId/{restaurantId}")
    public Info GetFoodInfoByRestaurantId(@RequestHeader String token, @PathVariable int restaurantId) {
        User user = JwtHelper.getUserByToken(token);

        LambdaQueryWrapper<Food> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Food::getRestaurantId, restaurantId);

        List<Food> foodList = foodService.getBaseMapper().selectList(lqw);

        // 处理返回前端数据
        List<FoodVO> foodVOList = new ArrayList<>();

        for (Food food : foodList) {
            FoodVO bo = new FoodVO();

            BeanUtils.copyProperties(food, bo);

            LambdaQueryWrapper<Cart> lqw1 = new LambdaQueryWrapper<>();
            lqw1.eq(Cart::getCartFoodId, food.getFoodId());
            lqw1.eq(Cart::getCartUserId, user.getUserId());

            Cart cart = cartService.getOne(lqw1);
            if (cart == null) {
                bo.setCartCount(0);
            } else {
                bo.setCartCount(cart.getCartFoodCount());
            }

            foodVOList.add(bo);
        }

        return new Info(true, foodVOList);
    }

    /**
     * 通过餐厅 ID 和食品类别 ID 获取食物信息
     *
     * @param restaurantId:   餐厅 ID
     * @param foodCategoryId: 食品类别 ID
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/23 22:14
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "restaurantId", value = "餐厅 ID", dataType = "int", required = true),
            @ApiImplicitParam(name = "foodCategoryId", value = "食品类别 ID", dataType = "int", required = true)
    })
    @ApiOperation(value = "通过餐厅 ID 和食品类别 ID 获取食物信息", notes = "需携带token")
    @GetMapping("/getFoodInfoByRestaurantIdAndFoodCategoryId/{restaurantId}/{foodCategoryId}")
    public Info GetFoodInfoByRestaurantIdAndFoodCategoryId(@PathVariable int restaurantId, @PathVariable int foodCategoryId) {
        LambdaQueryWrapper<Food> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Food::getRestaurantId, restaurantId);
        lqw.eq(Food::getFoodCategoryId, foodCategoryId);

        List<Food> foodList = foodService.getBaseMapper().selectList(lqw);

        // 处理返回前端数据
        List<FoodVO> foodVOList = new ArrayList<>();

        for (Food food : foodList) {
            FoodVO bo = new FoodVO();

            BeanUtils.copyProperties(food, bo);
            bo.setCartCount(0);

            foodVOList.add(bo);
        }
        return new Info(true, foodVOList);
    }

    /**
     * 添加食物
     *
     * @param food: 食物
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/25 20:42
     */
    @ApiImplicitParam(name = "food", value = "食物", dataType = "食物表", required = true)
    @ApiOperation(value = "添加食物", notes = "需携带token")
    @PostMapping("/addFood")
    public Info AddFood(@RequestBody Food food) {
        food.setFoodSellCount(0);
        boolean b = foodService.save(food);
        if (b) {
            return new Info(true, "添加成功");
        }
        return new Info(false, "添加失败");
    }

    /**
     * 修改食物
     *
     * @param food: 食物
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/25 20:42
     */
    @ApiImplicitParam(name = "food", value = "食物", dataType = "食物表", required = true)
    @ApiOperation(value = "修改食物", notes = "需携带token")
    @PostMapping("/updateFood")
    public Info UpdateFood(@RequestBody Food food) {
        boolean b = foodService.updateById(food);
        if (b) {
            return new Info(true, "更新成功");
        }
        return new Info(false, "更新失败");
    }

    /**
     * 删除食物
     *
     * @param foodId: 食物 id
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/25 20:42
     */
    @ApiImplicitParam(name = "foodId", value = "食物 id", dataType = "integer", required = true)
    @ApiOperation(value = "删除食物", notes = "需携带token")
    @PostMapping("/deleteFood/{foodId}")
    public Info DeleteFoodByFoodId(@PathVariable Integer foodId) {
        boolean b = foodService.removeById(foodId);
        if (b) {
            return new Info(true, "删除成功");
        }
        return new Info(false, "删除失败");
    }
}
