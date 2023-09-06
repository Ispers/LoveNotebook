package com.example.lovenotebook_back.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.lovenotebook_back.controller.vo.FoodCategoryVO;
import com.example.lovenotebook_back.entity.FoodCategory;
import com.example.lovenotebook_back.service.FoodCategoryService;
import com.example.lovenotebook_back.utils.Info;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/foodCategory")
@Api(tags = "食物类别相关接口", description = "请求接口必须携带token")
public class FoodCategoryController {

    @Autowired
    private FoodCategoryService foodCategoryService;

    /**
     * 按餐厅 ID 获取食物类别
     *
     * @param restaurantId: 餐馆ID
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/23 22:09
     */
    @ApiImplicitParam(name = "restaurantId", value = "餐馆ID", dataType = "int", required = true)
    @ApiOperation("按餐厅 ID 获取食物类别")
    @GetMapping("/getFoodCategoryByRestaurantId/{restaurantId}")
    public Info GetFoodCategoryByRestaurantId(@PathVariable int restaurantId) {
        LambdaQueryWrapper<FoodCategory> lqw = new LambdaQueryWrapper<>();
        lqw.eq(FoodCategory::getRestaurantId, restaurantId);
        List<FoodCategory> foodCategoryList = foodCategoryService.getBaseMapper().selectList(lqw);

        List<FoodCategoryVO> foodCategoryVOList = new ArrayList<>();

        // 处理返回前端数据
        for (int i = 0; i < foodCategoryList.size(); i++) {
            FoodCategoryVO bo = new FoodCategoryVO();

            BeanUtils.copyProperties(foodCategoryList.get(i), bo);
            bo.setFoodCategoryIsCheck(i == 0);

            foodCategoryVOList.add(bo);
        }
        System.out.println(Arrays.toString(foodCategoryVOList.toArray()));
        return new Info(true, foodCategoryVOList, "success");
    }

    /**
     * 按餐厅ID添加食物类别
     *
     * @param foodCategory: 食品类别
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/25 20:22
     */
    @ApiImplicitParam(name = "foodCategory", value = "食品类别", dataType = "食物类别表", required = true)
    @ApiOperation("按餐厅ID添加食物类别")
    @PostMapping("/addFoodCategory")
    public Info AddFoodCategory(@RequestBody FoodCategory foodCategory) {
        boolean b = foodCategoryService.save(foodCategory);
        if (b) {
            return new Info(true, "添加成功");
        }
        return new Info(false, "添加失败");
    }

    /**
     * 按食品类别ID删除食物类别
     *
     * @param foodCategoryId: 食品类别ID
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/25 20:22
     */
    @ApiImplicitParam(name = "foodCategoryId", value = "食品类别ID", dataType = "integer", required = true)
    @ApiOperation("按食品类别ID删除食物类别")
    @PostMapping("/deleteFoodCategory/{foodCategoryId}")
    public Info DeleteFoodCategory(@PathVariable Integer foodCategoryId) {
        boolean b = foodCategoryService.removeById(foodCategoryId);
        if (b) {
            return new Info(true, "删除成功");
        }
        return new Info(false, "删除失败");
    }

    /**
     * 更新食品类别
     *
     * @param foodCategory: 食品类别
     * @return com.example.lovenotebook_back.utils.Info
     * @author sun0316
     * @date 2023/5/25 20:27
     */
    @ApiImplicitParam(name = "foodCategory", value = "食品类别", dataType = "食物类别表", required = true)
    @ApiOperation("更新食品类别")
    @PostMapping("/updateFoodCategory")
    public Info UpdateFoodCategory(@RequestBody FoodCategory foodCategory) {
        boolean b = foodCategoryService.updateById(foodCategory);
        if (b) {
            return new Info(true, "更新成功");
        }
        return new Info(false, "更新失败");
    }
}
