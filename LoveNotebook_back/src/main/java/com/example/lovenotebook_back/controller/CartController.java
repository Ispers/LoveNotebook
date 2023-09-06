package com.example.lovenotebook_back.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.lovenotebook_back.controller.vo.CartVO;
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
@RequestMapping("/api/cart")
@Api(tags = "购物车相关接口", description = "请求接口必须携带token")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private FoodService foodService;

    /**
     * 清空当前用户购物车
     *
     * @param token: 用户唯一凭证
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/24 16:18
     */
    @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true)
    @ApiOperation("清空当前用户购物车")
    @PostMapping("/deleteCart")
    public Info DeleteCart(@RequestHeader String token) {
        User user = JwtHelper.getUserByToken(token);

        LambdaQueryWrapper<Cart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Cart::getCartUserId, user.getUserId());
        boolean b = cartService.remove(lqw);

        if (b) {
            return new Info(true, "删除成功");
        }
        return new Info(false, "删除失败");
    }

    /**
     * 按用户 ID 获取购物车信息
     *
     * @param token: 用户唯一凭证
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/24 17:16
     */
    @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true)
    @ApiOperation("按用户 ID 获取购物车信息")
    @GetMapping("/getCartInfoByUserId")
    public Info GetCartInfoByUserId(@RequestHeader String token) {
        User user = JwtHelper.getUserByToken(token);

        LambdaQueryWrapper<Cart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Cart::getCartUserId, user.getUserId());

        List<Cart> carts = cartService.getBaseMapper().selectList(lqw);

        ArrayList<CartVO> cartVOList = new ArrayList<>();

        Integer totalHeartCount = 0;

        for (Cart cart : carts) {
            CartVO bo = new CartVO();

            BeanUtils.copyProperties(cart, bo);
            bo.setCartIsCheck(1);

            Food food = foodService.getById(cart.getCartFoodId());
            bo.setFood(food);

            totalHeartCount += cart.getCartFoodCount() * food.getFoodHeartCount();

            cartVOList.add(bo);
        }


        return new Info(true, cartVOList, totalHeartCount.toString());
    }

    /**
     * 食物加号按钮
     * --判断购物车是否存在该商品，存在数量+1，不存在添加到购物车
     *
     * @param token:  用户唯一凭证
     * @param foodId: 食物id
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/25 14:01
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "foodId", value = "食物id", dataType = "integer", required = true)
    })
    @ApiOperation(value = "食物加号按钮", notes = "判断购物车是否存在该商品，存在数量+1，不存在添加到购物车")
    @PostMapping("/cartAdd/{foodId}")
    public Info CartAdd(@RequestHeader String token, @PathVariable Integer foodId) {
        User user = JwtHelper.getUserByToken(token);

        Cart cart = null;

        LambdaQueryWrapper<Cart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Cart::getCartUserId, user.getUserId());
        lqw.eq(Cart::getCartFoodId, foodId);

        cart = cartService.getOne(lqw);

        if (cart != null) {
            cart.setCartFoodCount(cart.getCartFoodCount() + 1);
            boolean b = cartService.updateById(cart);
            if (b) {
                return new Info(true, "修改成功");
            }
            return new Info(false, "修改失败");
        }

        cart = new Cart();
        cart.setCartFoodId(foodId);
        cart.setCartUserId(user.getUserId());
        cart.setCartFoodCount(1);

        boolean b1 = cartService.save(cart);
        if (b1) {
            return new Info(true, "添加成功");
        }
        return new Info(false, "添加失败");
    }

    /**
     * 食物减号按钮
     * --判断购物车商品数量是否等于1，等于1将商品从购物车删除，不等于数量减1
     *
     * @param token:  用户唯一凭证
     * @param foodId: 食物id
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/25 14:02
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "foodId", value = "食物id", dataType = "integer", required = true)
    })
    @ApiOperation(value = "食物减号按钮", notes = "判断购物车商品数量是否等于1，等于1将商品从购物车删除，不等于数量减1")
    @PostMapping("/cartMinus/{foodId}")
    public Info CartMinus(@RequestHeader String token, @PathVariable Integer foodId) {
        User user = JwtHelper.getUserByToken(token);

        Cart cart = null;

        LambdaQueryWrapper<Cart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Cart::getCartUserId, user.getUserId());
        lqw.eq(Cart::getCartFoodId, foodId);

        cart = cartService.getOne(lqw);

        if (cart.getCartFoodCount() == 1) {
            LambdaQueryWrapper<Cart> lqw1 = new LambdaQueryWrapper<>();
            lqw1.eq(Cart::getCartUserId, user.getUserId());
            lqw1.eq(Cart::getCartFoodId, foodId);

            boolean b = cartService.remove(lqw);

            if (b) {
                return new Info(true, "删除成功");
            }
            return new Info(false, "删除失败");
        } else {
            cart.setCartFoodCount(cart.getCartFoodCount() - 1);
            boolean b = cartService.updateById(cart);
            if (b) {
                return new Info(true, "修改成功");
            }
            return new Info(false, "修改失败");
        }
    }

    /**
     * 根据食物ID删除当前用户购物车内容
     *
     * @param foodIds: 要删除的食物id数组
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/24 16:18
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "唯一凭证", dataType = "string", required = true),
            @ApiImplicitParam(name = "foodIds", value = "要删除的食物id数组", dataType = "ArrayList<Integer>", required = true)
    })
    @ApiOperation(value = "根据食物ID删除当前用户购物车内容", notes = "传递食物ID数组")
    @PostMapping("/deleteCartByIds")
    public Info DeleteCartByIds(@RequestHeader String token, @RequestBody ArrayList<Integer> foodIds) {
        User user = JwtHelper.getUserByToken(token);

        LambdaQueryWrapper<Cart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Cart::getCartUserId, user.getUserId());
        lqw.in(Cart::getCartFoodId, foodIds);

        boolean b = cartService.remove(lqw);

        if (b) {
            return new Info(true, "删除成功");
        }
        return new Info(false, "删除失败");
    }
}